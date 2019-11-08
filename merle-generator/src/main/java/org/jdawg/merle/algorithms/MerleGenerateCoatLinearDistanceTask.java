/*
 * MerleGenerateCoatLinearDistanceTask.java
 * 
 * Created on Jun 5, 2018
 */
package org.jdawg.merle.algorithms;

import org.jdawg.merle.AbstractGenerateCoatTask;

/**
 * MerleGenerateCoatLinearDistanceTask overrides the default MerleGenerateCoatTask's
 * signal strength function with a linear decay function: signal strength decays over the
 * distance between the two points.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class MerleGenerateCoatLinearDistanceTask extends MerleGenerateCoatTask
{
	// LAM - It may be better to allow ColorGenes to specify their own bits of expression
	// code to be wrapped as configurable functions and used to decay signals without
	// extending the original Merle algorithm class?

	/**
	 * Builds an instance of MerleGenerateCoatLinearDistanceTask.
	 * 
	 * @author David Schmidt (dschmidt13@gmail.com)
	 */
	public static class Builder extends MerleGenerateCoatTask.Builder
	{
		/**
		 * Public constructor.
		 */
		public Builder( )
		{
		} // Builder


		@Override
		protected AbstractGenerateCoatTask createInstance( )
		{
			return new MerleGenerateCoatLinearDistanceTask( );

		} // createInstance

	} // Builder

	/**
	 * The name and factory key for this algorithm.
	 */
	public static final String ALGORITHM_NAME = "Merle-DistanceInverse";


	/**
	 * Private constructor. Instantiate via Builder.
	 */
	private MerleGenerateCoatLinearDistanceTask( )
	{
	} // MerleGenerateCoatLinearDistanceTask


	@Override
	protected double degradeSignal( double signalStrength, double distance )
	{
		return signalStrength / distance;

	} // degradeSignal


	@Override
	public String getAlgorithmName( )
	{
		return ALGORITHM_NAME;

	} // getAlgorithmName

}
