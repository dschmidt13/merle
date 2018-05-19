/*
 * MerleService.java
 * 
 * Created on May 16, 2018
 */
package org.jdawg.merle;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.jdawg.merle.MainController.ColorGene;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * MerleService
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class MerleService extends Service<Void>
{
	// Data members.
	private Canvas fieldCanvas;
	private List<ColorGene> fieldColorGenes;


	/**
	 * MerleService constructor.
	 */
	public MerleService( )
	{
	} // MerleService


	private static Color chooseGrowthColor( List<ColorGene> colorGenes, Map<Point, ColorGene> seeds,
			Point point, Random random, int iteration )
	{
		// Must beat 0 to replace null.
		ColorGene strongestGene = null;
		double strongestSignalDiff = 0;

		ColorGene gene;
		for ( Map.Entry<Point, ColorGene> entry : seeds.entrySet( ) )
			{
			gene = entry.getValue( );

			double distance = point.distance( entry.getKey( ) );
			double adjSignalStr = degradeSignal( gene.getSignalStrength( ), distance );
			double randDiff = adjSignalStr - random.nextDouble( );

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


	private static Color chooseSeedColor( List<ColorGene> colorGenes, Map<Point, ColorGene> seeds,
			Point point, Random random, int iteration )
	{
		ColorGene gene = null;

		for ( ColorGene candidate : colorGenes )
			{
			double prob = candidate.getSeedConversionProb( ) - ( candidate.getSeedConversionProb( )
					* candidate.getCoolingRate( ) * iteration );
			if ( random.nextDouble( ) < prob )
				{
				gene = candidate;
				break;
				}
			}

		if ( gene != null && gene.getSignalStrength( ) > 0 )
			seeds.put( point, gene );

		return ( gene == null ) ? null : gene.getColor( );

	} // chooseSeedColor


	private static Color[ ][ ] copy( Color[ ][ ] pixelColors, int width, int height )
	{
		Color[ ][ ] newColors = new Color[ height ][ ];
		for ( int index = 0; index < height; index++ )
			newColors[ index ] = Arrays.copyOf( pixelColors[ index ], width );

		return newColors;

	} // copy


	private static double degradeSignal( double signalStrength, double distance )
	{
		// FIXME - Try different things.
		return signalStrength / Math.pow( distance, 2.0 );

	} // degradeSignal


	private static void drawUpdate( Canvas canvas, Color[ ][ ] pixelColors )
	{
		PixelWriter pixelWriter = canvas.getGraphicsContext2D( ).getPixelWriter( );

		int width = ( int ) canvas.getWidth( );
		int height = ( int ) canvas.getHeight( );

		for ( int yIndex = 0; yIndex < height; yIndex++ )
			{
			for ( int xIndex = 0; xIndex < width; xIndex++ )
				{
				Color color = pixelColors[ yIndex ][ xIndex ];
				if ( color != null )
					{
					pixelWriter.setColor( xIndex, yIndex, pixelColors[ yIndex ][ xIndex ] );
					}
				}
			}

	} // drawUpdate


	private static void whitewash( Canvas canvas )
	{
		GraphicsContext gfx = canvas.getGraphicsContext2D( );
		Paint fillPaint = gfx.getFill( );

		gfx.setFill( Color.WHITE );
		gfx.fillRect( 0, 0, canvas.getWidth( ), canvas.getHeight( ) );
		gfx.setFill( fillPaint );

	} // whitewash


	@Override
	protected Task<Void> createTask( )
	{
		final Canvas canvas = Objects.requireNonNull( fieldCanvas );
		final List<ColorGene> colorGenes = new ArrayList<>(
				Objects.requireNonNull( fieldColorGenes ) );

		Task<Void> task = new Task<Void>( )
		{

			@Override
			protected Void call( )
					throws Exception
			{
				int width = ( int ) canvas.getWidth( );
				int height = ( int ) canvas.getHeight( );
				int nullCount = width * height;
				long maxWork = nullCount;

				Color[ ][ ] pixelColors = new Color[ height ][ ];
				for ( int index = 0; index < height; index++ )
					pixelColors[ index ] = new Color[ width ];

				Map<Point, ColorGene> seeds = new HashMap<>( );

				Random random = new Random( );

				updateProgress( 0, maxWork );
				Platform.runLater( ( ) -> whitewash( canvas ) );

				int iteration = 0;
				while ( nullCount > 0 && !isCancelled( ) )
					{
					Map<Point, ColorGene> newSeeds = new HashMap<>( );
					for ( int yIndex = 0; yIndex < height; yIndex++ )
						{
						for ( int xIndex = 0; xIndex < width; xIndex++ )
							{
							if ( pixelColors[ yIndex ][ xIndex ] == null )
								{
								Point p = new Point( xIndex, yIndex );
								Color color = chooseGrowthColor( colorGenes, seeds, p, random,
										iteration );

								if ( color == null )
									color = chooseSeedColor( colorGenes, newSeeds, p, random,
											iteration );

								if ( color != null )
									{
									nullCount--;
									pixelColors[ yIndex ][ xIndex ] = color;
									}
								}
							}
						}

					// This map is updated once per generation to prevent
					// novel seeds from influencing fewer than all of the
					// pixels.
					seeds.putAll( newSeeds );

					updateProgress( maxWork - nullCount, maxWork );
					Platform.runLater(
							( ) -> drawUpdate( canvas, copy( pixelColors, width, height ) ) );

					iteration++;
					}

				Platform.runLater( ( ) -> drawUpdate( canvas, pixelColors ) );

				return null;

			} // call

		};

		return task;

	} // createTask


	public void setCanvas( Canvas canvas )
	{
		fieldCanvas = canvas;

	} // setCanvas


	public void setColorGenes( List<ColorGene> colorGenes )
	{
		fieldColorGenes = colorGenes;

	} // setColorGenes

}
