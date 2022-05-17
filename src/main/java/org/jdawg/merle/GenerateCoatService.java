/*
 * GenerateCoatService.java
 * 
 * Created on May 16, 2018
 */
package org.jdawg.merle;

import java.util.List;
import java.util.function.Consumer;

import org.jdawg.merle.config.GenerateConfigBuilder;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;

/**
 * GenerateCoatService
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class GenerateCoatService extends Service<GenerateCoatProgress>
{
	// Data members.
	private GenerateConfigBuilder fieldConfigBuilder = new GenerateConfigBuilder( );
	private Consumer<GenerateCoatProgress> fieldProgressFunction;

	/**
	 * GenerateCoatService constructor.
	 */
	public GenerateCoatService( )
	{
	} // GenerateCoatService


	@Override
	protected Task<GenerateCoatProgress> createTask( )
	{
		try
			{
			AbstractGenerateCoatTask task = GenerateCoatTaskFactory
					.createTask( fieldConfigBuilder.build( ) );
			task.setProgressFunction( fieldProgressFunction );
			return task;
			}
		catch ( IllegalArgumentException exception )
			{
			throw new IllegalArgumentException(
					"Could not instantiate an AbstractGenerateCoatTask; the algorithm name '"
							+ fieldConfigBuilder.getAlgorithmName( )
							+ "' is missing or not recognized.",
					exception );
			}

	} // createTask


	public void setAlgorithm( String algorithm )
	{
		fieldConfigBuilder.algorithmName( algorithm );

	} // setAlgorithm


	public void setBaseColor( Color baseColor )
	{
		fieldConfigBuilder.baseColor( baseColor );

	} // setBaseColor


	public void setColorGenes( List<ColorGene> colorGenes )
	{
		fieldConfigBuilder.colorGenes( colorGenes );

	} // setColorGenes


	public void setHeight( int height )
	{
		fieldConfigBuilder.height( height );

	} // setHeight


	public void setIterationLimit( int iterationLimit )
	{
		fieldConfigBuilder.iterationLimit( iterationLimit );

	} // setIterationLimit


	/**
	 * @param progressFunction - a Consumer<GenerateCoatProgress> to set as the
	 *            fieldProgressFunction
	 */
	public void setProgressFunction( Consumer<GenerateCoatProgress> progressFunction )
	{
		fieldProgressFunction = progressFunction;

	} // setProgressFunction


	public void setRandomSeed( Long randomSeed )
	{
		fieldConfigBuilder.randomSeed( randomSeed );

	} // setRandomSeed


	public void setWidth( int width )
	{
		fieldConfigBuilder.width( width );

	} // setWidth

}
