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

import org.jdawg.merle.algorithms.GenerateAlgorithm;
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
	private static final Map<String, Function<GenerateConfig, ? extends AbstractGenerateCoatTask>> CONFIG_MAP = new HashMap<>( );

	static
		{
		// TODO - Perhaps this could be loaded from a file somehow? But we'd have to
		// construct via reflection, which we can't guarantee will always work. Closures
		// suffice for now; this is app-level, not library-level.
		registerAlgorithmClass( MerleGenerateCoatTask.class,
				( config ) -> new MerleGenerateCoatTask( config ) );
		registerAlgorithmClass( MerleGenerateCoatLinearDistanceTask.class,
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


	private static <T extends AbstractGenerateCoatTask> void registerAlgorithmClass( Class<T> clazz,
			Function<GenerateConfig, T> constructor )
	{
		// Look up the handle of the generator algorithm from its annotation.
		GenerateAlgorithm generateAnnotation = clazz
				.getDeclaredAnnotation( GenerateAlgorithm.class );
		if ( null == generateAnnotation )
			{
			throw new IllegalArgumentException(
					"The class '" + clazz.getName( ) + "' is not a valid coat generation algorithm."
							+ " (It is missing the @GenerateAlgorithm annotation.)" );
			}
		String name = generateAnnotation.value( );

		// Place it in the map.
		CONFIG_MAP.put( name, constructor );

	} // registerAlgorithmClass


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
