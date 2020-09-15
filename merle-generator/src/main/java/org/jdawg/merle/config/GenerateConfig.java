/*
 * GenerateConfig.java
 * 
 * Created: Aug 26, 2020
 */
package org.jdawg.merle.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.jdawg.merle.ColorGene;
import org.jdawg.util.DefaultNamingStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.scene.paint.Color;

/**
 * GenerateConfig is an immutable container for the algorithm parameters of a Generation
 * task (e.g., MerleGenerateCoatTask).
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class GenerateConfig
{
	// Data members.
	private final String fieldAlgorithmName;
	private final int fieldWidth;
	private final int fieldHeight;
	private final List<ColorGene> fieldColorGenes;
	private final Color fieldBaseColor;
	private final Long fieldRandomSeed;
	private final long fieldIterationLimit;

	/**
	 * Construct a new GenerateConfig
	 */
	protected GenerateConfig( GenerateConfigBuilder validatedBuilder )
	{
		fieldWidth = validatedBuilder.getWidth( );
		fieldHeight = validatedBuilder.getHeight( );
		fieldColorGenes = validatedBuilder.getColorGenes( );
		fieldBaseColor = validatedBuilder.getBaseColor( );
		fieldRandomSeed = validatedBuilder.getRandomSeed( );
		fieldIterationLimit = validatedBuilder.getIterationLimit( );
		fieldAlgorithmName = validatedBuilder.getAlgorithmName( );

	} // GenerateConfig


	public static void save( GenerateConfig config, Path path )
			throws IOException
	{
		// Convert the config object to JSON.
		Gson gson = new GsonBuilder( ).setFieldNamingStrategy( new DefaultNamingStrategy( ) )
				.create( );
		String json = gson.toJson( config );

		// Write it to a file.
		Files.writeString( path, json, StandardOpenOption.WRITE, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING );

	} // save


	@Override
	public boolean equals( Object obj )
	{
		// TODO Auto-generated method stub
		return super.equals( obj );

	} // equals


	@Override
	public int hashCode( )
	{
		// TODO Auto-generated method stub
		return super.hashCode( );

	} // hashCode


	@Override
	public String toString( )
	{
		// TODO Auto-generated method stub
		return super.toString( );

	} // toString


	/**
	 * @return the width
	 */
	public int getWidth( )
	{
		return fieldWidth;
	}


	/**
	 * @return the height
	 */
	public int getHeight( )
	{
		return fieldHeight;
	}


	/**
	 * @return the colorGenes
	 */
	public List<ColorGene> getColorGenes( )
	{
		return fieldColorGenes;
	}


	/**
	 * @return the baseColor
	 */
	public Color getBaseColor( )
	{
		return fieldBaseColor;
	}


	/**
	 * @return the randomSeed
	 */
	public Long getRandomSeed( )
	{
		return fieldRandomSeed;
	}


	/**
	 * @return the iterationLimit
	 */
	public long getIterationLimit( )
	{
		return fieldIterationLimit;
	}


	/**
	 * @return the algorithmName
	 */
	public String getAlgorithmName( )
	{
		return fieldAlgorithmName;

	} // getAlgorithmName

}
