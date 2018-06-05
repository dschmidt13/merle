/*
 * MerleService.java
 * 
 * Created on May 16, 2018
 */
package org.jdawg.merle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;

/**
 * MerleService
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class MerleService extends Service<GenerateCoatResult>
{
	// Data members.
	private List<ColorGene> fieldColorGenes;
	private int fieldGenerationLimit = -1;
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
	protected Task<GenerateCoatResult> createTask( )
	{
		final List<ColorGene> colorGenes = new ArrayList<>(
				Objects.requireNonNull( fieldColorGenes ) );
		final int generationLimit = fieldGenerationLimit;
		final int width = fieldWidth;
		final int height = fieldHeight;
		final Color baseColor = ( fieldBaseColor == null ? Color.WHITE : fieldBaseColor );

		// TODO - Allow different task implementations to be used.
		Task<GenerateCoatResult> task;
		if ( fieldRandomSeed == null )
			{
			task = new MerleGenerateCoatTask( new ArrayList<>( colorGenes ), width, height,
					baseColor, generationLimit );
			}
		else
			{
			task = new MerleGenerateCoatTask( new ArrayList<>( colorGenes ), width, height,
					baseColor, generationLimit, fieldRandomSeed.longValue( ) );
			}

		return task;

	} // createTask


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
	 * @param generationLimit - a int to set as the generationLimit. This places a cap on
	 *            the number of passes taken in rendering the image. Setting this
	 *            {@code <= 0} will result in no cap.
	 */
	public void setGenerationLimit( int generationLimit )
	{
		fieldGenerationLimit = generationLimit;

	} // setGenerationLimit


	/**
	 * @param height - a int to set as the fieldHeight
	 */
	public void setHeight( int height )
	{
		fieldHeight = height;

	} // setHeight


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
