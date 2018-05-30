/*
 * ScrollAwareSlider.java
 * 
 * Created on May 29, 2018
 */
package org.jdawg.fxcontrol;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * ScrollAwareSlider empowers sliders in the ColorGeneEditor with on-hover
 * scrollwheel-awareness.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class ScrollAwareSlider extends Slider implements Initializable
{
	// Class constants.
	private static final String COMPONENT_FXML_FILENAME = "ScrollAwareSlider.fxml";

	// Data members.
	private boolean fieldHovered;


	/**
	 * ScrollAwareSlider constructor.
	 */
	public ScrollAwareSlider( )
	{
		// Per tutorial example at:
		// https://docs.oracle.com/javafx/2/fxml_get_started/custom_control.htm

		// This instance will serve as our controller and dynamic root of our FXML graph.
		FXMLLoader loader = new FXMLLoader( getClass( ).getResource( COMPONENT_FXML_FILENAME ) );
		loader.setRoot( this );
		loader.setController( this );

		try
			{
			loader.load( );
			}
		catch ( IOException exception )
			{
			throw new RuntimeException( exception );
			}

	} // ScrollAwareSlider


	private static boolean isPositiveScroll( ScrollEvent event )
	{
		return ( event.getTextDeltaY( ) > 0 || event.getDeltaY( ) > 0 || event.getTextDeltaX( ) > 0
				|| event.getDeltaX( ) > 0 );

	} // isPositiveScroll


	private void handleMouseEntered( MouseEvent event )
	{
		fieldHovered = true;
		event.consume( );

	} // handleMouseEntered


	private void handleMouseExited( MouseEvent event )
	{
		fieldHovered = false;
		event.consume( );

	} // handleMouseExited


	private void handleScrollEvent( ScrollEvent event )
	{
		if ( fieldHovered )
			{
			if ( isPositiveScroll( event ) )
				this.increment( );
			else
				this.decrement( );

			event.consume( );
			}

	} // handleScrollEvent


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		addEventFilter( MouseEvent.MOUSE_ENTERED, this::handleMouseEntered );
		addEventFilter( MouseEvent.MOUSE_EXITED, this::handleMouseExited );
		addEventFilter( ScrollEvent.SCROLL, this::handleScrollEvent );

	} // initialize

}
