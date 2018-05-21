/*
 * FXUtils.java
 * 
 * Created on May 20, 2018
 */
package org.jdawg.util;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;

/**
 * FXUtils provides utilities for working in the JavaFX environment.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class FXUtils
{
	private static class PlatformTaskWrapper extends TimerTask
	{
		private final Runnable fieldTask;


		public PlatformTaskWrapper( Runnable task )
		{
			fieldTask = Objects.requireNonNull( task );

		} // PlatformTaskWrapper


		@Override
		public void run( )
		{
			Platform.runLater( fieldTask );

		} // run

	} // class PlatformTaskWrapper

	// Class constants.
	private static final Timer TASK_TIMER = new Timer( true );


	/**
	 * @throws AssertionError always. This class is for static utilities.
	 */
	private FXUtils( )
			throws AssertionError
	{
		throw new AssertionError( "Cannot instantiate static class." );

	} // FXUtils


	/**
	 * Schedules the given runnable to be enqueued to run with {@code Platform.runLater()}
	 * at the given time. Note that this does not guarantee that the task will run exactly
	 * at that time -- that depends on what the FX Application thread is up to. The task
	 * that will perform the enqueuing is returned so that it may be cancelled or
	 * scheduled prematurely if desired.
	 * 
	 * @param task a Runnable to (eventually) run on the FX Application thread.
	 * @param when an Instant indicating when the task should be scheduled onto the FX
	 *            Application thread.
	 * @return a TimerTask that may be used to immediately schedule the task to run or
	 *         attempt to cancel it.
	 */
	public static TimerTask runLaterScheduled( Runnable task, Instant when )
	{
		PlatformTaskWrapper wrapper = new PlatformTaskWrapper( task );
		TASK_TIMER.schedule( wrapper, new Date( when.toEpochMilli( ) ) );

		return wrapper;

	} // runLaterScheduled

}
