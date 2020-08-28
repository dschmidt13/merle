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
import java.util.function.Function;

import org.jdawg.merle.algorithms.MerleGenerateCoatLinearDistanceTask;
import org.jdawg.merle.algorithms.MerleGenerateCoatTask;
import org.jdawg.merle.config.GenerateConfig;

/**
 * GenerateCoatTaskFactory creates AbstractGenerateCoatTasks for a named configuration and
 * serves as a registry singleton for known algorithms.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class GenerateCoatTaskFactory
{
	// Class constants.
	private static final Map<String, Function<GenerateConfig, AbstractGenerateCoatTask>> CONFIG_MAP = new HashMap<>( );

	static
		{
		// TODO - Perhaps this could be loaded from a file somehow? But we'd have to
		// construct via reflection, which we can't guarantee will work. Closures suffice
		// for now; this is app-level, not library-level.
		CONFIG_MAP.put( MerleGenerateCoatTask.ALGORITHM_NAME,
				( config ) -> new MerleGenerateCoatTask( config ) );
		CONFIG_MAP.put( MerleGenerateCoatLinearDistanceTask.ALGORITHM_NAME,
				( config ) -> new MerleGenerateCoatLinearDistanceTask( config ) );
		}

	/**
	 * @throws AssertionError always.
	 */
	private GenerateCoatTaskFactory( )
			throws AssertionError
	{
		throw new AssertionError( "Cannot instantiate static class." );

	} // GenerateCoatTaskFactory


	public static AbstractGenerateCoatTask createTask( GenerateConfig config )
	{
		String algorithm = config.getAlgorithmName( );
		if ( algorithm == null )
			throw new IllegalArgumentException( "No algorithm name was specified in the config." );

		return ( CONFIG_MAP.containsKey( algorithm ) ? CONFIG_MAP.get( algorithm ).apply( config )
				: null );

	} // createTask


	public static Set<String> getKnownAlgorithms( )
	{
		return Collections.unmodifiableSet( CONFIG_MAP.keySet( ) );

	} // getKnownAlgorithms

}
