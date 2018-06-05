/*
 * AbstractGenerateCoatTask.java
 * 
 * Created on Jun 4, 2018
 */
package org.jdawg.merle;

import java.util.List;
import java.util.Random;

import javafx.concurrent.Task;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * AbstractGenerateCoatTask
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public abstract class AbstractGenerateCoatTask extends Task<GenerateCoatProgress>
{
	// Data members.
	private List<ColorGene> fieldColorGenes;
	private int fieldWidth;
	private int fieldHeight;
	private Color fieldBaseColor = Color.WHITE;
	private int fieldPassLimit = -1;
	private Random fieldRandom = new Random( );


	/**
	 * AbstractGenerateCoatTask constructor.
	 */
	protected AbstractGenerateCoatTask( )
	{
	} // AbstractGenerateCoatTask


	public static void drawUpdate( WritableImage image, List<ColorPoint> points )
	{
		PixelWriter pixelWriter = image.getPixelWriter( );
		for ( ColorPoint colorPoint : points )
			{
			pixelWriter.setColor( ( int ) colorPoint.getX( ), ( int ) colorPoint.getY( ),
					colorPoint.getColor( ) );
			}

	} // drawUpdate


	protected WritableImage createBlank( )
	{
		final int width = getWidth( );
		final int height = getHeight( );
		final Color baseColor = getBaseColor( );

		WritableImage image = new WritableImage( getWidth( ), getHeight( ) );

		PixelWriter writer = image.getPixelWriter( );
		for ( int yIdx = 0; yIdx < height; yIdx++ )
			for ( int xIdx = 0; xIdx < width; xIdx++ )
				writer.setColor( xIdx, yIdx, baseColor );

		return image;

	} // createBlank


	/**
	 * @return Color - the fieldBaseColor
	 */
	public Color getBaseColor( )
	{
		return fieldBaseColor;

	} // getBaseColor


	/**
	 * @return List<ColorGene> - the fieldColorGenes
	 */
	public List<ColorGene> getColorGenes( )
	{
		return fieldColorGenes;

	} // getColorGenes


	/**
	 * @return int - the fieldHeight
	 */
	public int getHeight( )
	{
		return fieldHeight;

	} // getHeight


	/**
	 * @return int - the fieldPassLimit
	 */
	public int getPassLimit( )
	{
		return fieldPassLimit;

	} // getPassLimit


	/**
	 * @return Random - the fieldRandom
	 */
	public Random getRandom( )
	{
		return fieldRandom;

	} // getRandom


	/**
	 * @return int - the fieldWidth
	 */
	public int getWidth( )
	{
		return fieldWidth;

	} // getWidth


	void setBaseColor( Color baseColor )
	{
		fieldBaseColor = baseColor;

	} // setBaseColor


	void setColorGenes( List<ColorGene> colorGenes )
	{
		fieldColorGenes = colorGenes;

	} // setColorGenes


	void setHeight( int height )
	{
		fieldHeight = height;

	} // setHeight


	void setPassLimit( int passLimit )
	{
		fieldPassLimit = passLimit;

	} // setPassLimit


	void setRandom( Random random )
	{
		fieldRandom = random;

	} // setRandom


	void setWidth( int width )
	{
		fieldWidth = width;

	} // setWidth

}
