/*
 * ColorGeneEditorController.java
 * 
 * Created on May 16, 2018
 */
package org.jdawg.merle;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jdawg.fxcontrol.NumericTextField;
import org.jdawg.util.FXUtils;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
	private MerleService fieldPatternService;
	private List<ColorGene> fieldServiceGenes;
	private List<Color> fieldBasePaletteColors;
	private List<Color> fieldRecentPaletteColors;
	private TimerTask fieldLastPatternAutoRefreshTask;

	// Injected FXML members.
	@FXML
	private TextField fieldName;

	@FXML
	private ImageView fieldPalette;

	@FXML
	private ImageView fieldRecentPalette;

	@FXML
	private Slider fieldRed;

	@FXML
	private NumericTextField fieldRedText;

	@FXML
	private Slider fieldGreen;

	@FXML
	private NumericTextField fieldGreenText;

	@FXML
	private Slider fieldBlue;

	@FXML
	private NumericTextField fieldBlueText;

	@FXML
	private Slider fieldAlpha;

	@FXML
	private NumericTextField fieldAlphaText;

	@FXML
	private Canvas fieldSampleColor;

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
		fieldServiceGenes.clear( );
		fieldServiceGenes.add( paramsToColorGene( ) );

		fieldPatternService.restart( );

	} // actRefreshSamplePattern


	public void destroy( )
	{
		fieldPatternService.cancel( );

	} // destroy


	private Color getColor( )
	{
		Color color = new Color( fieldRed.getValue( ) / 255.0, fieldGreen.getValue( ) / 255.0,
				fieldBlue.getValue( ) / 255.0, fieldAlpha.getValue( ) / 255.0 );

		return color;

	} // getColor


	public void handleCancel( ActionEvent event )
	{
		fieldPatternService.cancel( );

	} // handleCancel


	public void handleOk( ActionEvent event )
	{
		// Someday we might want to do validation or something here.
		fieldPatternService.cancel( );
		acceptChanges( );

	} // handleOk


	private void handlePaletteClick( MouseEvent event )
	{
		// TODO

	} // handlePaletteClick


	private void initColorSliders( )
	{
		// Configure sample redraw listeners.
		fieldRed.valueProperty( ).addListener( ( obs, old, nw ) -> redrawColorSample( ) );
		fieldGreen.valueProperty( ).addListener( ( obs, old, nw ) -> redrawColorSample( ) );
		fieldBlue.valueProperty( ).addListener( ( obs, old, nw ) -> redrawColorSample( ) );
		fieldAlpha.valueProperty( ).addListener( ( obs, old, nw ) -> redrawColorSample( ) );

		// Configure slider/text field bindings.
		fieldRedText.setNumberProperty( fieldRed.valueProperty( ), 0 );
		fieldGreenText.setNumberProperty( fieldGreen.valueProperty( ), 0 );
		fieldBlueText.setNumberProperty( fieldBlue.valueProperty( ), 0 );
		fieldAlphaText.setNumberProperty( fieldAlpha.valueProperty( ), 0 );

	} // jiggleInitSliders


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		// Initialize sliders & listeners.
		initService( );
		initPalettes( );
		initColorSliders( );
		initPatternSliders( );

		// Set the UI defaults.
		setColorGene( null );

		// Draw the first color swatch.
		redrawColorSample( );

		// NOTE: Don't refresh the pattern right away, as we have no color to draw.

	} // initialize


	private void initPalettes( )
	{
		// TODO

	} // initPalettes


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
		fieldPatternService = new MerleService( );

		fieldPatternService.setCanvas( fieldSamplePattern );

		fieldServiceGenes = new CopyOnWriteArrayList<>( );
		fieldPatternService.setColorGenes( fieldServiceGenes );

		fieldPatternService.setGenerationLimit( 5 );

	} // initService


	private ColorGene paramsToColorGene( )
	{
		ColorGene gene = new ColorGene( );
		setParams( gene );

		return gene;

	} // paramsToColorGene


	private void redrawColorSample( )
	{
		GraphicsContext gfx = fieldSampleColor.getGraphicsContext2D( );
		gfx.setFill( Color.WHITE );
		gfx.fillRect( 0, 0, fieldSampleColor.getWidth( ), fieldSampleColor.getHeight( ) );
		gfx.setFill( getColor( ) );
		gfx.fillRect( 0, 0, fieldSampleColor.getWidth( ), fieldSampleColor.getHeight( ) );

	} // redrawSample


	public void setColorGene( ColorGene gene )
	{
		fieldGene = gene;

		if ( gene == null )
			{
			fieldName.setText( "" );
			fieldRed.setValue( 0 );
			fieldGreen.setValue( 0 );
			fieldBlue.setValue( 0 );
			fieldAlpha.setValue( 0 );
			fieldConversionProbability.setValue( 0 );
			fieldCoolingRate.setValue( 0 );
			fieldSignalStrength.setValue( 0 );
			}
		else
			{
			fieldName.setText( gene.getName( ) );
			fieldRed.setValue( Math.round( gene.getColor( ).getRed( ) * 255.0 ) );
			fieldGreen.setValue( Math.round( gene.getColor( ).getGreen( ) * 255.0 ) );
			fieldBlue.setValue( Math.round( gene.getColor( ).getBlue( ) * 255.0 ) );
			fieldAlpha.setValue( Math.round( gene.getColor( ).getOpacity( ) * 255.0 ) );
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
			gene.setColor( getColor( ) );
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
