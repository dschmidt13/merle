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

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
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
	private class ColorGeneCell extends ListCell<ColorGene>
	{
		// Class constants.
		private static final String STYLE_DRAG_INSERT_POINT = "" //
				+ "-fx-border-width: 3px 0px 0px 0px; " //
				+ "-fx-border-color: black; " //
				+ "-fx-border-style: solid; ";


		public ColorGeneCell( )
		{
			// Set up event handlers.
			initHandlers( );

		} // ColorGeneCell


		private void handleClick( MouseEvent event )
		{
			if ( event.getClickCount( ) == 2 )
				{
				if ( getItem( ) == null )
					actAddColorGene( );
				else
					editColorGene( getItem( ) );

				event.consume( );
				}

		} // handleClick


		private void handleDragDetected( MouseEvent event )
		{
			if ( getItem( ) != null )
				{
				event.consume( );

				// Start a drag and drop and set up the dragboard.
				Dragboard dragboard = startDragAndDrop( TransferMode.MOVE );
				dragboard.setDragView( snapshot( null, null ) );

				ClipboardContent content = new ClipboardContent( );
				content.putString( getItem( ).getName( ) );

				dragboard.setContent( content );

				// Clear list selection.
				fieldColorGenes.getSelectionModel( ).clearSelection( );
				}

		} // handleDragDetected


		private void handleDragDropped( DragEvent event )
		{
			if ( event.getGestureSource( ) instanceof ColorGeneCell )
				{
				moveInList( ( ( ColorGeneCell ) event.getGestureSource( ) ).getIndex( ),
						getIndex( ) );

				// Let the system know we succeeded.
				event.setDropCompleted( true );
				event.consume( );
				}

		} // handleDragDropped


		private void handleDragEntered( DragEvent event )
		{
			if ( event.getGestureSource( ) instanceof ColorGeneCell )
				{
				setStyle( STYLE_DRAG_INSERT_POINT );
				event.consume( );
				}

		} // handleDragEntered


		private void handleDragExited( DragEvent event )
		{
			if ( event.getGestureSource( ) instanceof ColorGeneCell )
				{
				setStyle( "" );
				event.consume( );
				}

		} // handleDragExited


		private void handleDragOver( DragEvent event )
		{
			if ( event.getGestureSource( ) instanceof ColorGeneCell )
				{
				event.acceptTransferModes( TransferMode.MOVE );
				event.consume( );
				}

		} // handleDragOver


		private void initHandlers( )
		{
			setOnDragDetected( this::handleDragDetected );
			setOnDragEntered( this::handleDragEntered );
			setOnDragExited( this::handleDragExited );
			setOnDragOver( this::handleDragOver );
			setOnDragDropped( this::handleDragDropped );
			setOnMouseClicked( this::handleClick );

		} // initHandlers


		private void moveInList( int sourceIndex, int targetIndex )
		{
			// When it's a real item being dragged, remove it from the model list and add
			// it at the target index or the end.
			ObservableList<ColorGene> genes = fieldColorGenes.getItems( );
			if ( ( sourceIndex != targetIndex ) && ( sourceIndex < genes.size( ) ) )
				{
				// Remove the source gene from its position in the list.
				ColorGene sourceGene = genes.remove( sourceIndex );

				// Moving up in the list: targetIndex doesn't change after removal.
				if ( sourceIndex > targetIndex )
					genes.add( targetIndex, sourceGene );

				// Moving down in the list, but not out of bounds: targetIndex has shrunk
				// by 1 after removal.
				else if ( ( targetIndex - 1 ) < genes.size( ) )
					genes.add( targetIndex - 1, sourceGene );

				// Moving past the end of the list (targetIndex is irrelevant).
				else
					genes.add( sourceGene );
				}

		} // moveInList


		@Override
		protected void updateItem( ColorGene item, boolean empty )
		{
			super.updateItem( item, empty );

			if ( item == null || empty )
				{
				if ( getItem( ) != null )
					{
					getItem( ).nameProperty( ).removeListener( this::updateName );
					}
				setText( null );
				setGraphic( null );
				}
			else
				{
				item.nameProperty( ).addListener( this::updateName );
				setText( item.getName( ) );
				}

		} // updateItem


		private void updateName( ObservableValue observable, String oldValue, String newValue )
		{
			if ( getItem( ) != null )
				{
				setText( newValue );
				}

		} // updateName

	} // class ColorGeneCell

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

	// Data members.
	private FileChooser fieldFileChooser;
	private Dialog<Boolean> fieldEditColorGeneDialog;
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
		ColorGene gene = createDefaultColorGene( );
		if ( editColorGene( gene ) )
			fieldColorGenes.getItems( ).add( gene );

	} // actAddColorGene


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


	private boolean editColorGene( ColorGene colorGene )
	{
		boolean saved = false;

		Dialog<Boolean> dialog = getEditColorGeneDialog( colorGene );
		Optional<Boolean> result = dialog.showAndWait( );
		if ( result.isPresent( ) )
			saved = result.get( ).booleanValue( );

		return saved;

	} // actEditColorGene


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


	private Dialog<Boolean> getEditColorGeneDialog( ColorGene colorGene )
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

				// Configure OK button to update its gene with the changes. Wired per
				// Dialog validation Javadoc note.
				Button okBtn = ( Button ) pane.lookupButton( ButtonType.OK );
				okBtn.addEventFilter( ActionEvent.ACTION,
						fieldColorGeneEditorController::handleOk );
				Button cancelBtn = ( Button ) pane.lookupButton( ButtonType.CANCEL );
				cancelBtn.addEventFilter( ActionEvent.ACTION,
						fieldColorGeneEditorController::handleCancel );

				// Lets the caller know whether the user accepted the changes.
				fieldEditColorGeneDialog
						.setResultConverter( ( buttonType ) -> ButtonType.OK.equals( buttonType ) );
				}
			catch ( IOException exception )
				{
				// TODO Auto-generated catch block
				exception.printStackTrace( );
				}
			}

		fieldColorGeneEditorController.setColorGene( colorGene );

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
			actAddColorGene( );
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
		getEditColorGeneDialog( null );
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
		fieldColorGenes.setCellFactory( ( ignored ) -> new ColorGeneCell( ) );
		fieldColorGenes.setOnMouseClicked( this::handleGeneListClick );
		fieldColorGenes.setOnKeyPressed( this::handleGeneListKeyPressed );

		// Attach listeners to color properties.
		fieldEyeColor.addListener( ( obs, old, nw ) -> fillCanvas( fieldEyeColorCanvas, nw ) );
		fieldNoseColor.addListener( ( obs, old, nw ) -> fillCanvas( fieldNoseColorCanvas, nw ) );

	} // initEmbeddedUi


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		initEmbeddedUi( );
		initDialogs( );

		// Initialize the algorithm selector.
		fieldAlgorithmSelector.getItems( )
				.addAll( GenerateCoatTaskBuilderFactory.getSupportedAlgorithms( ) );
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
