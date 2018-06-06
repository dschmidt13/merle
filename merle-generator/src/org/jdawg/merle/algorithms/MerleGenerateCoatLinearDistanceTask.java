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

}
