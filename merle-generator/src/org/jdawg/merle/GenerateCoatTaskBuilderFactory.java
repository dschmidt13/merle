/*
 * GenerateCoatTaskBuilderFactory.java
 * 
 * Created on Jun 5, 2018
 */
package org.jdawg.merle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.jdawg.merle.algorithms.MerleGenerateCoatLinearDistanceTask;
import org.jdawg.merle.algorithms.MerleGenerateCoatTask;

/**
 * GenerateCoatTaskBuilderFactory creates builders that may be used to instantiate
 * AbstractGenerateCoatTasks for a particular algorithm.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class GenerateCoatTaskBuilderFactory
{
	// Class constants.
	private static final Map<String, Supplier<AbstractGenerateCoatTaskBuilder>> CONFIG_MAP = new HashMap<>( );

	static
		{
		// TODO - Perhaps this could be loaded from a file somehow?
		CONFIG_MAP.put( "Merle-Distance-Squared", MerleGenerateCoatTask.Builder::new );
		CONFIG_MAP.put( "Merle-Distance-Linear", MerleGenerateCoatLinearDistanceTask.Builder::new );
		}


	/**
	 * @throws AssertionError always.
	 */
	private GenerateCoatTaskBuilderFactory( )
			throws AssertionError
	{
		throw new AssertionError( "Cannot instantiate static class." );

	} // GenerateCoatTaskBuilderFactory


	public static AbstractGenerateCoatTaskBuilder createBuilder( String algorithm )
	{
		return ( CONFIG_MAP.containsKey( algorithm ) ? CONFIG_MAP.get( algorithm ).get( ) : null );

	} // createBuilder


	public static Set<String> getSupportedAlgorithms( )
	{
		return Collections.unmodifiableSet( CONFIG_MAP.keySet( ) );

	} // getSupportedAlgorithms

}
