/*
 * ColorGeneEditorController.java
 * 
 * Created on May 16, 2018
 */
package org.jdawg.merle;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimerTask;

import org.jdawg.fxcontrol.NumericTextField;
import org.jdawg.util.FXUtils;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * ColorGeneEditorController
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class ColorGeneEditorController implements Initializable
{
	// Class constants.
	private static final String DEFAULT_PALETTE_URL_PATH = "/images/palette-default.png";

	/**
	 * Consistent with the number of squares on a default palette row.
	 */
	private static final int RECENT_PALETTE_MAX_SIZE = 11;

	private static final int PALETTE_SQUARE_SIZE = 18;

	// Private data members.
	private ColorGene fieldGene;
	private GenerateCoatService fieldPatternService;
	private TimerTask fieldLastPatternAutoRefreshTask;
	private List<Color> fieldBasePaletteColors;
	private Deque<Color> fieldRecentPaletteColors;

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
		// Calculate whether a dark or light background would be more appropriate
		// for the current color, and set it into the service.
		fieldPatternService.setBaseColor( choosePatternBackground( ) );
		fieldPatternService.setColorGenes( Arrays.asList( paramsToColorGene( ) ) );
		fieldPatternService.restart( );

	} // actRefreshSamplePattern


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


	private Color choosePatternBackground( )
	{
		final int LIGHT_THRESHOLD = 255 * 13 / 15;

		int lightCount = 0;
		if ( fieldRed.getValue( ) > LIGHT_THRESHOLD )
			lightCount++;
		if ( fieldGreen.getValue( ) > LIGHT_THRESHOLD )
			lightCount++;
		if ( fieldBlue.getValue( ) > LIGHT_THRESHOLD )
			lightCount++;

		return ( lightCount > 1 ? Color.BLACK : Color.WHITE );

	} // choosePatternBackground


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
		addRecentPaletteColor( );

	} // handleOk


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


	private void redrawColorSample( )
	{
		GraphicsContext gfx = fieldSampleColor.getGraphicsContext2D( );
		gfx.setFill( Color.WHITE );
		gfx.fillRect( 0, 0, fieldSampleColor.getWidth( ), fieldSampleColor.getHeight( ) );
		gfx.setFill( getColor( ) );
		gfx.fillRect( 0, 0, fieldSampleColor.getWidth( ), fieldSampleColor.getHeight( ) );

	} // redrawSample


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


	public void setAlgorithm( String algorithm )
	{
		fieldPatternService.setAlgorithm( algorithm );

	} // setAlgorithm


	private void setColor( Color color )
	{
		fieldRed.setValue( color.getRed( ) * 255.0 );
		fieldGreen.setValue( color.getGreen( ) * 255.0 );
		fieldBlue.setValue( color.getBlue( ) * 255.0 );
		fieldAlpha.setValue( color.getOpacity( ) * 255.0 );

	} // setColor


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
