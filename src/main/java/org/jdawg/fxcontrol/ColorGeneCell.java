/*
 * ColorGeneCell.java
 * 
 * Created on Jun 12, 2018
 */
package org.jdawg.fxcontrol;

import org.jdawg.fxcomponent.ColorGeneCellGraphic;
import org.jdawg.merle.ColorGene;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class ColorGeneCell extends ListCell<ColorGene>
{
	// Class constants.
	private static final String STYLE_DRAG_INSERT_POINT = "" //
			+ "-fx-border-width: 3px 0px 0px 0px; " //
			+ "-fx-border-color: black; " //
			+ "-fx-border-style: solid; ";

	// Data members.
	private ListView<ColorGene> fieldListView;

	// Graphic members.
	private ColorGeneCellGraphic fieldGraphicNode;


	public ColorGeneCell( ListView<ColorGene> listView )
	{
		fieldListView = listView;
		fieldGraphicNode = new ColorGeneCellGraphic( );

		// Set up event handlers.
		initHandlers( );

	} // ColorGeneCell


	private void handleClick( MouseEvent event )
	{
		if ( getItem( ) == null )
			fieldListView.getSelectionModel( ).clearSelection( );
		else
			fieldListView.getSelectionModel( ).select( getItem( ) );

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
			fieldListView.getSelectionModel( ).clearSelection( );
			}

	} // handleDragDetected


	private void handleDragDropped( DragEvent event )
	{
		if ( event.getGestureSource( ) instanceof ColorGeneCell )
			{
			moveInList( ( ( ColorGeneCell ) event.getGestureSource( ) ).getIndex( ), getIndex( ) );

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
		ObservableList<ColorGene> genes = fieldListView.getItems( );
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
			setText( null );
			setGraphic( null );
			}
		else
			{
			fieldGraphicNode.setColorGene( item );
			setGraphic( fieldGraphicNode );
			}

	} // updateItem

}