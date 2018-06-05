/*
 * MerleGenerateCoatTask.java
 * 
 * Created on Jun 4, 2018
 */
package org.jdawg.merle;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * MerleGenerateCoatTask
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class MerleGenerateCoatTask extends AbstractGenerateCoatTask
{
	// Data members.
	private Map<Point, ColorGene> fieldSeeds = new HashMap<>( );
	private int fieldIteration = 0;

	private WritableImage fieldImage;


	/**
	 * MerleGenerateCoatTask constructor.
	 * 
	 * @param colorGenes
	 * @param width
	 * @param height
	 */
	public MerleGenerateCoatTask( List<ColorGene> colorGenes, int width, int height )
	{
		super( colorGenes, width, height );

	} // MerleGenerateCoatTask


	/**
	 * MerleGenerateCoatTask constructor.
	 * 
	 * @param colorGenes
	 * @param width
	 * @param height
	 * @param baseColor
	 */
	public MerleGenerateCoatTask( List<ColorGene> colorGenes, int width, int height,
			Color baseColor )
	{
		super( colorGenes, width, height, baseColor );

	} // MerleGenerateCoatTask


	/**
	 * MerleGenerateCoatTask constructor.
	 * 
	 * @param colorGenes
	 * @param width
	 * @param height
	 * @param baseColor
	 * @param passLimit
	 */
	public MerleGenerateCoatTask( List<ColorGene> colorGenes, int width, int height,
			Color baseColor, int passLimit )
	{
		super( colorGenes, width, height, baseColor, passLimit );

	} // MerleGenerateCoatTask


	/**
	 * MerleGenerateCoatTask constructor.
	 * 
	 * @param colorGenes
	 * @param width
	 * @param height
	 * @param baseColor
	 * @param passLimit
	 * @param randomSeed
	 */
	public MerleGenerateCoatTask( List<ColorGene> colorGenes, int width, int height,
			Color baseColor, int passLimit, long randomSeed )
	{
		super( colorGenes, width, height, baseColor, passLimit, randomSeed );

	} // MerleGenerateCoatTask


	@Override
	protected GenerateCoatResult call( )
			throws Exception
	{
		long startTime = System.currentTimeMillis( );
		fieldImage = createBlank( );

		int width = getWidth( );
		int height = getHeight( );
		int nullCount = width * height;
		long maxWork = nullCount;

		Color[ ][ ] pixelColors = new Color[ height ][ ];
		for ( int index = 0; index < height; index++ )
			pixelColors[ index ] = new Color[ width ];

		updateProgress( 0, maxWork );

		List<ColorPoint> drawPoints = new ArrayList<>( );
		while ( nullCount > 0 && !isCancelled( ) )
			{
			Map<Point, ColorGene> newSeeds = new HashMap<>( );
			drawPoints.clear( );

			for ( int yIndex = 0; yIndex < height; yIndex++ )
				{
				for ( int xIndex = 0; xIndex < width; xIndex++ )
					{
					if ( pixelColors[ yIndex ][ xIndex ] == null )
						{
						Point p = new Point( xIndex, yIndex );
						Color color = chooseGrowthColor( p );

						if ( color == null )
							color = chooseSeedColor( p, newSeeds );

						if ( color != null )
							{
							nullCount--;
							pixelColors[ yIndex ][ xIndex ] = color;
							drawPoints.add( new ColorPoint( p, color ) );
							}
						}
					}
				}

			// This map is updated once per generation to prevent
			// novel seeds from influencing fewer than all of the
			// pixels.
			fieldSeeds.putAll( newSeeds );

			updateProgress( maxWork - nullCount, maxWork );
			drawUpdate( fieldImage, drawPoints );

			fieldIteration++;
			if ( getPassLimit( ) > 0 && fieldIteration >= getPassLimit( ) )
				break;
			}

		return getResult( System.currentTimeMillis( ) - startTime );

	} // call


	protected Color chooseGrowthColor( Point point )
	{
		// Must beat 0 to replace null.
		ColorGene strongestGene = null;
		double strongestSignalDiff = 0;

		ColorGene gene;
		Random rand = getRandom( );
		for ( Map.Entry<Point, ColorGene> entry : fieldSeeds.entrySet( ) )
			{
			gene = entry.getValue( );

			double distance = point.distance( entry.getKey( ) );
			double adjSignalStr = degradeSignal( gene.getSignalStrength( ), distance );
			double randDiff = adjSignalStr - rand.nextDouble( );

			if ( randDiff > strongestSignalDiff )
				{
				strongestSignalDiff = randDiff;
				strongestGene = gene;
				}
			}

		Color growthColor = null;

		// A non-null value here implies a signal > 0.
		if ( strongestGene != null )
			growthColor = strongestGene.getColor( );

		return growthColor;

	} // chooseGrowthColor


	protected Color chooseSeedColor( Point point, Map<Point, ColorGene> generationSeeds )
	{
		ColorGene gene = null;

		Random rand = getRandom( );
		for ( ColorGene candidate : getColorGenes( ) )
			{
			double prob = candidate.getSeedConversionProb( ) - ( candidate.getSeedConversionProb( )
					* candidate.getCoolingRate( ) * fieldIteration );
			if ( rand.nextDouble( ) < prob )
				{
				gene = candidate;
				break;
				}
			}

		if ( gene != null && gene.getSignalStrength( ) > 0 )
			generationSeeds.put( point, gene );

		return ( gene == null ) ? null : gene.getColor( );

	} // chooseSeedColor


	protected double degradeSignal( double signalStrength, double distance )
	{
		// FIXME - Try different things.
		return signalStrength / Math.pow( distance, 2.0 );

	} // degradeSignal


	protected GenerateCoatResult getResult( long runTime )
	{
		GenerateCoatResult result = new GenerateCoatResult( );

		result.setPassCount( fieldIteration );
		result.setCoatPattern( fieldImage );
		result.setRunTimeMs( runTime );

		return result;

	} // getResult

}
