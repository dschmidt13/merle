/*
 * GenerateConfigBuilder.java
 * 
 * Created: Aug 26, 2020
 */
package org.jdawg.merle.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdawg.merle.ColorGene;

import javafx.scene.paint.Color;

/**
 * GenerateConfigBuilder
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class GenerateConfigBuilder
{
	// Defaults.
	private static final Color DEFAULT_BASE_COLOR = Color.WHITE;
	private static final long DEFAULT_ITERATION_LIMIT = -1;

	// Data members.
	private int fieldWidth;
	private int fieldHeight;
	private List<ColorGene> fieldColorGenes = Collections.emptyList( );
	private Color fieldBaseColor = DEFAULT_BASE_COLOR;
	private Long fieldRandomSeed = null;
	private long fieldIterationLimit = DEFAULT_ITERATION_LIMIT;
	private String fieldAlgorithmName;

	/**
	 * Create a new GenerateConfigBuilder.
	 */
	public GenerateConfigBuilder( )
	{
	} // GenerateConfigBuilder


	public GenerateConfigBuilder algorithmName( String algorithmName )
	{
		fieldAlgorithmName = algorithmName;
		return this;

	} // algorithmName


	public GenerateConfigBuilder baseColor( Color baseColor )
	{
		fieldBaseColor = baseColor;
		return this;

	} // baseColor


	/**
	 * Validates the field values given and instantiates an immutable
	 * {@code GenerateConfig} instance based on the values currently contained. Note that
	 * all instances are copied during the builder population process, so modifications
	 * performed to value containers given to this builder will never be reflected in the
	 * resulting config. Therefore, the same builder may be reused to build multiple
	 * differing configs with minimal duplicate code.
	 * <p>
	 * Note: Subclasses should follow the same constructor pattern for the corresponding
	 * GenerateConfig subclass and call its constructor rather than that of
	 * {@code GenerateConfig} itself, allowing {@code GenerateConfig} to populate its
	 * fields via the superconstructor.
	 * 
	 * @return a GenerateConfig whose state mirrors the values owned by this builder when
	 *         {@code build} is called.
	 * @throws IllegalArgumentException
	 */
	public GenerateConfig build( )
			throws IllegalArgumentException
	{
		validate( );

		return new GenerateConfig( this );

	} // build


	public GenerateConfigBuilder colorGenes( List<ColorGene> colorGenes )
	{
		// FIXME - This is a wee bit ugly.
		if ( colorGenes == null )
			{
			fieldColorGenes = null;
			}
		else
			{
			fieldColorGenes = new ArrayList<ColorGene>( );
			for ( ColorGene cg : colorGenes )
				fieldColorGenes.add( new ColorGene( cg ) );
			}

		fieldColorGenes = colorGenes;
		return this;

	} // colorGenes


	/**
	 * @return the algorithmName
	 */
	public String getAlgorithmName( )
	{
		return fieldAlgorithmName;

	} // getAlgorithmName


	/**
	 * @return the baseColor
	 */
	public Color getBaseColor( )
	{
		return fieldBaseColor;
	}


	/**
	 * @return the colorGenes
	 */
	public List<ColorGene> getColorGenes( )
	{
		// FIXME - Can return mutable CG instances...
		return Collections.unmodifiableList( fieldColorGenes );
	}


	/**
	 * @return the height
	 */
	public int getHeight( )
	{
		return fieldHeight;
	}


	/**
	 * @return the iterationLimit
	 */
	public long getIterationLimit( )
	{
		return fieldIterationLimit;
	}


	/**
	 * @return the randomSeed
	 */
	public Long getRandomSeed( )
	{
		return fieldRandomSeed;
	}


	/**
	 * @return the width
	 */
	public int getWidth( )
	{
		return fieldWidth;
	}


	public GenerateConfigBuilder height( int height )
	{
		fieldHeight = height;
		return this;

	} // height


	/**
	 * @param iterationLimit - a long to set as the iterationLimit. This places a cap on
	 *            the number of overall passes taken in rendering the image. Specific
	 *            interpretation depends on the rendering implementation. Setting this
	 *            {@code <= 0} will typically result in no cap.
	 */
	public GenerateConfigBuilder iterationLimit( long iterationLimit )
	{
		fieldIterationLimit = iterationLimit;
		return this;

	} // iterationLimit


	public GenerateConfigBuilder randomSeed( Long randomSeed )
	{
		fieldRandomSeed = randomSeed;
		return this;

	} // randomSeed


	/**
	 * Ensures that the current values of the fields are valid, not only in themselves but
	 * with respect to each other.
	 * <p>
	 * <b>Note:</b> All subclasses should call {@code super.validate()}.
	 * 
	 * @throws IllegalArgumentException if any of the given fields are not valid.
	 */
	protected void validate( )
			throws IllegalArgumentException
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


	public GenerateConfigBuilder width( int width )
	{
		fieldWidth = width;
		return this;

	} // width

}
