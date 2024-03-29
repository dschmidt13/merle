/*
 * MainController.java
 * 
 * Created on May 15, 2018
 */
package org.jdawg.merle;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.jdawg.fxcomponent.CoatProgressSummary;
import org.jdawg.fxcomponent.ColorSelector;
import org.jdawg.fxcontrol.ColorGeneCell;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;

/**
 * MainController is the controller for the main Application UI.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class MainController implements Initializable
{
	/**
	 * Pure red opaque pixels in the base image are mapped with coat color pixels from the
	 * generated pattern.
	 */
	private static final int BASE_COLOR_COAT = ( 255 << 24 ) | ( 255 << 16 );

	/**
	 * Pure blue opaque pixels in the base image are mapped with eye color pixels from the
	 * designated eye color.
	 */
	private static final int BASE_COLOR_EYES = ( 255 << 24 ) | ( 255 );

	/**
	 * Pure green opaque pixels in the base image are mapped with nose color pixels from
	 * the designated nose color.
	 */
	private static final int BASE_COLOR_NOSE = ( 255 << 24 ) | ( 255 << 8 );

	private static final URL EDITOR_FXML_URL = MainController.class
			.getResource( "ColorGeneEditor.fxml" );

	private static final Color DEFAULT_COLOR_EYES = Color.SKYBLUE;
	private static final Color DEFAULT_COLOR_NOSE = Color.BLACK;

	// Data members.
	private FileChooser fieldFileChooser;
	private Dialog<ColorGeneEditResult> fieldEditColorGeneDialog;
	private Dialog<Color> fieldEditEyeColorDialog;
	private Dialog<Color> fieldEditNoseColorDialog;
	private ColorGeneEditorController fieldColorGeneEditorController;

	private ObjectProperty<Color> fieldEyeColor = new SimpleObjectProperty<>( null );
	private ObjectProperty<Color> fieldNoseColor = new SimpleObjectProperty<>( null );

	// Background service.
	private GenerateCoatService fieldGenerateCoatService;

	// Injected FXML members.
	@FXML
	private Canvas fieldCanvas;

	@FXML
	private ListView<ColorGene> fieldColorGenes;

	@FXML
	private ComboBox<String> fieldAlgorithmSelector;

	@FXML
	private CoatProgressSummary fieldSummary;

	@FXML
	private TextField fieldRandomSeed;

	@FXML
	private Canvas fieldEyeColorCanvas;

	@FXML
	private Canvas fieldNoseColorCanvas;

	/**
	 * MainController constructor.
	 */
	public MainController( )
	{
	} // MainController


	public static int colorToPackedInt( Color color )
	{
		if ( color == null )
			throw new NullPointerException( "Cannot represent a null color as a packed int." );

		// Get normalized color values and scale to bytes.
		int alpha = ( int ) Math.round( 255 * color.getOpacity( ) );
		int red = ( int ) Math.round( 255 * color.getRed( ) );
		int green = ( int ) Math.round( 255 * color.getGreen( ) );
		int blue = ( int ) Math.round( 255 * color.getBlue( ) );

		// Shift to prepare for packing. Blue shifts by 0 (i.e., it does not shift).
		alpha <<= 24;
		red <<= 16;
		green <<= 8;

		// Pack and return.
		return ( alpha | red | green | blue );

	} // colorToPackedInt


	public void actAddColorGene( )
	{
		fieldColorGenes.getSelectionModel( ).clearSelection( );
		actAddOrEditSelectedColorGene( );

	} // actAddColorGene


	public void actAddOrEditSelectedColorGene( )
	{
		Dialog<ColorGeneEditResult> dialog = getEditColorGeneDialog( );

		SelectionModel<ColorGene> selectionModel = fieldColorGenes.getSelectionModel( );
		ColorGene selected = selectionModel.getSelectedItem( );
		if ( selected == null )
			{
			fieldColorGeneEditorController.setColorGene( createDefaultColorGene( ) );
			}
		else
			{
			fieldColorGeneEditorController.setColorGene( selected );
			}

		Optional<ColorGeneEditResult> result = dialog.showAndWait( );
		if ( result.isPresent( ) )
			{
			ColorGeneEditResult editResult = result.get( );
			if ( selected == null )
				{
				fieldColorGenes.getItems( ).add( editResult.getColorGene( ) );
				}
			else
				{
				int selIndex = selectionModel.getSelectedIndex( );
				fieldColorGenes.getItems( ).set( selIndex, editResult.getColorGene( ) );
				}
			}

	} // actAddOrEditSelectedColorGene


	public void actCancel( )
	{
		fieldGenerateCoatService.cancel( );

	} // actCancel


	public void actCopy( )
	{
		WritableImage image = fieldCanvas.snapshot( null, null );

		Clipboard clipboard = Clipboard.getSystemClipboard( );
		ClipboardContent content = new ClipboardContent( );
		content.putImage( image );
		clipboard.setContent( content );

	} // actCopy


	public void actEditEyeColor( )
	{
		Optional<Color> result = getEditEyeColorDialog( ).showAndWait( );
		if ( result.isPresent( ) )
			fieldEyeColor.set( result.get( ) );

	} // actEditEyeColor


	public void actEditNoseColor( )
	{
		Optional<Color> result = getEditNoseColorDialog( ).showAndWait( );
		if ( result.isPresent( ) )
			fieldNoseColor.set( result.get( ) );

	} // actEditNoseColor


	public void actGenerate( )
	{
		if ( !fieldColorGenes.getItems( ).isEmpty( ) )
			{
			updateServiceProperties( );
			fieldGenerateCoatService.restart( );
			}

	} // actGenerate


	public void actRemoveSelectedColorGenes( )
	{
		List<ColorGene> selected = new ArrayList<>(
				fieldColorGenes.getSelectionModel( ).getSelectedItems( ) );

		for ( ColorGene gene : selected )
			fieldColorGenes.getItems( ).remove( gene );

	} // actRemoveSelectedColorGenes


	public void actSave( )
	{
		FileChooser fileChooser = getFileChooser( );
		File saveFile = fileChooser.showSaveDialog( fieldCanvas.getScene( ).getWindow( ) );
		if ( saveFile != null )
			{
			WritableImage image = fieldCanvas.snapshot( null, null );
			try
				{
				ImageIO.write( SwingFXUtils.fromFXImage( image, null ), "PNG", saveFile );
				}
			catch ( IOException exception )
				{
				// TODO Auto-generated catch block
				exception.printStackTrace( );
				}
			}

	} // actSave


	private void compositePattern( Image coatPattern )
			throws IOException
	{
		// TODO - Break this up a bit.

		// TODO - Make this more dynamic. Maybe a file chooser? Or a dropdown menu? Or a
		// constant at the very least? (What are we going to do to select this in the
		// future?)
		// Read the base image.
		BufferedImage dogBase = ImageIO.read( getClass( ).getResource( "/images/spitzbase.png" ) );

		int baseWidth = dogBase.getWidth( );
		int baseHeight = dogBase.getHeight( );

		// Get the background image. We'll draw onto this.
		BufferedImage background = getBackground( baseWidth, baseHeight );

		// TODO - Calculate position if size isn't the same.
		int bgOffsetX = 0;
		int bgOffsetY = 0;

		// Get the Canvas's pattern image.
		BufferedImage swingPattern = SwingFXUtils.fromFXImage( coatPattern, null );

		int baseOffsetX = 0;
		int baseOffsetY = 0;

		// TODO - Validate against negatives so we don't go OOB.
		// int baseOffsetX = ( int ) Math.round( ( pattern.getWidth( ) - dogBase.getWidth(
		// ) ) / 2.0 );
		// int baseOffsetY = ( int ) Math
		// .round( ( pattern.getHeight( ) - dogBase.getHeight( ) ) / 2.0 );

		// Get the eye and nose fill colors.
		int eyeColorRGB = colorToPackedInt( getEyeColor( ) );
		int noseColorRGB = colorToPackedInt( getNoseColor( ) );

		// Copy pixels from the Canvas's image to BG where base is red.
		int basePixel;
		for ( int yIdx = 0; yIdx < baseHeight; yIdx++ )
			{
			for ( int xIdx = 0; xIdx < baseWidth; xIdx++ )
				{
				// Get base pixel colors and check against red with full alpha.
				basePixel = dogBase.getRGB( xIdx, yIdx );
				switch ( basePixel )
					{
					case BASE_COLOR_COAT :
						// Copy from pattern to background at this pixel location.
						background.setRGB( xIdx, yIdx, swingPattern.getRGB( xIdx, yIdx ) );
						break;

					case BASE_COLOR_EYES :
						background.setRGB( xIdx, yIdx, eyeColorRGB );
						break;

					case BASE_COLOR_NOSE :
						background.setRGB( xIdx, yIdx, noseColorRGB );
						break;

					default :
						// Don't draw if we don't recognize it.
						break;
					}
				}
			}

		// Draw the lines image over the background image to create the final image.
		// TODO - Again, a constant or something?
		BufferedImage outline = ImageIO.read( getClass( ).getResource( "/images/spitzlines.png" ) );
		Graphics2D bgGfx = ( Graphics2D ) background.getGraphics( );
		bgGfx.drawImage( outline, null, 0, 0 );

		// Paint the background over the canvas.
		fieldCanvas.getGraphicsContext2D( ).drawImage( SwingFXUtils.toFXImage( background, null ),
				0, 0 );

	} // compositePattern


	private ColorGene createDefaultColorGene( )
	{
		ColorGene gene = new ColorGene( );

		gene.setName( "Black" );
		gene.setColor( Color.BLACK );
		gene.setCoolingRate( 0.333 );
		gene.setSeedConversionProb( 0.0001 );
		gene.setSignalStrength( 50 );

		return gene;

	} // createDefaultColorGene


	private Dialog<Color> createEditColorDialog( String title )
	{
		DialogPane pane = new DialogPane( );
		ColorSelector selector = new ColorSelector( );
		pane.setContent( selector );

		Dialog<Color> dialog = new Dialog<>( );
		dialog.initModality( Modality.APPLICATION_MODAL );
		dialog.setTitle( title );
		dialog.setDialogPane( pane );

		// Add buttons.
		pane.getButtonTypes( ).add( ButtonType.OK );
		pane.getButtonTypes( ).add( ButtonType.CANCEL );

		dialog.setResultConverter( ( buttonType ) -> ( ButtonType.OK.equals( buttonType )
				? selector.getColor( ) : null ) );

		return dialog;

	} // createEditColorDialog


	public void destroy( )
	{
		// Get subcontrollers to clean up their messes.
		fieldColorGeneEditorController.destroy( );

		// Clean up our own messes.
		fieldGenerateCoatService.cancel( );

	} // destroy


	private void fillCanvas( Canvas canvas, Paint paint )
	{
		// TODO - This could probably be moved to a static utility class.
		GraphicsContext gfx = canvas.getGraphicsContext2D( );
		Paint oldPaint = gfx.getFill( );
		gfx.setFill( paint );
		gfx.fillRect( 0, 0, canvas.getWidth( ), canvas.getHeight( ) );
		gfx.setFill( oldPaint );

	} // fillCanvas


	private BufferedImage getBackground( int minWidth, int minHeight )
	{
		// TODO - Load this from somewhere?
		BufferedImage background = new BufferedImage( minWidth, minHeight,
				BufferedImage.TYPE_INT_ARGB );
		Graphics2D gfx = ( Graphics2D ) background.getGraphics( );
		java.awt.Color oldFill = gfx.getColor( );
		gfx.setColor( java.awt.Color.WHITE );
		gfx.fillRect( 0, 0, minWidth, minHeight );
		gfx.setColor( oldFill );

		return background;

	} // getBackground


	private Dialog<ColorGeneEditResult> getEditColorGeneDialog( )
	{
		if ( fieldEditColorGeneDialog == null )
			{
			try
				{
				FXMLLoader loader = new FXMLLoader( EDITOR_FXML_URL );
				Parent editRoot = loader.load( );
				fieldColorGeneEditorController = loader.getController( );

				DialogPane pane = new DialogPane( );
				pane.setContent( editRoot );

				editRoot.requestLayout( );

				fieldEditColorGeneDialog = new Dialog<>( );
				fieldEditColorGeneDialog.initModality( Modality.APPLICATION_MODAL );
				fieldEditColorGeneDialog.setTitle( "Edit Gene" );
				fieldEditColorGeneDialog.setDialogPane( pane );

				// Add buttons.
				pane.getButtonTypes( ).add( ButtonType.OK );
				pane.getButtonTypes( ).add( ButtonType.CANCEL );

				// Lets the caller know whether the user accepted the changes.
				fieldEditColorGeneDialog.setResultConverter(
						( buttonType ) -> fieldColorGeneEditorController.getResult( buttonType ) );
				}
			catch ( IOException exception )
				{
				// TODO Auto-generated catch block
				exception.printStackTrace( );
				}
			}

		return fieldEditColorGeneDialog;

	} // getEditColorGeneDialog


	private Dialog<Color> getEditEyeColorDialog( )
	{
		if ( fieldEditEyeColorDialog == null )
			{
			fieldEditEyeColorDialog = createEditColorDialog( "Eye Color" );
			}

		return fieldEditEyeColorDialog;

	} // getEditEyeColorDialog


	private Dialog<Color> getEditNoseColorDialog( )
	{
		if ( fieldEditNoseColorDialog == null )
			{
			fieldEditNoseColorDialog = createEditColorDialog( "Nose Color" );
			}

		return fieldEditNoseColorDialog;

	} // getEditNoseColorDialog


	private Color getEyeColor( )
	{
		return fieldEyeColor.get( );

	} // getEyeColor


	private FileChooser getFileChooser( )
	{
		if ( fieldFileChooser == null )
			{
			fieldFileChooser = new FileChooser( );
			fieldFileChooser.setTitle( "Save as..." );

			ExtensionFilter filter = new ExtensionFilter( "PNG Image", Arrays.asList( "*.png" ) );
			fieldFileChooser.getExtensionFilters( ).add( filter );
			fieldFileChooser.setSelectedExtensionFilter( filter );
			}

		return fieldFileChooser;

	} // getFileChooser


	private Color getNoseColor( )
	{
		return fieldNoseColor.get( );

	} // getNoseColor


	public String getSelectedAlgorithm( )
	{
		return fieldAlgorithmSelector.getSelectionModel( ).getSelectedItem( );

	} // getSelectedAlgorithm


	private void handleGeneListClick( MouseEvent event )
	{
		if ( event.getClickCount( ) == 2 )
			{
			actAddOrEditSelectedColorGene( );
			event.consume( );
			}

	} // handleGeneListClick


	private void handleGeneListKeyPressed( KeyEvent event )
	{
		switch ( event.getCode( ) )
			{
			case DELETE :
				actRemoveSelectedColorGenes( );
				break;

			default :
				// Do nothing by default.
				break;
			}

	} // handleGeneListKeyPressed


	private void initDialogs( )
	{
		// Initialize the edit pane so we can set its algorithm.
		getEditColorGeneDialog( );
		getEditEyeColorDialog( );
		getEditNoseColorDialog( );

		// Additional background init.
		Platform.runLater( ( ) -> {
		getFileChooser( );
		} );

	} // initDialogs


	private void initEmbeddedUi( )
	{
		// Initialize the ColorGenes list.
		fieldColorGenes.getSelectionModel( ).setSelectionMode( SelectionMode.MULTIPLE );
		fieldColorGenes.setCellFactory( ( listView ) -> new ColorGeneCell( listView ) );
		fieldColorGenes.setOnMouseClicked( this::handleGeneListClick );
		fieldColorGenes.setOnKeyPressed( this::handleGeneListKeyPressed );

		// Attach listeners to color properties and then set default values.
		fieldEyeColor.addListener( ( obs, old, nw ) -> fillCanvas( fieldEyeColorCanvas, nw ) );
		fieldNoseColor.addListener( ( obs, old, nw ) -> fillCanvas( fieldNoseColorCanvas, nw ) );
		fieldEyeColor.set( DEFAULT_COLOR_EYES );
		fieldNoseColor.set( DEFAULT_COLOR_NOSE );

	} // initEmbeddedUi


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		initEmbeddedUi( );
		initDialogs( );

		// Initialize the algorithm selector.
		fieldAlgorithmSelector.getItems( ).addAll( GenerateCoatTaskFactory.getKnownAlgorithms( ) );
		fieldAlgorithmSelector.getSelectionModel( ).selectedItemProperty( )
				.addListener( ( obs, old, nw ) -> {
				if ( fieldColorGeneEditorController != null )
					fieldColorGeneEditorController.setAlgorithm( nw );
				} );
		fieldAlgorithmSelector.getSelectionModel( ).select( 0 );

		initServices( );

	} // initialize


	private void initServices( )
	{
		fieldGenerateCoatService = new GenerateCoatService( );
		fieldGenerateCoatService.setOnFailed( this::onSvcFail );
		fieldGenerateCoatService.setOnSucceeded(
				( event ) -> onServiceUpdate( fieldGenerateCoatService.getValue( ) ) );
		updateServiceProperties( );

	} // initServices


	private void onServiceUpdate( GenerateCoatProgress progress )
	{
		fieldSummary.setCoatProgress( progress );

		if ( progress.isComplete( ) )
			{
			try
				{
				compositePattern( progress.getCoatPattern( ) );
				}
			catch ( IOException exception )
				{
				// TODO Auto-generated catch block
				exception.printStackTrace( );
				}
			}
		else
			{
			fieldCanvas.getGraphicsContext2D( ).drawImage( progress.getCoatPattern( ), 0, 0 );
			}

	} // onServiceUpdate


	private void onSvcFail( WorkerStateEvent event )
	{
		System.err.println( "Service error: " + event.getSource( ).getException( ) );

	} // onSvcFail


	private void updateServiceProperties( )
	{
		fieldGenerateCoatService.setAlgorithm( getSelectedAlgorithm( ) );
		fieldGenerateCoatService.setWidth( ( int ) fieldCanvas.getWidth( ) );
		fieldGenerateCoatService.setHeight( ( int ) fieldCanvas.getHeight( ) );
		fieldGenerateCoatService.setColorGenes( fieldColorGenes.getItems( ) );
		fieldGenerateCoatService.setBaseColor( Color.WHITE );
		fieldGenerateCoatService.setIterationLimit( -1 );
		fieldGenerateCoatService.setProgressFunction( this::onServiceUpdate );

		try
			{
			Long randomSeed = Long.valueOf( fieldRandomSeed.getText( ) );
			fieldGenerateCoatService.setRandomSeed( randomSeed );
			}
		catch ( NumberFormatException ignored )
			{
			fieldGenerateCoatService.setRandomSeed( null );
			}

	} // updateServiceProperties

}
