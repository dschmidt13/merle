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
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
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
	static class ColorGene
	{
		private StringProperty name = new SimpleStringProperty( "Black" );
		private ObjectProperty<Color> color = new SimpleObjectProperty<>( Color.BLACK );
		private DoubleProperty seedConversionProb = new SimpleDoubleProperty( 0.15 );
		private DoubleProperty coolingRate = new SimpleDoubleProperty( 0.2 );
		private DoubleProperty signalStrength = new SimpleDoubleProperty( 50 );


		public ColorGene( )
		{
		} // ColorGene;


		public ColorGene( ColorGene colorGene )
		{
			name = colorGene.name;
			color = colorGene.color;
			seedConversionProb = colorGene.seedConversionProb;
			coolingRate = colorGene.coolingRate;
			signalStrength = colorGene.signalStrength;

		} // ColorGene


		public ObjectProperty<Color> colorProperty( )
		{
			return color;
		}


		public DoubleProperty coolingRateProperty( )
		{
			return coolingRate;
		}


		public Color getColor( )
		{
			return colorProperty( ).get( );
		}


		public double getCoolingRate( )
		{
			return coolingRateProperty( ).get( );
		}


		public String getName( )
		{
			return nameProperty( ).get( );
		}


		public double getSeedConversionProb( )
		{
			return seedConversionProbProperty( ).get( );
		}


		public double getSignalStrength( )
		{
			return signalStrengthProperty( ).get( );
		}


		public StringProperty nameProperty( )
		{
			return name;
		}


		public DoubleProperty seedConversionProbProperty( )
		{
			return seedConversionProb;
		}


		public void setColor( Color color )
		{
			colorProperty( ).set( color );
		}


		public void setCoolingRate( double coolingRate )
		{
			coolingRateProperty( ).set( coolingRate );
		}


		public void setName( String name )
		{
			nameProperty( ).set( name );
		}


		public void setSeedConversionProb( double seedConversionProb )
		{
			seedConversionProbProperty( ).set( seedConversionProb );
		}


		public void setSignalStrength( double signalStrength )
		{
			signalStrengthProperty( ).set( signalStrength );
		}


		public DoubleProperty signalStrengthProperty( )
		{
			return signalStrength;
		}

	} // class ColorGene

	private static class ColorGeneCell extends ListCell<ColorGene>
	{
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

	private static final URL EDITOR_FXML_URL = MainController.class
			.getResource( "ColorGeneEditor.fxml" );

	// Data members.
	private FileChooser fieldFileChooser;
	private Dialog<Void> fieldEditDialog;
	private ColorGeneEditorController fieldEditorController;

	// Background service.
	private MerleService fieldMerleService;

	// Injected FXML members.
	@FXML
	private Canvas fieldCanvas;

	@FXML
	private ListView<ColorGene> fieldColorGenes;

	@FXML
	private ProgressBar fieldProgressBar;


	/**
	 * MainController constructor.
	 */
	public MainController( )
	{
	} // MainController


	public void actAddColorGene( )
	{
		ColorGene gene = new ColorGene( );
		fieldColorGenes.getItems( ).add( gene );
		actEditColorGene( gene );

	} // actAddColorGene


	public void actCopy( )
	{
		WritableImage image = fieldCanvas.snapshot( null, null );

		Clipboard clipboard = Clipboard.getSystemClipboard( );
		ClipboardContent content = new ClipboardContent( );
		content.putImage( image );
		clipboard.setContent( content );

	} // actCopy


	public void actEditColorGene( ColorGene colorGene )
	{
		Dialog dialog = getEditDialog( colorGene );
		dialog.showAndWait( );

		// LAM - Drag to reorder?

	} // actEditColorGene


	public void actGenerate( )
	{
		fieldMerleService.restart( );

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


	private void compositePattern( )
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
		BufferedImage pattern = SwingFXUtils.fromFXImage( fieldCanvas.snapshot( null, null ),
				null );

		int baseOffsetX = 0;
		int baseOffsetY = 0;

		// TODO - Validate against negatives so we don't go OOB.
		// int baseOffsetX = ( int ) Math.round( ( pattern.getWidth( ) - dogBase.getWidth(
		// ) ) / 2.0 );
		// int baseOffsetY = ( int ) Math
		// .round( ( pattern.getHeight( ) - dogBase.getHeight( ) ) / 2.0 );

		// Copy pixels from the Canvas's image to BG where base is red.
		int sRGBRed = ( 255 << 24 ) | ( 255 << 16 );
		for ( int yIdx = 0; yIdx < baseHeight; yIdx++ )
			{
			for ( int xIdx = 0; xIdx < baseWidth; xIdx++ )
				{
				// Get base pixel colors and check against red with full alpha.
				if ( dogBase.getRGB( xIdx, yIdx ) == sRGBRed )
					{
					// Copy from pattern to background at this pixel location.
					background.setRGB( xIdx, yIdx, pattern.getRGB( xIdx, yIdx ) );
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


	private Dialog<Void> getEditDialog( ColorGene colorGene )
	{
		if ( fieldEditDialog == null )
			{
			try
				{
				FXMLLoader loader = new FXMLLoader( EDITOR_FXML_URL );
				Parent editRoot = loader.load( );
				fieldEditorController = loader.getController( );

				DialogPane pane = new DialogPane( );
				pane.setContent( editRoot );

				editRoot.requestLayout( );

				fieldEditDialog = new Dialog<>( );
				fieldEditDialog.initModality( Modality.APPLICATION_MODAL );
				fieldEditDialog.setTitle( "Edit Gene" );
				fieldEditDialog.setDialogPane( pane );
				}
			catch ( IOException exception )
				{
				// TODO Auto-generated catch block
				exception.printStackTrace( );
				}
			}

		fieldEditorController.setColorGene( colorGene );

		return fieldEditDialog;

	} // getEditDialog


	private FileChooser getFileChooser( )
	{
		if ( fieldFileChooser == null )
			{
			fieldFileChooser = new FileChooser( );
			fieldFileChooser.setTitle( "Save as..." );

			ExtensionFilter filter = new ExtensionFilter( "PNG Image", "*.png" );
			fieldFileChooser.setSelectedExtensionFilter( filter );
			}

		return fieldFileChooser;

	} // getFileChooser


	private void handleListClick( MouseEvent event )
	{
		Object target = event.getTarget( );

		if ( event.getClickCount( ) == 2 && target instanceof ColorGeneCell )
			{
			ColorGene gene = ( ( ColorGeneCell ) target ).getItem( );
			if ( gene != null )
				{
				actEditColorGene( gene );
				}
			}

	} // handleListClick


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		fieldColorGenes.getSelectionModel( ).setSelectionMode( SelectionMode.MULTIPLE );
		fieldColorGenes.setCellFactory( ( ignored ) -> new ColorGeneCell( ) );
		fieldColorGenes.setFocusTraversable( false );

		fieldColorGenes.setOnMouseClicked( this::handleListClick );

		fieldMerleService = new MerleService( );
		fieldMerleService.setCanvas( fieldCanvas );
		fieldMerleService.setColorGenes( fieldColorGenes.getItems( ) );
		fieldMerleService.setOnRunning( this::onSvcStart );
		fieldMerleService.setOnCancelled( this::onSvcEnd );
		fieldMerleService.setOnSucceeded( this::onSvcSuccess );
		fieldMerleService.setOnFailed( this::onSvcFail );
		fieldMerleService.progressProperty( ).addListener( this::onSvcProgress );

	} // initialize


	private void onSvcEnd( WorkerStateEvent event )
	{
		fieldProgressBar.setVisible( false );

	} // onSvcEnd


	private void onSvcFail( WorkerStateEvent event )
	{
		fieldProgressBar.setVisible( false );
		System.err.println( "Service error: " + event.getSource( ).getException( ) );

	} // onSvcFail


	private void onSvcProgress( ObservableValue<? extends Number> observable, Number oldValue,
			Number newValue )
	{
		fieldProgressBar.setProgress( fieldMerleService.getProgress( ) );

	} // onSvcProgress


	private void onSvcStart( WorkerStateEvent event )
	{
		fieldProgressBar.setVisible( true );
		fieldProgressBar.setProgress( -1 );

	} // onSvcStart


	private void onSvcSuccess( WorkerStateEvent event )
	{
		fieldProgressBar.setVisible( false );
		try
			{
			compositePattern( );
			}
		catch ( IOException exception )
			{
			// TODO Auto-generated catch block
			exception.printStackTrace( );
			}

	} // onSvcSuccess

}
