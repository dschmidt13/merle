/*
 * ColorGeneEditResult.java
 * 
 * Created on Jun 12, 2018
 */
package org.jdawg.merle;

import javafx.scene.image.Image;

/**
 * ColorGeneEditResult
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class ColorGeneEditResult
{
	// Data members.
	private Image fieldPatternSample;
	private ColorGene fieldColorGene;


	/**
	 * ColorGeneEditResult constructor.
	 */
	public ColorGeneEditResult( )
	{
	} // ColorGeneEditResult


	/**
	 * @return ColorGene - the fieldColorGene
	 */
	public ColorGene getColorGene( )
	{
		return fieldColorGene;

	} // getColorGene


	/**
	 * @return Image - the fieldPatternSample
	 */
	public Image getPatternSample( )
	{
		return fieldPatternSample;

	} // getPatternSample


	/**
	 * @param colorGene - a ColorGene to set as the fieldColorGene
	 */
	public void setColorGene( ColorGene colorGene )
	{
		fieldColorGene = colorGene;

	} // setColorGene


	/**
	 * @param patternSample - a Image to set as the fieldPatternSample
	 */
	public void setPatternSample( Image patternSample )
	{
		fieldPatternSample = patternSample;

	} // setPatternSample

}
