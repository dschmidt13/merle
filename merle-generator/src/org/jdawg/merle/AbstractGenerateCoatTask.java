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
	// Configuration.
	// TODO - Move to a real config class.
	private List<ColorGene> fieldColorGenes;
	private int fieldWidth;
	private int fieldHeight;
	private Color fieldBaseColor = Color.WHITE;
	private int fieldIterationLimit = -1;
	private Long fieldRandomSeed;
	private Random fieldRandom = new Random( );
	private Consumer<GenerateCoatProgress> fieldProgressFunction;

	// Execution.
	private Instant fieldStartTime;
	private WritableImage fieldImage;
	private int fieldIteration = 0;
	private boolean fieldComplete;


	/**
	 * AbstractGenerateCoatTask constructor.
	 */
	protected AbstractGenerateCoatTask( )
	{
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
			}

		// Clean up progress members.
		fieldComplete = true;
		if ( fieldProgressFunction != null )
			{
			animation.cancel( );
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
	 * Returns the name of the algorithm.
	 * 
	 * @return
	 */
	public abstract String getAlgorithmName( );


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


	protected abstract long getCurrentCalcs( );


	/**
	 * @return int - the fieldHeight
	 */
	public int getHeight( )
	{
		return fieldHeight;

	} // getHeight


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
	 * @return int - the fieldIterationLimit
	 */
	public int getIterationLimit( )
	{
		return fieldIterationLimit;

	} // getIterationLimit


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
		progress.setAlgorithmName( getAlgorithmName( ) );
		progress.setIterationLimit( fieldIterationLimit );
		progress.setComplete( fieldComplete );
		progress.setRunTime( Duration.between( fieldStartTime, Instant.now( ) ) );
		progress.setRandomSeed( fieldRandomSeed );
		progress.setCoatPattern( fieldImage );
		progress.setCalculationsPerformed( getCurrentCalcs( ) );
		progress.setEstimatedCalculationsRemaining( estimateRemainingCalcs( ) );
		progress.setIterations( getIteration( ) );

		return progress;

	} // GenerateCoatProgress


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


	protected boolean nextIteration( )
	{
		boolean canIterate = ( getIterationLimit( ) <= 0
				|| ( fieldIteration < getIterationLimit( ) ) );

		if ( canIterate )
			fieldIteration++;

		return canIterate;

	} // nextIteration


	/**
	 * Called by super to allow subclasses to perform rendering.
	 */
	protected abstract void render( );


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


	/**
	 * @param iterationLimit - a int to set as the fieldIterationLimit
	 */
	public void setIterationLimit( int iterationLimit )
	{
		fieldIterationLimit = iterationLimit;

	} // setIterationLimit


	void setProgressFunction( Consumer<GenerateCoatProgress> progressFunction )
	{
		fieldProgressFunction = progressFunction;

	} // setProgressFunction


	void setRandom( Random random )
	{
		fieldRandom = random;

	} // setRandom


	void setRandomSeed( Long randomSeed )
	{
		fieldRandomSeed = randomSeed;

	} // setRandomSeed


	void setWidth( int width )
	{
		fieldWidth = width;

	} // setWidth

}
