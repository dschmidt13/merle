/*
 * GenerateCoatProgress.java
 * 
 * Created on Jun 3, 2018
 */
package org.jdawg.merle;

import java.time.Duration;
import java.time.Instant;

import javafx.scene.image.WritableImage;

/**
 * GenerateCoatProgress is a simple wrapper object to store a result from generating a
 * coat pattern and some statistics about the run, e.g. how long it took.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class GenerateCoatProgress
{
	// Data members.
	private boolean fieldComplete;
	private int fieldIterations;
	private long fieldCalculationsPerformed;
	private long fieldEstimatedCalculationsRemaining;
	private Instant fieldStartTime;
	private Instant fieldStopTime;
	private Duration fieldRunTime;
	private WritableImage fieldCoatPattern;
	private boolean fieldCancelled;

	// TODO - Replace these fields with a dedicated config object.
	private String fieldAlgorithmName;
	private Long fieldRandomSeed;
	private int fieldIterationLimit;


	/**
	 * GenerateCoatProgress constructor.
	 */
	public GenerateCoatProgress( )
	{
	} // GenerateCoatProgress


	public String getAlgorithmName( )
	{
		return fieldAlgorithmName;

	} // getAlgorithmName


	/**
	 * @return long - the fieldCalculationsPerformed
	 */
	public long getCalculationsPerformed( )
	{
		return fieldCalculationsPerformed;

	} // getCalculationsPerformed


	/**
	 * @return WritableImage - the fieldCoatPattern
	 */
	public WritableImage getCoatPattern( )
	{
		return fieldCoatPattern;

	} // getCoatPattern


	/**
	 * @return long - the fieldEstimatedCalculationsRemaining
	 */
	public long getEstimatedCalculationsRemaining( )
	{
		return fieldEstimatedCalculationsRemaining;

	} // getEstimatedCalculationsRemaining


	/**
	 * Requires that at least one of the estimatedCalculationsRemaining and
	 * calculationsPerformed values be nonzero. Otherwise, a rougher guess will be made.
	 * 
	 * @return the estimated percentage complete, as a double between 0 and 1. If no
	 *         calculation data are available, will either return 0 or 1 as appropriate
	 *         depending on the value of the {@code complete} flag.
	 */
	public double getEstimatedPercentComplete( )
	{
		double estPctComplete;
		if ( isComplete( ) )
			{
			// If we're done, we're done, estimates aside.
			estPctComplete = 1.0;
			}
		else if ( fieldCalculationsPerformed == 0 && fieldEstimatedCalculationsRemaining == 0 )
			{
			// Don't divide by 0.
			estPctComplete = 0;
			}
		else
			{
			estPctComplete = ( double ) fieldCalculationsPerformed
					/ ( ( double ) fieldCalculationsPerformed
							+ fieldEstimatedCalculationsRemaining );
			}

		return estPctComplete;

	} // getEstimatedPercentComplete


	public Duration getEstimatedRemainingRunTime( )
	{
		if ( isComplete( ) || isCancelled( ) )
			{
			return Duration.ZERO;
			}
		else if ( fieldRunTime == null
				|| ( fieldCalculationsPerformed == 0 && fieldEstimatedCalculationsRemaining == 0 ) )
			{
			return null;
			}

		double estPctComplete = getEstimatedPercentComplete( );

		Duration estRemaining = Duration.ofMillis(
				( long ) ( fieldRunTime.toMillis( ) / estPctComplete - fieldRunTime.toMillis( ) ) );

		return estRemaining;

	} // getEstimatedRemainingRunTime


	/**
	 * @return int - the fieldIterationLimit
	 */
	public int getIterationLimit( )
	{
		return fieldIterationLimit;

	} // getIterationLimit


	/**
	 * @return int - the fieldIterations
	 */
	public int getIterations( )
	{
		return fieldIterations;

	} // getIterations


	/**
	 * @return Instant - the fieldStopTime unless null (not stopped), in which case the
	 *         stop time will be estimated by adding the estimated remaining run time to
	 *         the current time.
	 */
	public Instant getOrEstimateStopTime( )
	{
		Instant stopTime = fieldStopTime;

		if ( stopTime == null && getEstimatedRemainingRunTime( ) != null )
			{
			stopTime = Instant.now( ).plus( getEstimatedRemainingRunTime( ) );
			}

		return stopTime;

	} // getOrEstimateStopTime


	public Long getRandomSeed( )
	{
		return fieldRandomSeed;

	} // getRandomSeed


	/**
	 * @return Duration - the fieldRunTime
	 */
	public Duration getRunTime( )
	{
		return fieldRunTime;

	} // getRunTime


	/**
	 * @return Instant - the fieldStartTime
	 */
	public Instant getStartTime( )
	{
		return fieldStartTime;

	} // getStartTime


	/**
	 * @return boolean - the fieldCancelled
	 */
	public boolean isCancelled( )
	{
		return fieldCancelled;

	} // isCancelled


	/**
	 * @return boolean - the fieldComplete
	 */
	public boolean isComplete( )
	{
		return fieldComplete;

	} // isComplete


	public void setAlgorithmName( String algorithmName )
	{
		fieldAlgorithmName = algorithmName;

	} // setAlgorithmName


	/**
	 * @param calculationsPerformed - a long to set as the fieldCalculationsPerformed
	 */
	public void setCalculationsPerformed( long calculationsPerformed )
	{
		fieldCalculationsPerformed = calculationsPerformed;

	} // setCalculationsPerformed


	/**
	 * @param cancelled - a boolean to set as the fieldCancelled
	 */
	public void setCancelled( boolean cancelled )
	{
		fieldCancelled = cancelled;

	} // setCancelled


	/**
	 * @param coatPattern - a WritableImage to set as the fieldCoatPattern
	 */
	public void setCoatPattern( WritableImage coatPattern )
	{
		fieldCoatPattern = coatPattern;

	} // setCoatPattern


	/**
	 * @param complete - a boolean to set as the fieldComplete
	 */
	public void setComplete( boolean complete )
	{
		fieldComplete = complete;

	} // setComplete


	/**
	 * @param estimatedCalculationsRemaining - a long to set as the
	 *            fieldEstimatedCalculationsRemaining
	 */
	public void setEstimatedCalculationsRemaining( long estimatedCalculationsRemaining )
	{
		fieldEstimatedCalculationsRemaining = estimatedCalculationsRemaining;

	} // setEstimatedCalculationsRemaining


	/**
	 * @param iterationLimit - a int to set as the fieldIterationLimit
	 */
	public void setIterationLimit( int iterationLimit )
	{
		fieldIterationLimit = iterationLimit;

	} // setIterationLimit


	/**
	 * @param iterations - a int to set as the fieldIterations
	 */
	public void setIterations( int iterations )
	{
		fieldIterations = iterations;

	} // setIterations


	public void setRandomSeed( Long randomSeed )
	{
		fieldRandomSeed = randomSeed;

	} // setRandomSeed


	/**
	 * @param runTime - a Duration to set as the fieldRunTime
	 */
	public void setRunTime( Duration runTime )
	{
		fieldRunTime = runTime;

	} // setRunTime


	/**
	 * @param startTime - a Instant to set as the fieldStartTime
	 */
	public void setStartTime( Instant startTime )
	{
		fieldStartTime = startTime;

	} // setStartTime


	/**
	 * @param stopTime - a Instant to set as the fieldStopTime
	 */
	public void setStopTime( Instant stopTime )
	{
		fieldStopTime = stopTime;

	} // setStopTime

}
