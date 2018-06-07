/*
 * MerleGenerateCoatTask.java
 * 
 * Created on Jun 4, 2018
 */
package org.jdawg.merle.algorithms;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import org.jdawg.merle.AbstractGenerateCoatTask;
import org.jdawg.merle.AbstractGenerateCoatTaskBuilder;
import org.jdawg.merle.ColorGene;
import org.jdawg.merle.ColorPoint;

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

	// Class constants.
	public static final String ALGORITHM_NAME = "Merle-DistanceInverseSquare";

	// Data members.
	private Map<Point, ColorGene> fieldSeeds = new HashMap<>( );
	private long fieldCalcsPerformed;
	private int fieldPixelsRemaining;


	/**
	 * Extensible but protected constructor. Instantiate via Builder only!
	 */
	protected MerleGenerateCoatTask( )
	{
	} // MerleGenerateCoatTask


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

			fieldCalcsPerformed++;
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
					* candidate.getCoolingRate( ) * ( getIteration( ) - 1 ) );
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


	@Override
	protected long estimateRemainingCalcs( )
	{
		long calcs = 0;
		long area = fieldPixelsRemaining;
		long lastArea = -1;
		long seeds = fieldSeeds.size( );
		double seedProb;
		int stagnation = 0;
		int iter = getIteration( ) - 1;
		while ( area > 0 )
			{
			// One calc per seed per pixel.
			calcs += seeds * area;

			// Reduce area by the number converted.
			area -= ( long ) ( getCombinedGrowthProb( iter, seeds ) * area );

			// One seed calc per remaining pixel.
			calcs += area;

			// Reduce area by the new number seeding.
			area -= ( long ) ( getCombinedSeedProb( iter ) * area );

			// Are we stagnating?
			if ( area == lastArea )
				stagnation++;

			// Have we stalled completely?
			if ( stagnation > 5 )
				{
				calcs = Long.MAX_VALUE;
				break;
				}

			// Keep track of where we were.
			lastArea = area;

			iter++;
			}

		return calcs;

	} // estimateTotalCalcs


	@Override
	public String getAlgorithmName( )
	{
		return ALGORITHM_NAME;

	} // getAlgorithmName


	private double getCombinedGrowthProb( int iteration, long nSeeds )
	{
		// Estimate seed density and average distance to a seed.
		double seedDensity = ( double ) nSeeds / ( getWidth( ) * getHeight( ) );
		double dAvg = Math.sqrt( nSeeds / seedDensity );

		// Get sum lifetime seed prob for weighting signal strength.
		double sumLifetimeSeedProb = 0;
		for ( ColorGene gene : getColorGenes( ) )
			sumLifetimeSeedProb += getLifetimeSeedProb( gene, iteration );

		// Estimate weighted signal strength average, taking cooling into consideration.
		double strAvg = 0;
		for ( ColorGene gene : getColorGenes( ) )
			{
			double weight = getLifetimeSeedProb( gene, iteration ) / sumLifetimeSeedProb;
			strAvg += weight * gene.getSignalStrength( );
			}

		// Note: Might not work well if decay is nonlinear.
		double sigAvg = degradeSignal( strAvg, dAvg );

		return sigAvg;

	} // getCombinedGrowthProb


	/**
	 * @return the total probability that a point will convert to a seed on this
	 *         iteration.
	 */
	private double getCombinedSeedProb( int iteration )
	{
		double unseedProb = 1;
		for ( ColorGene gene : getColorGenes( ) )
			{
			double iterProb = gene.getSeedConversionProb( )
					- ( iteration * gene.getCoolingRate( ) * gene.getSeedConversionProb( ) );
			unseedProb *= ( 1.0 - iterProb );
			}

		return ( 1.0 - unseedProb );

	} // getCombinedSeedProb


	@Override
	protected long getCurrentCalcs( )
	{
		return fieldCalcsPerformed;

	} // getCurrentCalcs


	private double getLifetimeSeedProb( ColorGene gene, int iteration )
	{
		double notProb = 1;
		for ( int index = 0; index < iteration; index++ )
			{
			double iterProb = gene.getSeedConversionProb( )
					- ( iteration * gene.getCoolingRate( ) * gene.getSeedConversionProb( ) );
			notProb *= ( 1.0 - iterProb );
			}

		return ( 1.0 - notProb );

	} // getLifetimeSeedProb


	@Override
	protected void render( )
	{
		int width = getWidth( );
		int height = getHeight( );
		int nullCount = width * height;
		long maxWork = nullCount;

		// Keep track of remaining uncalculated points. To begin with, we have all of
		// them. We'll iterate in queue order until the source queue is empty. Note that
		// we bounce elements between two queues to keep track of our full iterations.
		Queue<Point> srcQ = new ArrayDeque<>( nullCount );
		for ( int yIdx = 0; yIdx < height; yIdx++ )
			for ( int xIdx = 0; xIdx < width; xIdx++ )
				srcQ.offer( new Point( xIdx, yIdx ) );
		Queue<Point> collQ = new ArrayDeque<>( nullCount );

		List<ColorPoint> drawPoints = new ArrayList<>( );
		Map<Point, ColorGene> newSeeds = new HashMap<>( );

		Queue<Point> swapQ;
		Point point;
		Color color;
		while ( !isCancelled( ) && !srcQ.isEmpty( ) && nextIteration( ) )
			{
			newSeeds.clear( );
			drawPoints.clear( );

			while ( !srcQ.isEmpty( ) )
				{
				// Get the next point.
				point = srcQ.poll( );

				// Try to assign it a color.
				color = chooseGrowthColor( point );
				if ( color == null )
					color = chooseSeedColor( point, newSeeds );

				// Collect misses and save hits.
				if ( color == null )
					collQ.offer( point );
				else
					drawPoints.add( new ColorPoint( point, color ) );
				}

			// Update our rendering.
			drawUpdate( drawPoints );

			// This map is updated once per generation to prevent novel seeds from
			// influencing fewer than all of the pixels.
			fieldSeeds.putAll( newSeeds );

			// Swap the source and collection queues to prepare for the next pass.
			swapQ = srcQ;
			srcQ = collQ;
			collQ = swapQ;
			swapQ = null;
			}

	} // render

}
