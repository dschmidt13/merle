/*
 * ColorGeneCellGraphic.java
 * 
 * Created on Jun 12, 2018
 */
package org.jdawg.fxcomponent;

import java.net.URL;
import java.util.ResourceBundle;

import org.jdawg.merle.ColorGene;
import org.jdawg.util.FXUtils;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * ColorGeneCellGraphic
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class ColorGeneCellGraphic extends HBox implements Initializable
{
	// Class constants.
	private static final String COMPONENT_FXML_FILENAME = "ColorGeneCellGraphic.fxml";

	// Injected FXML members.
	@FXML
	private Label fieldLabel;

	@FXML
	private ImageView fieldColorSample;


	/**
	 * ColorGeneCellGraphic constructor.
	 */
	public ColorGeneCellGraphic( )
	{
		FXUtils.loadAsControlRoot( COMPONENT_FXML_FILENAME, this );

	} // ColorGeneCellGraphic


	private Image calculateColorSample( ColorGene gene )
	{
		Canvas canvas = new Canvas( fieldColorSample.getFitWidth( ),
				fieldColorSample.getFitHeight( ) );
		GraphicsContext gfx = canvas.getGraphicsContext2D( );
		gfx.setFill( gene.getColor( ) );
		gfx.fillRect( 0, 0, canvas.getWidth( ), canvas.getHeight( ) );

		return canvas.snapshot( null, null );

	} // calculateGraphicImage


	private String calculateLabel( ColorGene gene )
	{
		StringBuilder builder = new StringBuilder( );

		builder.append( gene.getName( ) );
		builder.append( " (" );
		builder.append( gene.getSignalStrength( ) );
		builder.append( ")" );

		return builder.toString( );

	} // calculateGraphicLabel


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
	} // initialize


	public void setColorGene( ColorGene gene )
	{
		if ( gene == null )
			{
			fieldLabel.setText( "" );
			fieldColorSample.setImage( null );
			}
		else
			{
			fieldLabel.setText( calculateLabel( gene ) );
			fieldColorSample.setImage( calculateColorSample( gene ) );
			}

	} // setColorGene

}
