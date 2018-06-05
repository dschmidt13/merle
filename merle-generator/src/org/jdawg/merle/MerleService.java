/*
 * MerleService.java
 * 
 * Created on May 16, 2018
 */
package org.jdawg.merle;

import java.util.List;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;

/**
 * MerleService
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class MerleService extends Service<GenerateCoatProgress>
{
	// Data members.
	private String fieldAlgorithm;
	private List<ColorGene> fieldColorGenes;
	private int fieldIterationLimit = -1;
	private int fieldWidth;
	private int fieldHeight;
	private Long fieldRandomSeed;
	private Color fieldBaseColor;


	/**
	 * MerleService constructor.
	 */
	public MerleService( )
	{
	} // MerleService


	@Override
	protected Task<GenerateCoatProgress> createTask( )
	{
		AbstractGenerateCoatTaskBuilder builder = GenerateCoatTaskBuilderFactory
				.createBuilder( fieldAlgorithm );

		if ( builder == null )
			{
			throw new NullPointerException(
					"Could not instantiate an AbstractGenerateCoatTaskBuilder; the algorithm name '"
							+ fieldAlgorithm + "' is missing or not recognized." );
			}

		builder.width( fieldWidth ).height( fieldHeight ).colorGenes( fieldColorGenes )
				.baseColor( fieldBaseColor ).iterationLimit( fieldIterationLimit )
				.randomSeed( fieldRandomSeed );

		return builder.build( );

	} // createTask


	/**
	 * @param algorithm - a String to set as the fieldAlgorithm
	 */
	public void setAlgorithm( String algorithm )
	{
		fieldAlgorithm = algorithm;

	} // setAlgorithm


	/**
	 * @param baseColor - a Color to set as the fieldBaseColor
	 */
	public void setBaseColor( Color baseColor )
	{
		fieldBaseColor = baseColor;

	} // setBaseColor


	public void setColorGenes( List<ColorGene> colorGenes )
	{
		fieldColorGenes = colorGenes;

	} // setColorGenes


	/**
	 * @param height - a int to set as the fieldHeight
	 */
	public void setHeight( int height )
	{
		fieldHeight = height;

	} // setHeight


	/**
	 * @param iterationLimit - a int to set as the iterationLimit. This places a cap on
	 *            the number of overall passes taken in rendering the image. Specific
	 *            interpretation depends on the rendering implementation. Setting this
	 *            {@code <= 0} will typically result in no cap.
	 */
	public void setIterationLimit( int iterationLimit )
	{
		fieldIterationLimit = iterationLimit;

	} // setIterationLimit


	/**
	 * @param randomSeed - a Long to set as the fieldRandomSeed
	 */
	public void setRandomSeed( Long randomSeed )
	{
		fieldRandomSeed = randomSeed;

	} // setRandomSeed


	/**
	 * @param width - a int to set as the fieldWidth
	 */
	public void setWidth( int width )
	{
		fieldWidth = width;

	} // setWidth

}
