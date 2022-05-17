/*
 * ColorPoint.java
 * 
 * Created on Jun 4, 2018
 */
package org.jdawg.merle;

import java.awt.Point;

import javafx.scene.paint.Color;

/**
 * ColorPoint pairs a 2D point with a Color. Used to pipeline multi-pass rendering.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class ColorPoint
{
	// Data members.
	private final Color fieldColor;
	private final Point fieldPoint;


	/**
	 * ColorPoint constructor.
	 */
	public ColorPoint( Point point, Color color )
	{
		fieldPoint = point;
		fieldColor = color;

	} // ColorPoint


	public Color getColor( )
	{
		return fieldColor;

	} // getColor


	public double getX( )
	{
		return fieldPoint.getX( );

	} // getX


	public double getY( )
	{
		return fieldPoint.getY( );

	} // getY

}
