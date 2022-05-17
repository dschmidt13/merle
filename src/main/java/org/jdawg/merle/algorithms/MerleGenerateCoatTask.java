/*
 * MerleGenerateCoatTask.java
 * 
 * Created on Jun 4, 2018
 */
package org.jdawg.merle.algorithms;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.jdawg.merle.AbstractGenerateCoatTask;
import org.jdawg.merle.ColorGene;
import org.jdawg.merle.config.GenerateConfig;

import javafx.scene.image.PixelWriter;
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
@GenerateAlgorithm( "Merle-DistanceInverseSquare" )
public class MerleGenerateCoatTask extends AbstractGenerateCoatTask
{
	// Data members.
	private Map<Point, ColorGene> fieldSeeds = new HashMap<>( );
	private long fieldCalcsPerformed;
	private int fieldPixelsRemaining;

	public MerleGenerateCoatTask( GenerateConfig config )
	{
		super( config );

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

			// Counting distance and signal degradation separately.
			fieldCalcsPerformed += 2;

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
		for ( ColorGene candidate : getConfig( ).getColorGenes( ) )
			{
			double prob = candidate.getSeedConversionProb( ) - ( candidate.getSeedConversionProb( )
					* candidate.getCoolingRate( ) * ( getIteration( ) - 1 ) );
			fieldCalcsPerformed++;

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
		long maxArea = getConfig( ).getWidth( ) * getConfig( ).getHeight( );
		double areaComplete = ( double ) ( maxArea - fieldPixelsRemaining ) / maxArea;
		double estTotalWork = ( fieldCalcsPerformed / areaComplete );
		return ( ( ( long ) estTotalWork ) - fieldCalcsPerformed );

	} // estimateTotalCalcs


	@Override
	protected long getCurrentCalcs( )
	{
		return fieldCalcsPerformed;

	} // getCurrentCalcs


	@Override
	protected void render( )
	{
		int width = getConfig( ).getWidth( );
		int height = getConfig( ).getHeight( );

		// Keep track of remaining uncalculated points. To begin with, we have all of
		// them. We'll iterate in queue order until the source queue is empty. Note that
		// we bounce elements between two queues to keep track of our full iterations.
		ArrayDeque<Point> srcQ = new ArrayDeque<>( width * height );
		for ( int yIdx = 0; yIdx < height; yIdx++ )
			for ( int xIdx = 0; xIdx < width; xIdx++ )
				srcQ.offer( new Point( xIdx, yIdx ) );
		ArrayDeque<Point> collQ = new ArrayDeque<>( srcQ.size( ) );
		fieldPixelsRemaining = srcQ.size( );

		PixelWriter writer = getImage( ).getPixelWriter( );

		Map<Point, ColorGene> newSeeds = new HashMap<>( );
		ArrayDeque<Point> swapQ;
		Point point;
		Color color;
		while ( !isCancelled( ) && !srcQ.isEmpty( ) && nextIteration( ) )
			{
			newSeeds.clear( );

			while ( !srcQ.isEmpty( ) && !isCancelled( ) )
				{
				// Get the next point.
				point = srcQ.poll( );

				// Try to assign it a color.
				color = chooseGrowthColor( point );
				if ( color == null )
					color = chooseSeedColor( point, newSeeds );

				// Collect misses and save hits.
				if ( color == null )
					{
					collQ.offer( point );
					}
				else
					{
					writer.setColor( ( int ) point.getX( ), ( int ) point.getY( ), color );
					fieldPixelsRemaining--;
					}
				}

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
