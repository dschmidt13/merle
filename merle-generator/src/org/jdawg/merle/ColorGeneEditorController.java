/*
 * ColorGeneEditorController.java
 * 
 * Created on May 16, 2018
 */
package org.jdawg.merle;

import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.TimerTask;

import org.jdawg.fxcomponent.ColorSelector;
import org.jdawg.fxcontrol.NumericTextField;
import org.jdawg.util.FXUtils;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * ColorGeneEditorController
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class ColorGeneEditorController implements Initializable
{
	// Private data members.
	private ColorGene fieldGene;
	private GenerateCoatService fieldPatternService;
	private TimerTask fieldLastPatternAutoRefreshTask;

	// Injected FXML members.
	@FXML
	private TextField fieldName;

	@FXML
	private ColorSelector fieldColorSelector;

	@FXML
	private Slider fieldConversionProbability;

	@FXML
	private NumericTextField fieldConversionProbabilityText;

	@FXML
	private Slider fieldCoolingRate;

	@FXML
	private NumericTextField fieldCoolingRateText;

	@FXML
	private Slider fieldSignalStrength;

	@FXML
	private NumericTextField fieldSignalStrengthText;

	@FXML
	private Canvas fieldSamplePattern;


	public void acceptChanges( )
	{
		// TODO - This should probably just look up the ColorGene in the list and replace
		// it. Maybe we should be given the index when we start editing. Or return a
		// complete object and let the caller figure it out.
		setParams( fieldGene );
		fieldPatternService.cancel( );

	} // acceptChanges


	/**
	 * Regenerates the sample pattern image.
	 */
	public void actRefreshSamplePattern( )
	{
		// Calculate whether a dark or light background would be more appropriate
		// for the current color, and set it into the service.
		fieldPatternService.setBaseColor( choosePatternBackground( ) );
		fieldPatternService.setColorGenes( Arrays.asList( paramsToColorGene( ) ) );
		fieldPatternService.restart( );

	} // actRefreshSamplePattern


	private Color choosePatternBackground( )
	{
		final int LIGHT_THRESHOLD = 255 * 13 / 15;

		Color color = fieldColorSelector.getColor( );

		int lightCount = 0;
		if ( color.getRed( ) > LIGHT_THRESHOLD )
			lightCount++;
		if ( color.getGreen( ) > LIGHT_THRESHOLD )
			lightCount++;
		if ( color.getBlue( ) > LIGHT_THRESHOLD )
			lightCount++;

		// FIXME - Account for alpha.

		return ( lightCount > 1 ? Color.BLACK : Color.WHITE );

	} // choosePatternBackground


	public void destroy( )
	{
		fieldPatternService.cancel( );

	} // destroy


	public void handleCancel( ActionEvent event )
	{
		fieldPatternService.cancel( );

	} // handleCancel


	public void handleOk( ActionEvent event )
	{
		// Someday we might want to do validation or something here.
		fieldPatternService.cancel( );
		acceptChanges( );
		fieldColorSelector.select( );

	} // handleOk


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		// Initialize sliders & listeners.
		initService( );
		initPatternSliders( );

		// Set the UI defaults.
		setColorGene( null );

		// NOTE: Don't refresh the pattern right away, as we have no color to draw.

	} // initialize


	private void initPatternSliders( )
	{
		fieldConversionProbabilityText
				.setNumberProperty( fieldConversionProbability.valueProperty( ), 6 );
		fieldCoolingRateText.setNumberProperty( fieldCoolingRate.valueProperty( ), 3 );
		fieldSignalStrengthText.setNumberProperty( fieldSignalStrength.valueProperty( ), 2 );

		fieldConversionProbability.valueProperty( ).addListener( this::triggerPatternRefresh );
		fieldCoolingRate.valueProperty( ).addListener( this::triggerPatternRefresh );
		fieldSignalStrength.valueProperty( ).addListener( this::triggerPatternRefresh );

	} // initPatternSliders


	private void initService( )
	{
		fieldPatternService = new GenerateCoatService( );
		fieldPatternService.setWidth( ( int ) fieldSamplePattern.getWidth( ) );
		fieldPatternService.setHeight( ( int ) fieldSamplePattern.getHeight( ) );
		fieldPatternService.setIterationLimit( 5 );
		fieldPatternService.setAlgorithm(
				GenerateCoatTaskBuilderFactory.getSupportedAlgorithms( ).iterator( ).next( ) );
		fieldPatternService.setOnSucceeded( this::redrawPattern );

	} // initService


	private ColorGene paramsToColorGene( )
	{
		ColorGene gene = new ColorGene( );
		setParams( gene );

		return gene;

	} // paramsToColorGene


	private void redrawPattern( WorkerStateEvent event )
	{
		if ( event != null && event.getSource( ).getState( ) == State.SUCCEEDED )
			{
			Object resultObj = event.getSource( ).getValue( );
			if ( resultObj instanceof GenerateCoatProgress )
				{
				GenerateCoatProgress result = ( GenerateCoatProgress ) resultObj;
				Image image = result.getCoatPattern( );
				if ( image != null )
					{
					fieldSamplePattern.getGraphicsContext2D( ).drawImage( image, 0, 0 );
					}
				}
			}

	} // redrawPattern


	public void setAlgorithm( String algorithm )
	{
		fieldPatternService.setAlgorithm( algorithm );

	} // setAlgorithm


	public void setColorGene( ColorGene gene )
	{
		fieldGene = gene;

		if ( gene == null )
			{
			fieldName.setText( "" );
			fieldColorSelector.setColor( null );
			fieldConversionProbability.setValue( 0 );
			fieldCoolingRate.setValue( 0 );
			fieldSignalStrength.setValue( 0 );
			}
		else
			{
			fieldName.setText( gene.getName( ) );
			fieldColorSelector.setColor( gene.getColor( ) );
			fieldConversionProbability.setValue( gene.getSeedConversionProb( ) );
			fieldCoolingRate.setValue( gene.getCoolingRate( ) );
			fieldSignalStrength.setValue( gene.getSignalStrength( ) );
			}

	} // setColorGene


	private void setParams( ColorGene gene )
	{
		if ( gene != null )
			{
			gene.setName( fieldName.getText( ) );
			gene.setColor( fieldColorSelector.getColor( ) );
			gene.setSeedConversionProb( fieldConversionProbability.getValue( ) );
			gene.setSignalStrength( fieldSignalStrength.getValue( ) );
			gene.setCoolingRate( fieldCoolingRate.getValue( ) );
			}

	} // setParams


	private void triggerPatternRefresh( ObservableValue<? extends Number> observable,
			Number oldValue, Number newValue )
	{
		// Doesn't matter if this succeeds; what matter is that we try, and we reduce
		// service overhead on a best-effort basis.
		if ( fieldLastPatternAutoRefreshTask != null )
			fieldLastPatternAutoRefreshTask.cancel( );

		// Refresh in 100ms unless someone cancels it with a newer auto-refresh before
		// then. (In short: Don't begin to refresh the pattern until we've been idle for
		// 100ms. This keeps the sliders from overwhelming us with cancelled rendering.)
		fieldLastPatternAutoRefreshTask = FXUtils.runLaterScheduled( this::actRefreshSamplePattern,
				Instant.now( ).plusMillis( 100 ) );

	} // triggerPatternRefresh

}
