/*
 * CoatProgressSummary.java
 * 
 * Created on Jun 6, 2018
 */
package org.jdawg.fxcomponent;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ResourceBundle;

import org.jdawg.merle.GenerateCoatProgress;
import org.jdawg.util.FXUtils;

import javafx.fxml.FXML;
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
	private static final long ONE_THOUSAND = 1000;
	private static final long ONE_MILLION = 1000 * ONE_THOUSAND;
	private static final long ONE_BILLION = 1000 * ONE_MILLION;
	private static final long ONE_TRILLION = 1000 * ONE_BILLION;
	private static final long ONE_QUADRILLION = 1000 * ONE_TRILLION;
	private static final long ONE_QUINTILLION = 1000 * ONE_QUADRILLION;

	private static final String DATE_FORMAT = "%1$tF %1$tl:%1$tM:%1$tS%1$tp";
	private static final String DATE_FORMAT_APPROX_TIME = "%1$tF ~%1$tl:%1$tM:%1$tS%1$tp";

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
	private Label fieldLabelCalculations;

	@FXML
	private Label fieldLabelPctComplete;

	@FXML
	private Label fieldLabelStartTime;

	@FXML
	private Label fieldLabelStopTime;

	@FXML
	private Label fieldLabelCurrentRunTime;

	@FXML
	private Label fieldLabelEstRemainingTime;

	/**
	 * CoatProgressSummary constructor.
	 */
	public CoatProgressSummary( )
	{
		FXUtils.loadAsControlRoot( COMPONENT_FXML_FILENAME, this );

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
			builder.append( duration.toHoursPart( ) ).append( "h " );
			builder.append( duration.toMinutesPart( ) ).append( "m " );
			}
		else if ( duration.toMinutes( ) > 0 )
			{
			builder.append( duration.toMinutesPart( ) ).append( "m " );
			}

		builder.append( duration.toSecondsPart( ) ).append( "s" );

		return builder.toString( );

	} // formatDuration


	public static String getCountSummary( long value )
	{
		if ( value > ONE_QUINTILLION )
			return ( ( value / ONE_QUINTILLION ) + " Quin" );
		else if ( value > ONE_QUADRILLION )
			return ( ( value / ONE_QUADRILLION ) + " Q" );
		else if ( value > ONE_TRILLION )
			return ( ( value / ONE_TRILLION ) + " T" );
		else if ( value > ONE_BILLION )
			return ( ( value / ONE_BILLION ) + " B" );
		else if ( value > ONE_MILLION )
			return ( ( value / ONE_MILLION ) + " M" );
		else if ( value > ONE_THOUSAND )
			return ( ( value / ONE_THOUSAND ) + " K" );
		else
			return ( Long.toString( value ) );

	} // getRoughCount


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
			fieldLabelCalculations.setText( "" );
			fieldLabelPctComplete.setText( "" );
			fieldLabelStartTime.setText( "" );
			fieldLabelCurrentRunTime.setText( "" );
			fieldLabelEstRemainingTime.setText( "" );
			fieldLabelStopTime.setText( "" );
			}
		else
			{
			fieldLabelAlgorithm.setText( fieldCoatProgress.getConfig( ).getAlgorithmName( ) == null
					? "" : fieldCoatProgress.getConfig( ).getAlgorithmName( ) );
			fieldLabelRandomSeed.setText( fieldCoatProgress.getConfig( ).getRandomSeed( ) == null
					? "" : String.valueOf( fieldCoatProgress.getConfig( ).getRandomSeed( ) ) );
			fieldLabelIterationLimit.setText(
					( fieldCoatProgress.getConfig( ).getIterationLimit( ) <= 0 ? "None" : String
							.valueOf( fieldCoatProgress.getConfig( ).getIterationLimit( ) ) ) );
			fieldLabelIterations.setText( String.valueOf( fieldCoatProgress.getIterations( ) ) );
			fieldLabelCalculations
					.setText( getCountSummary( fieldCoatProgress.getCalculationsPerformed( ) ) );
			fieldLabelPctComplete.setText( String.format( "%.2f%%",
					100.0 * fieldCoatProgress.getEstimatedPercentComplete( ) ) );
			fieldLabelStartTime.setText( String.format( DATE_FORMAT,
					fieldCoatProgress.getStartTime( ).toEpochMilli( ) ) );
			fieldLabelCurrentRunTime.setText( formatDuration( fieldCoatProgress.getRunTime( ) ) );
			fieldLabelEstRemainingTime
					.setText( formatDuration( fieldCoatProgress.getEstimatedRemainingRunTime( ) ) );

			Instant stopTime = fieldCoatProgress.getOrEstimateStopTime( );

			if ( fieldCoatProgress.isComplete( ) || fieldCoatProgress.isCancelled( ) )
				{
				fieldProgressBar.setVisible( false );
				if ( stopTime != null )
					{
					fieldLabelStopTime
							.setText( String.format( DATE_FORMAT, stopTime.toEpochMilli( ) ) );
					}
				}
			else
				{
				fieldProgressBar.setVisible( true );
				fieldProgressBar.setProgress( fieldCoatProgress.getEstimatedPercentComplete( ) );
				if ( stopTime != null )
					{
					fieldLabelStopTime.setText(
							String.format( DATE_FORMAT_APPROX_TIME, stopTime.toEpochMilli( ) ) );
					}
				}
			}

	} // update

}
