/*
 * NumericTextField.java
 * 
 * Created on May 18, 2018
 */
package org.jdawg.fxcontrol;

import java.net.URL;
import java.util.ResourceBundle;

import org.jdawg.util.FXUtils;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * NumericTextField
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class NumericTextField extends TextField implements Initializable
{
	// Class constants.
	private static final String COMPONENT_FXML_FILENAME = "NumericTextField.fxml";

	// Data members.
	private Property<Number> fieldNumberProperty;

	private final Property<Number> fieldInternalNumberProperty = new SimpleDoubleProperty( );

	private boolean fieldUpdating;
	private String fieldFormatString;


	/**
	 * NumericTextField constructor.
	 */
	public NumericTextField( )
	{
		FXUtils.loadAsControlRoot( COMPONENT_FXML_FILENAME, this );

	} // NumericTextField


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		setText( "0" );
		textProperty( ).addListener( ( obs, old, nw ) -> updateNumberFromText( nw ) );
		fieldInternalNumberProperty.addListener( ( obs, old, nw ) -> updateTextFromNumber( nw ) );

	} // initialize


	public void setNumberProperty( Property<Number> numberProperty, int maxPrecision )
	{
		if ( numberProperty.isBound( ) )
			{
			throw new IllegalArgumentException( "Cannot bind to an already-bound property." );
			}

		// Unbind, update, and rebind number properties.
		if ( fieldNumberProperty != null )
			fieldNumberProperty.unbindBidirectional( fieldInternalNumberProperty );
		fieldNumberProperty = numberProperty;
		fieldNumberProperty.bindBidirectional( fieldInternalNumberProperty );

		// Update the format string.
		fieldFormatString = "%." + maxPrecision + "f";

	} // setNumberProperty


	private void updateNumberFromText( String newText )
	{
		if ( !fieldUpdating )
			{
			fieldUpdating = true;
			try
				{
				double doubleValue = Double.parseDouble( newText );
				fieldInternalNumberProperty.setValue( doubleValue );
				}
			catch ( NumberFormatException exception )
				{
				// Ignore it.
				}
			fieldUpdating = false;
			}

	} // updateNumberFromText


	private void updateTextFromNumber( Number newNumber )
	{
		if ( !fieldUpdating )
			{
			fieldUpdating = true;

			boolean needsUpdate = true;
			try
				{
				// Don't replace text if it parses to the same thing as what's there.
				// That's obnoxious. This occurs when deleting trailing 0's, for instance.
				double currentValue = Double.parseDouble( getText( ) );
				if ( currentValue == newNumber.doubleValue( ) )
					needsUpdate = false;
				}
			catch ( NumberFormatException exception )
				{
				// Ignore and continue with update.
				}

			if ( needsUpdate )
				{
				StringBuilder builder = new StringBuilder(
						String.format( fieldFormatString, newNumber.doubleValue( ) ) );

				// Delete trailing zeroes that don't contribute new information. Precision
				// isn't relevant in this application.
				if ( builder.indexOf( "." ) != -1 )
					{
					while ( builder.charAt( builder.length( ) - 1 ) == '0' )
						builder.deleteCharAt( builder.length( ) - 1 );

					// No more deletions after the decimal point, if we get that far.
					if ( builder.charAt( builder.length( ) - 1 ) == '.' )
						builder.deleteCharAt( builder.length( ) - 1 );
					}

				setText( builder.toString( ) );
				}

			fieldUpdating = false;
			}

	} // updateTextFromNumber

}
