/*
 * MerleGenerateCoatTask.java
 * 
 * Created on Jun 4, 2018
 */
package org.jdawg.merle.algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jdawg.merle.AbstractGenerateCoatTask;
import org.jdawg.merle.AbstractGenerateCoatTaskBuilder;
import org.jdawg.merle.ColorGene;
import org.jdawg.merle.ColorPoint;
import org.jdawg.merle.GenerateCoatProgress;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * MerleGenerateCoatTask probabilistically places "seeds" - colors at specific points - on
 * the first pass over the image pixels. On subsequent passes, it gives each neutral pixel
 * a chance to convert to each seed based on the strength of the signal. The signal
 * strength is a function of the distance between the pixel in question and the seed
 * attempting to spread its color. Should conversion fail for the pixel, it is given an
 * opportunity to become a seed itself. All probabilities and probability decays are
 * controlled by parameters specified in the ColorGenes given to this instance (from the
 * AbstractGenerateCoatTask).
 * <p>
 * By default, the signal strength decays over the square of the distance between the two
 * points. Other signal strength functions are possible.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class MerleGenerateCoatTask extends AbstractGenerateCoatTask
{
	/**
	 * Builds an instance of MerleGenerateCoatTask.
	 * 
	 * @author David Schmidt (dschmidt13@gmail.com)
	 */
	public static class Builder extends AbstractGenerateCoatTaskBuilder
	{
		/**
		 * Public constructor.
		 */
		public Builder( )
		{
		} // Builder


		@Override
		protected AbstractGenerateCoatTask createInstance( )
		{
			return new MerleGenerateCoatTask( );

		} // createInstance

	} // Builder

	// Data members.
	private Map<Point, ColorGene> fieldSeeds = new HashMap<>( );
	private int fieldIteration = 0;

	private WritableImage fieldImage;


	/**
	 * Extensible but protected constructor. Instantiate via Builder only!
	 */
	protected MerleGenerateCoatTask( )
	{
	} // MerleGenerateCoatTask


	@Override
	protected GenerateCoatProgress call( )
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
		return signalStrength / Math.pow( distance, 2.0 );

	} // degradeSignal


	protected GenerateCoatProgress getResult( long runTime )
	{
		GenerateCoatProgress result = new GenerateCoatProgress( );

		result.setIterations( fieldIteration );
		result.setCoatPattern( fieldImage );

		return result;

	} // getResult

}
