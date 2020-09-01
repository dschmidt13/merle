/*
 * DefaultNamingStrategy.java
 * 
 * Created: Sep 1, 2020
 */
package org.jdawg.util;

import java.lang.reflect.Field;

import com.google.gson.FieldNamingStrategy;

/**
 * DefaultNamingStrategy interprets Java Class field names that are prefixed with "field,"
 * such as "fieldX" to "x". If the prefix does not exist, the name is unchanged.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class DefaultNamingStrategy implements FieldNamingStrategy
{

	public DefaultNamingStrategy( )
	{
	} // DefaultNamingStrategy


	@Override
	public String translateName( Field field )
	{
		String javaName = field.getName( );
		String translated;
		if ( javaName.startsWith( "field" ) && javaName.length( ) > 5 )
			translated = Character.toLowerCase( javaName.charAt( 5 ) ) + javaName.substring( 6 );
		else
			translated = javaName;

		return translated;

	} // translateName

}
