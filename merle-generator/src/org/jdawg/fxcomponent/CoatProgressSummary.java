/*
 * CoatProgressSummary.java
 * 
 * Created on Jun 6, 2018
 */
package org.jdawg.fxcomponent;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

import org.jdawg.merle.GenerateCoatProgress;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;

/**
 * CoatProgressSummary manages the presentation of data updates as an algorithm runs.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class CoatProgressSummary extends BorderPane implements Initializable
{
	// Class constants.
	private static final String COMPONENT_FXML_FILENAME = "CoatProgressSummary.fxml";

	// Data members.
	private GenerateCoatProgress fieldCoatProgress;

	@FXML
	private ProgressBar fieldProgressBar;

	@FXML
	private Label fieldLabelAlgorithm;

	@FXML
	private Label fieldLabelRandomSeed;

	@FXML
	private Label fieldLabelIterationLimit;

	@FXML
	private Label fieldLabelIterations;

	@FXML
	private Label fieldLabelPctComplete;

	@FXML
	private Label fieldLabelCurrentRunTime;

	@FXML
	private Label fieldLabelEstRemainingTime;


	/**
	 * CoatProgressSummary constructor.
	 */
	public CoatProgressSummary( )
	{
		// Per tutorial example at:
		// https://docs.oracle.com/javafx/2/fxml_get_started/custom_control.htm

		// This instance will serve as our controller and dynamic root of our FXML graph.
		FXMLLoader loader = new FXMLLoader( getClass( ).getResource( COMPONENT_FXML_FILENAME ) );
		loader.setRoot( this );
		loader.setController( this );

		try
			{
			loader.load( );
			}
		catch ( IOException exception )
			{
			throw new RuntimeException( exception );
			}

	} // CoatProgressSummary


	public static String formatDuration( Duration duration )
	{
		if ( duration == null )
			return "";

		if ( duration.isZero( ) )
			return "0s";

		StringBuilder builder = new StringBuilder( );

		if ( duration.toHours( ) > 0 )
			{
			builder.append( duration.toHours( ) ).append( "h " );
			builder.append( duration.toMinutes( ) ).append( "m " );
			}
		else if ( duration.toMinutes( ) > 0 )
			{
			builder.append( duration.toMinutes( ) ).append( "m " );
			}

		builder.append( duration.toSeconds( ) ).append( "s" );

		return builder.toString( );

	} // formatDuration


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		update( );

	} // initialize


	public void setCoatProgress( GenerateCoatProgress coatProgress )
	{
		fieldCoatProgress = coatProgress;
		update( );

	} // setCoatProgress


	private void update( )
	{
		if ( fieldCoatProgress == null )
			{
			fieldProgressBar.setVisible( false );
			fieldLabelAlgorithm.setText( "" );
			fieldLabelRandomSeed.setText( "" );
			fieldLabelIterationLimit.setText( "" );
			fieldLabelIterations.setText( "" );
			fieldLabelPctComplete.setText( "" );
			fieldLabelCurrentRunTime.setText( "" );
			fieldLabelEstRemainingTime.setText( "" );
			}
		else
			{
			fieldLabelAlgorithm.setText( fieldCoatProgress.getAlgorithmName( ) == null ? ""
					: fieldCoatProgress.getAlgorithmName( ) );
			fieldLabelRandomSeed.setText( fieldCoatProgress.getRandomSeed( ) == null ? ""
					: String.valueOf( fieldCoatProgress.getRandomSeed( ) ) );
			fieldLabelIterationLimit
					.setText( String.valueOf( fieldCoatProgress.getIterationLimit( ) ) );
			fieldLabelIterations.setText( String.valueOf( fieldCoatProgress.getIterations( ) ) );
			fieldLabelPctComplete.setText( String.format( "%.2f%%",
					100.0 * fieldCoatProgress.getEstimatedPercentComplete( ) ) );
			fieldLabelCurrentRunTime.setText( formatDuration( fieldCoatProgress.getRunTime( ) ) );
			fieldLabelEstRemainingTime
					.setText( formatDuration( fieldCoatProgress.getEstimatedRemainingRunTime( ) ) );

			if ( fieldCoatProgress.isComplete( ) )
				{
				fieldProgressBar.setVisible( false );
				}
			else
				{
				fieldProgressBar.setVisible( true );
				fieldProgressBar.setProgress( fieldCoatProgress.getEstimatedPercentComplete( ) );
				}
			}

	} // update

}
