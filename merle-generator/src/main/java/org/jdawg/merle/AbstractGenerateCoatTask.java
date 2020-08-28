/*
 * AbstractGenerateCoatTask.java
 * 
 * Created on Jun 4, 2018
 */
package org.jdawg.merle;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import org.jdawg.merle.config.GenerateConfig;

import javafx.application.Platform;
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
	private class AnimationTask extends TimerTask
	{
		@Override
		public void run( )
		{
			if ( fieldProgressFunction != null )
				Platform.runLater( ( ) -> fieldProgressFunction.accept( getProgressObj( ) ) );

		} // run

	} // FramerateTask

	/**
	 * Default frame period.
	 */
	private static final long DEFAULT_FRAME_PERIOD = 1000 / 60;

	// Rendering timer singleton.
	private static final Timer TIMER = new Timer( true );

	// Data members.
	private final GenerateConfig fieldConfig;
	private final Random fieldRandom;
	private Consumer<GenerateCoatProgress> fieldProgressFunction;
	private Instant fieldStartTime;
	private Instant fieldStopTime;
	private WritableImage fieldImage;
	private int fieldIteration = 0;
	private boolean fieldComplete;

	/**
	 * AbstractGenerateCoatTask constructor.
	 */
	public AbstractGenerateCoatTask( GenerateConfig config )
	{
		fieldConfig = config;
		fieldRandom = ( config.getRandomSeed( ) == null ) ? new Random( )
				: new Random( config.getRandomSeed( ).longValue( ) );

	} // AbstractGenerateCoatTask


	@Override
	protected GenerateCoatProgress call( )
	{
		// Create an image base to render onto.
		fieldImage = createBlank( );

		// Set up progress members. Even if the timer task isn't set up, the rest will be
		// necessary for the returned result.
		TimerTask animation = null;
		fieldStartTime = Instant.now( );
		fieldComplete = false;
		if ( fieldProgressFunction != null )
			{
			animation = new AnimationTask( );
			TIMER.schedule( animation, 0, DEFAULT_FRAME_PERIOD );
			}

		// Perform rendering.
		try
			{
			render( );
			}
		catch ( Exception exception )
			{
			// TODO Auto-generated catch block
			exception.printStackTrace( );
			cancel( );
			}

		// Clean up progress members.
		fieldStopTime = Instant.now( );
		if ( !isCancelled( ) )
			fieldComplete = true;
		if ( fieldProgressFunction != null )
			{
			animation.cancel( );

			// Force a final update with the cleaned up flags (in case we were between
			// frames and missed the changes, which often happens).
			new AnimationTask( ).run( );
			}

		return getProgressObj( );

	} // call


	/**
	 * Creates a base image with the appropriate background color that may be rendered
	 * over by {@link #render()}. Subclasses may create a separate base image for their
	 * own purposes, but they typically shouldn't need to. Instead, simply use
	 * {@link #getImage()} to retrieve the image currently being rendered.
	 * 
	 * @return
	 */
	protected WritableImage createBlank( )
	{
		final int width = getConfig( ).getWidth( );
		final int height = getConfig( ).getHeight( );
		final Color baseColor = getConfig( ).getBaseColor( );

		WritableImage image = new WritableImage( width, height );

		PixelWriter writer = image.getPixelWriter( );
		for ( int yIdx = 0; yIdx < height; yIdx++ )
			for ( int xIdx = 0; xIdx < width; xIdx++ )
				writer.setColor( xIdx, yIdx, baseColor );

		return image;

	} // createBlank


	protected void drawUpdate( List<ColorPoint> points )
	{
		PixelWriter pixelWriter = fieldImage.getPixelWriter( );
		for ( ColorPoint colorPoint : points )
			{
			pixelWriter.setColor( ( int ) colorPoint.getX( ), ( int ) colorPoint.getY( ),
					colorPoint.getColor( ) );
			}

	} // drawUpdate


	/**
	 * Subclasses are responsible for estimating the total number of remaining
	 * calculations they will need to perform over the course of rendering. Their accuracy
	 * is not enforced; they may even return 0 if they wish. More accurate estimates will
	 * give the user a better expectation of when the rendering will complete.
	 * 
	 * @return
	 */
	protected abstract long estimateRemainingCalcs( );


	/**
	 * @return the config
	 */
	public GenerateConfig getConfig( )
	{
		return fieldConfig;

	} // getConfig


	protected abstract long getCurrentCalcs( );


	/**
	 * Returns the image being rendered onto. May be used directly and written to
	 * directly, but it's recommended to use {@link #drawUpdate(List)} instead.
	 * 
	 * @return
	 */
	protected WritableImage getImage( )
	{
		return fieldImage;

	} // getImage


	/**
	 * @return int - the fieldIteration
	 */
	public int getIteration( )
	{
		return fieldIteration;

	} // getIteration


	/**
	 * @return Consumer<GenerateCoatProgress> - the fieldProgressFunction
	 */
	public Consumer<GenerateCoatProgress> getProgressFunction( )
	{
		return fieldProgressFunction;

	} // getProgressFunction


	/**
	 * When the UI specifies a progressFunction to this task, the task will call upon its
	 * subclasses to generate updates at regular intervals. The default implementation
	 * returns basic information about the current execution, such as config info, run
	 * time, completion status, the algorithm name, the estimated number of total
	 * calculations, and a snapshot of the current image. Subclasses may override this to
	 * provide additional details on top of what the super provides.
	 * 
	 * @return
	 */
	protected GenerateCoatProgress getProgressObj( )
	{
		GenerateCoatProgress progress = new GenerateCoatProgress( );
		progress.setConfig( getConfig( ) );
		progress.setComplete( fieldComplete );
		progress.setStartTime( fieldStartTime );
		progress.setRunTime( Duration.between( fieldStartTime, Instant.now( ) ) );
		progress.setCoatPattern( fieldImage );
		progress.setCalculationsPerformed( getCurrentCalcs( ) );
		progress.setEstimatedCalculationsRemaining( estimateRemainingCalcs( ) );
		progress.setIterations( getIteration( ) );
		progress.setCancelled( isCancelled( ) );
		progress.setStopTime( fieldStopTime );

		return progress;

	} // GenerateCoatProgress


	/**
	 * @return Random - the fieldRandom
	 */
	public Random getRandom( )
	{
		return fieldRandom;

	} // getRandom


	protected boolean nextIteration( )
	{
		final long limit = getConfig( ).getIterationLimit( );
		boolean canIterate = ( limit <= 0 ) || ( fieldIteration < limit );

		if ( canIterate )
			fieldIteration++;

		return canIterate;

	} // nextIteration


	/**
	 * Called by super to allow subclasses to perform rendering.
	 */
	protected abstract void render( );


	void setProgressFunction( Consumer<GenerateCoatProgress> progressFunction )
	{
		fieldProgressFunction = progressFunction;

	} // setProgressFunction

}
