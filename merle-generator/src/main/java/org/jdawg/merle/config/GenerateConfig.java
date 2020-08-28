/*
 * GenerateConfig.java
 * 
 * Created: Aug 26, 2020
 */
package org.jdawg.merle.config;

import java.util.List;

import org.jdawg.merle.ColorGene;

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
