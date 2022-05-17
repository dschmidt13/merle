/*
 * GenerateConfigTests.java
 * 
 * Created: Sep 11, 2020
 */
package org.jdawg.merle.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jdawg.merle.ColorGene;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javafx.scene.paint.Color;

/**
 * @author Dave
 */
class GenerateConfigTests
{

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass( )
			throws Exception
	{
	} // setUpBeforeClass


	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass( )
			throws Exception
	{
	} // tearDownAfterClass


	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp( )
			throws Exception
	{
	} // setUp


	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown( )
			throws Exception
	{
	} // tearDown


	/**
	 * Test method for
	 * {@link org.jdawg.merle.config.GenerateConfig#save(org.jdawg.merle.config.GenerateConfig, java.nio.file.Path)}.
	 */
	@Disabled( "Feature not yet implemented." )
	@Test
	void testSave( )
			throws IOException
	{
		// Note: Don't continue if some residual file exists. We don't know if it's
		// important. Although it's probably not.
		final Path path = Paths.get( "./test-config.json" );
		assertFalse( Files.exists( path ),
				"Aborting test: ./test-config.json already exists. Please remove the file." );

		List<ColorGene> genes = new ArrayList<>( );
		ColorGene unlikelyGene = new ColorGene( );
		unlikelyGene.setColor( Color.RED );
		unlikelyGene.setCoolingRate( 0.2 );
		unlikelyGene.setName( "Wicked Red!" );
		unlikelyGene.setSeedConversionProb( 0.84 );
		unlikelyGene.setSignalStrength( 25 );
		genes.add( unlikelyGene );
		GenerateConfig config = new GenerateConfigBuilder( ).width( 50 ).height( 50 ).colorGenes( genes ).build( );

		String expected = "";

		try
			{
			// Act
			GenerateConfig.save( config, path );

			// Assert
			assertTrue( Files.exists( path ) );
			String actual = Files.readString( path );
			assertEquals( expected, actual );
			}
		finally
			{
			// Cleanup
			Files.delete( path );
			}

	} // testSave

}
