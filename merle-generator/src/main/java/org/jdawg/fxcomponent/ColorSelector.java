/*
 * ColorSelector.java
 * 
 * Created on Jun 11, 2018
 */
package org.jdawg.fxcomponent;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.jdawg.fxcontrol.NumericTextField;
import org.jdawg.util.FXUtils;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * ColorSelector
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class ColorSelector extends VBox implements Initializable
{
	// Class constants.
	private static final String COMPONENT_FXML_FILENAME = "ColorSelector.fxml";

	private static final String DEFAULT_PALETTE_URL_PATH = "/images/palette-default.png";

	/**
	 * Consistent with the number of squares on a default palette row.
	 */
	private static final int RECENT_PALETTE_MAX_SIZE = 11;

	private static final int PALETTE_SQUARE_SIZE = 18;

	// Data members.
	private List<Color> fieldBasePaletteColors;
	private Deque<Color> fieldRecentPaletteColors;

	// Injected view members.
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
	private ImageView fieldPalette;

	@FXML
	private ImageView fieldRecentPalette;

	@FXML
	private Canvas fieldSampleColor;


	/**
	 * ColorSelector constructor.
	 */
	public ColorSelector( )
	{
		FXUtils.loadAsControlRoot( COMPONENT_FXML_FILENAME, this );

	} // ColorSelector


	private void addRecentPaletteColor( )
	{
		Color color = getColor( );
		if ( !fieldBasePaletteColors.contains( color )
				&& !fieldRecentPaletteColors.contains( color ) )
			{
			if ( fieldRecentPaletteColors.size( ) >= RECENT_PALETTE_MAX_SIZE )
				fieldRecentPaletteColors.poll( );

			fieldRecentPaletteColors.offer( getColor( ) );

			redrawRecentPalette( );
			}

	} // addRecentPaletteColor


	public Color getColor( )
	{
		Color color = new Color( fieldRed.getValue( ) / 255.0, fieldGreen.getValue( ) / 255.0,
				fieldBlue.getValue( ) / 255.0, fieldAlpha.getValue( ) / 255.0 );

		return color;

	} // getColor


	private void handlePaletteClick( MouseEvent event )
	{
		if ( event.getSource( ) instanceof ImageView )
			{
			Image palette = ( ( ImageView ) event.getTarget( ) ).getImage( );
			int x = ( int ) Math.round( event.getX( ) );
			int y = ( int ) Math.round( event.getY( ) );

			Color color = palette.getPixelReader( ).getColor( x, y );
			setColor( color );
			}

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
		initColorSliders( );
		initPalettes( );

		// Draw the first color swatch.
		redrawColorSample( );

	} // initialize


	private void initPalettes( )
	{
		// Collections for colors in the base palette and colors we've added to our own
		// custom palette.
		fieldBasePaletteColors = new ArrayList<>( );
		fieldRecentPaletteColors = new ArrayDeque<>( RECENT_PALETTE_MAX_SIZE );

		// Load the base palette image.
		try
			{
			Image defaultPalette = new Image( DEFAULT_PALETTE_URL_PATH );
			fieldPalette.setImage( defaultPalette );
			fieldBasePaletteColors.addAll( scanPaletteColors( defaultPalette ) );
			}
		catch ( Exception exception )
			{
			// TODO Auto-generated catch block
			exception.printStackTrace( );
			}

		// Draw a default recent palette.
		redrawRecentPalette( );

		// Add a click listener for both palettes.
		fieldPalette.addEventFilter( MouseEvent.MOUSE_CLICKED, this::handlePaletteClick );
		fieldRecentPalette.addEventFilter( MouseEvent.MOUSE_CLICKED, this::handlePaletteClick );

	} // initPalettes


	private void redrawColorSample( )
	{
		GraphicsContext gfx = fieldSampleColor.getGraphicsContext2D( );
		gfx.setFill( Color.WHITE );
		gfx.fillRect( 0, 0, fieldSampleColor.getWidth( ), fieldSampleColor.getHeight( ) );
		gfx.setFill( getColor( ) );
		gfx.fillRect( 0, 0, fieldSampleColor.getWidth( ), fieldSampleColor.getHeight( ) );

	} // redrawSample


	private void redrawRecentPalette( )
	{
		Canvas canvas = new Canvas( fieldRecentPalette.getFitWidth( ),
				fieldRecentPalette.getFitHeight( ) );

		GraphicsContext gfx = canvas.getGraphicsContext2D( );

		gfx.setFill( Color.WHITE );
		gfx.fillRect( 0, 0, canvas.getWidth( ), canvas.getHeight( ) );

		int boxWidth = ( int ) canvas.getWidth( ) / RECENT_PALETTE_MAX_SIZE;
		int xOffset = 0;
		for ( Color color : fieldRecentPaletteColors )
			{
			gfx.setFill( color );
			gfx.fillRect( xOffset, 0, boxWidth, canvas.getHeight( ) );
			xOffset += boxWidth;
			}

		fieldRecentPalette.setImage( canvas.snapshot( null, null ) );

	} // redrawRecentPalette


	private Set<Color> scanPaletteColors( Image image )
	{
		Set<Color> colors = new HashSet<>( );

		int width = ( int ) image.getWidth( );
		int height = ( int ) image.getHeight( );
		PixelReader reader = image.getPixelReader( );
		for ( int yIndex = 0; yIndex < height; yIndex++ )
			{
			for ( int xIndex = 0; xIndex < width; xIndex++ )
				{
				colors.add( reader.getColor( xIndex, yIndex ) );
				}
			}

		return colors;

	} // scanBasePaletteColors


	public void select( )
	{
		addRecentPaletteColor( );

	} // accept


	public void setColor( Color color )
	{
		if ( color == null )
			{
			fieldRed.setValue( 0 );
			fieldGreen.setValue( 0 );
			fieldBlue.setValue( 0 );
			fieldAlpha.setValue( 0 );
			}
		else
			{
			fieldRed.setValue( color.getRed( ) * 255.0 );
			fieldGreen.setValue( color.getGreen( ) * 255.0 );
			fieldBlue.setValue( color.getBlue( ) * 255.0 );
			fieldAlpha.setValue( color.getOpacity( ) * 255.0 );
			}

		redrawColorSample( );

	} // setColor

}
