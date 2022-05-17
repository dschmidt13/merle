/*
 * MerleGenerateCoatLinearDistanceTask.java
 * 
 * Created on Jun 5, 2018
 */
package org.jdawg.merle.algorithms;

import org.jdawg.merle.config.GenerateConfig;

/**
 * MerleGenerateCoatLinearDistanceTask overrides the default MerleGenerateCoatTask's
 * signal strength function with a linear decay function: signal strength decays over the
 * distance between the two points.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
@GenerateAlgorithm( "Merle-DistanceInverse" )
public class MerleGenerateCoatLinearDistanceTask extends MerleGenerateCoatTask
{
	// LAM - It may be better to allow ColorGenes to specify their own bits of expression
	// code to be wrapped as configurable functions and used to decay signals without
	// extending the original Merle algorithm class?

	public MerleGenerateCoatLinearDistanceTask( GenerateConfig config )
	{
		super( config );

	} // MerleGenerateCoatLinearDistanceTask


	@Override
	protected double degradeSignal( double signalStrength, double distance )
	{
		return signalStrength / distance;

	} // degradeSignal

}
