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
	private Slider fieldGreen;

	@FXML
	private Slider fieldBlue;

	@FXML
	private Slider fieldAlpha;

	@FXML
	private Canvas fieldSample;

	@FXML
	private Slider fieldConversionProbability;

	@FXML
	private Slider fieldCoolingRate;

	@FXML
	private Slider fieldSignalStrength;


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
		redrawSample( );

		fieldRed.valueProperty( ).addListener( ( obs, old, nw ) -> redrawSample( ) );
		fieldGreen.valueProperty( ).addListener( ( obs, old, nw ) -> redrawSample( ) );
		fieldBlue.valueProperty( ).addListener( ( obs, old, nw ) -> redrawSample( ) );
		fieldAlpha.valueProperty( ).addListener( ( obs, old, nw ) -> redrawSample( ) );

	} // initialize


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

		fieldName.setText( gene.getName( ) );
		fieldRed.setValue( Math.round( gene.getColor( ).getRed( ) * 255.0 ) );
		fieldGreen.setValue( Math.round( gene.getColor( ).getGreen( ) * 255.0 ) );
		fieldBlue.setValue( Math.round( gene.getColor( ).getBlue( ) * 255.0 ) );
		fieldAlpha.setValue( Math.round( gene.getColor( ).getOpacity( ) * 255.0 ) );
		fieldConversionProbability.setValue( gene.getSeedConversionProb( ) );
		fieldSignalStrength.setValue( gene.getSignalStrength( ) );
		fieldCoolingRate.setValue( gene.getCoolingRate( ) );

	} // setColorGene

}
