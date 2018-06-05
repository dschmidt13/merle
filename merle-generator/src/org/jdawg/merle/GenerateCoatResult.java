/*
 * GenerateCoatResult.java
 * 
 * Created on Jun 3, 2018
 */
package org.jdawg.merle;

import javafx.scene.image.WritableImage;

/**
 * GenerateCoatResult is a simple wrapper object to store a result from generating a coat
 * pattern and some statistics about the run, e.g. how long it took.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class GenerateCoatResult
{
	// Data members.
	private long fieldRunTimeMs;
	private int fieldPassCount;
	private WritableImage fieldCoatPattern;


	/**
	 * GenerateCoatResult constructor.
	 */
	public GenerateCoatResult( )
	{
	} // GenerateCoatResult


	/**
	 * @return WritableImage - the fieldCoatPattern
	 */
	public WritableImage getCoatPattern( )
	{
		return fieldCoatPattern;

	} // getCoatPattern


	/**
	 * @return int - the fieldPassCount
	 */
	public int getPassCount( )
	{
		return fieldPassCount;

	} // getPassCount


	/**
	 * @return long - the fieldRunTimeMs
	 */
	public long getRunTimeMs( )
	{
		return fieldRunTimeMs;

	} // getRunTimeMs


	/**
	 * @param coatPattern - a WritableImage to set as the fieldCoatPattern
	 */
	public void setCoatPattern( WritableImage coatPattern )
	{
		fieldCoatPattern = coatPattern;

	} // setCoatPattern


	/**
	 * @param passCount - a int to set as the fieldPassCount
	 */
	public void setPassCount( int passCount )
	{
		fieldPassCount = passCount;

	} // setPassCount


	/**
	 * @param runTimeMs - a long to set as the fieldRunTimeMs
	 */
	public void setRunTimeMs( long runTimeMs )
	{
		fieldRunTimeMs = runTimeMs;

	} // setRunTime

}
