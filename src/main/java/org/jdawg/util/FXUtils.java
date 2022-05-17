/*
 * FXUtils.java
 * 
 * Created on May 20, 2018
 */
package org.jdawg.util;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

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
	 * Loads a given FXML file with the given controller object as both the root and
	 * controller object of the resulting scene graph. Used by custom components to
	 * bootstrap their FXML layouts without a lot of extra work. Call once during
	 * construction as: {@code FXUtils.loadAsControlRoot( MY_FXML_FILE, this )}.
	 * <p>
	 * For more information, see
	 * <a>https://docs.oracle.com/javafx/2/fxml_get_started/custom_control.htm</a>.
	 * 
	 * @param relativeUrlPath a String containing the URL path of the FXML file to load,
	 *            relative to the controller object's class. Must not be null.
	 * @param controller an Object to use as the controller and root of the FXML's graph.
	 *            It must be of the same type as the root element type of the FXML file.
	 *            Must not be null.
	 * @return the FXMLLoader used to load the document (after the load has been
	 *         performed). Returned as a convenience; typically this is not needed.
	 * @throws RuntimeException if an IOException occurs loading the FXML document. This
	 *             is the recommended practice by Oracle.
	 */
	public static FXMLLoader loadAsControlRoot( String relativeUrlPath, Object controller )
			throws RuntimeException
	{
		Objects.requireNonNull( relativeUrlPath, "url must not be null." );
		Objects.requireNonNull( controller, "controller must not be null." );

		URL resourceUrl = controller.getClass( ).getResource( relativeUrlPath );

		// Per tutorial example at:
		// https://docs.oracle.com/javafx/2/fxml_get_started/custom_control.htm

		// This instance will serve as our controller and dynamic root of our FXML graph.
		FXMLLoader loader = new FXMLLoader( resourceUrl );
		loader.setRoot( controller );
		loader.setController( controller );

		try
			{
			loader.load( );
			}
		catch ( IOException exception )
			{
			throw new RuntimeException( exception );
			}

		return loader;

	} // loadAsControlRoot


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
