/*
 * ColorGeneEditorController.java
 * 
 * Created on May 16, 2018
 */
package org.jdawg.merle;

import java.net.URL;
import java.util.ResourceBundle;

import org.jdawg.merle.MainController.ColorGene;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
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

	// Injected FXML members.
	@FXML
	private TextField fieldName;

	@FXML
	private Slider fieldRed;

	@FXML
	private TextField fieldRedText;

	@FXML
	private Slider fieldGreen;

	@FXML
	private TextField fieldGreenText;

	@FXML
	private Slider fieldBlue;

	@FXML
	private TextField fieldBlueText;

	@FXML
	private Slider fieldAlpha;

	@FXML
	private TextField fieldAlphaText;

	@FXML
	private Canvas fieldSample;

	@FXML
	private Slider fieldConversionProbability;

	@FXML
	private TextField fieldConversionProbabilityText;

	@FXML
	private Slider fieldCoolingRate;

	@FXML
	private TextField fieldCoolingRateText;

	@FXML
	private Slider fieldSignalStrength;

	@FXML
	private TextField fieldSignalStrengthText;


	public void acceptChanges( )
	{
		fieldGene.setName( fieldName.getText( ) );
		fieldGene.setColor( getColor( ) );
		fieldGene.setSeedConversionProb( fieldConversionProbability.getValue( ) );
		fieldGene.setSignalStrength( fieldSignalStrength.getValue( ) );
		fieldGene.setCoolingRate( fieldCoolingRate.getValue( ) );

		closeDialog( );

	} // acceptChanges


	private void closeDialog( )
	{
		fieldName.getScene( ).getWindow( ).hide( );

	} // closeDialog


	private Color getColor( )
	{
		Color color = new Color( fieldRed.getValue( ) / 255.0, fieldGreen.getValue( ) / 255.0,
				fieldBlue.getValue( ) / 255.0, fieldAlpha.getValue( ) / 255.0 );

		return color;

	} // getColor


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		// Initialize sliders & listeners.
		initSliders( );

		// Set the UI defaults.
		setColorGene( null );

		// Draw the first color swatch.
		redrawSample( );

	} // initialize


	private void initSliders( )
	{
		// Configure slider->text update listeners.
		fieldRed.valueProperty( )
				.addListener( ( obs, old, nw ) -> updateTextFromSlider( nw, fieldRedText, 0 ) );
		fieldGreen.valueProperty( )
				.addListener( ( obs, old, nw ) -> updateTextFromSlider( nw, fieldGreenText, 0 ) );
		fieldBlue.valueProperty( )
				.addListener( ( obs, old, nw ) -> updateTextFromSlider( nw, fieldBlueText, 0 ) );
		fieldAlpha.valueProperty( )
				.addListener( ( obs, old, nw ) -> updateTextFromSlider( nw, fieldAlphaText, 0 ) );
		fieldConversionProbability.valueProperty( ).addListener(
				( obs, old, nw ) -> updateTextFromSlider( nw, fieldConversionProbabilityText, 6 ) );
		fieldCoolingRate.valueProperty( ).addListener(
				( obs, old, nw ) -> updateTextFromSlider( nw, fieldCoolingRateText, 6 ) );
		fieldSignalStrength.valueProperty( ).addListener(
				( obs, old, nw ) -> updateTextFromSlider( nw, fieldSignalStrengthText, 2 ) );

		// Configure text->slider update listeners.
		fieldRedText.textProperty( )
				.addListener( ( obs, old, nw ) -> updateSliderFromTextValue( nw, fieldRed ) );
		fieldGreenText.textProperty( )
				.addListener( ( obs, old, nw ) -> updateSliderFromTextValue( nw, fieldGreen ) );
		fieldBlueText.textProperty( )
				.addListener( ( obs, old, nw ) -> updateSliderFromTextValue( nw, fieldBlue ) );
		fieldAlphaText.textProperty( )
				.addListener( ( obs, old, nw ) -> updateSliderFromTextValue( nw, fieldAlpha ) );
		fieldConversionProbabilityText.textProperty( ).addListener(
				( obs, old, nw ) -> updateSliderFromTextValue( nw, fieldConversionProbability ) );
		fieldCoolingRateText.textProperty( ).addListener(
				( obs, old, nw ) -> updateSliderFromTextValue( nw, fieldCoolingRate ) );
		fieldSignalStrengthText.textProperty( ).addListener(
				( obs, old, nw ) -> updateSliderFromTextValue( nw, fieldSignalStrength ) );

		// Jiggle sliders to ensure all text fields get filled in. (If we only start on a
		// default, they won't.)
		fieldRed.setValue( 1 );
		fieldGreen.setValue( 1 );
		fieldBlue.setValue( 1 );
		fieldAlpha.setValue( 1 );
		fieldConversionProbability.setValue( 1 );
		fieldCoolingRate.setValue( 1 );
		fieldSignalStrength.setValue( 1 );
		fieldRed.setValue( 0 );
		fieldGreen.setValue( 0 );
		fieldBlue.setValue( 0 );
		fieldAlpha.setValue( 0 );
		fieldConversionProbability.setValue( 0 );
		fieldCoolingRate.setValue( 0 );
		fieldSignalStrength.setValue( 0 );

		// Configure sample redraw listeners after jiggle to prevent excessive rendering.
		fieldRed.valueProperty( ).addListener( ( obs, old, nw ) -> redrawSample( ) );
		fieldGreen.valueProperty( ).addListener( ( obs, old, nw ) -> redrawSample( ) );
		fieldBlue.valueProperty( ).addListener( ( obs, old, nw ) -> redrawSample( ) );
		fieldAlpha.valueProperty( ).addListener( ( obs, old, nw ) -> redrawSample( ) );

	} // jiggleInitSliders


	private void redrawSample( )
	{
		GraphicsContext gfx = fieldSample.getGraphicsContext2D( );
		gfx.setFill( getColor( ) );
		gfx.fillRect( 0, 0, fieldSample.getWidth( ), fieldSample.getHeight( ) );

	} // redrawSample


	public void rejectChanges( )
	{
		closeDialog( );

	} // rejectChanges


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


	private void updateSliderFromTextValue( String textValue, Slider slider )
	{
		try
			{
			double doubleValue = Double.parseDouble( textValue );
			slider.setValue( doubleValue );
			}
		catch ( NumberFormatException exception )
			{
			// Ignore it.
			}

	} // updateSliderFromTextValue


	private void updateTextFromSlider( Number sliderValue, TextField textField, int precision )
	{
		String sliderText = String.format( "%." + precision + "f", sliderValue.doubleValue( ) );

		if ( !sliderText.equals( textField.getText( ) ) )
			textField.setText( sliderText );

	} // updateTextFromSlider

}
