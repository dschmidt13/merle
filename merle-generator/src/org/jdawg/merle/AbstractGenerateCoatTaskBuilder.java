/*
 * AbstractGenerateCoatTaskBuilder.java
 * 
 * Created on Jun 5, 2018
 */
package org.jdawg.merle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.paint.Color;

/**
 * AbstractGenerateCoatTaskBuilder defines a common way of instantiating valid
 * AbstractGenerateCoatTasks. Builder instances are obtained from the builder factory,
 * {@link GenerateCoatTaskBuilderFactory}. The builder itself may then be used to produce
 * one or more tasks.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public abstract class AbstractGenerateCoatTaskBuilder
{
	// Defaults.
	private static final Color DEFAULT_BASE_COLOR = Color.WHITE;

	// Data members.
	private Long fieldRandomSeed;
	private int fieldIterationLimit;
	private int fieldWidth;
	private int fieldHeight;
	private List<ColorGene> fieldColorGenes;
	private Color fieldBaseColor;


	/**
	 * AbstractGenerateCoatTaskBuilder constructor.
	 */
	public AbstractGenerateCoatTaskBuilder( )
	{
	} // AbstractGenerateCoatTaskBuilder


	public AbstractGenerateCoatTaskBuilder baseColor( Color baseColor )
	{
		fieldBaseColor = baseColor;
		return this;

	} // baseColor


	public AbstractGenerateCoatTask build( )
	{
		validate( );

		AbstractGenerateCoatTask instance = createInstance( );
		instance.setWidth( fieldWidth );
		instance.setHeight( fieldHeight );
		instance.setColorGenes( fieldColorGenes );
		instance.setBaseColor( fieldBaseColor );
		if ( fieldRandomSeed == null )
			instance.setRandom( new Random( ) );
		else
			instance.setRandom( new Random( fieldRandomSeed.longValue( ) ) );
		instance.setPassLimit( fieldIterationLimit );

		return instance;

	} // build


	public AbstractGenerateCoatTaskBuilder colorGenes( List<ColorGene> colorGenes )
	{
		if ( colorGenes != null )
			fieldColorGenes = new ArrayList<>( colorGenes );
		return this;

	} // colorGenes


	protected abstract AbstractGenerateCoatTask createInstance( );


	public AbstractGenerateCoatTaskBuilder height( int height )
	{
		fieldHeight = height;
		return this;

	} // height


	public AbstractGenerateCoatTaskBuilder iterationLimit( int iterationLimit )
	{
		fieldIterationLimit = iterationLimit;
		return this;

	} // iterationLimit


	public AbstractGenerateCoatTaskBuilder randomSeed( Long randomSeed )
	{
		fieldRandomSeed = randomSeed;
		return this;

	} // randomSeed


	protected void validate( )
	{
		if ( fieldWidth <= 0 )
			throw new IllegalArgumentException( "width must be > 0" );
		if ( fieldHeight <= 0 )
			throw new IllegalArgumentException( "height must be > 0" );
		if ( fieldColorGenes == null )
			throw new IllegalArgumentException( "colorGenes list is required" );
		if ( fieldColorGenes.isEmpty( ) )
			throw new IllegalArgumentException( "colorGenes must not be empty" );
		if ( fieldBaseColor == null )
			fieldBaseColor = DEFAULT_BASE_COLOR;

	} // validate


	public AbstractGenerateCoatTaskBuilder width( int width )
	{
		fieldWidth = width;
		return this;

	} // width

}
