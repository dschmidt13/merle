/*
 * ColorGene.java
 * 
 * Created on May 19, 2018
 */
package org.jdawg.merle;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

/**
 * ColorGene is a model object that associates a name, a color, and some algorithm
 * parameters. The parameters are related to tuning the occurrence of that color within a
 * generated merle image.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class ColorGene
{
	// Data members.
	private StringProperty name = new SimpleStringProperty( "Black" );
	private ObjectProperty<Color> color = new SimpleObjectProperty<>( Color.BLACK );
	private DoubleProperty seedConversionProb = new SimpleDoubleProperty( 0.15 );
	private DoubleProperty coolingRate = new SimpleDoubleProperty( 0.2 );
	private DoubleProperty signalStrength = new SimpleDoubleProperty( 50 );


	public ColorGene( )
	{
	} // ColorGene;


	public ColorGene( ColorGene colorGene )
	{
		name = colorGene.name;
		color = colorGene.color;
		seedConversionProb = colorGene.seedConversionProb;
		coolingRate = colorGene.coolingRate;
		signalStrength = colorGene.signalStrength;

	} // ColorGene


	public ObjectProperty<Color> colorProperty( )
	{
		return color;

	} // colorProperty


	public DoubleProperty coolingRateProperty( )
	{
		return coolingRate;

	} // coolingRateProperty


	public Color getColor( )
	{
		return colorProperty( ).get( );

	} // getColor


	public double getCoolingRate( )
	{
		return coolingRateProperty( ).get( );

	} // getCoolingRate


	public String getName( )
	{
		return nameProperty( ).get( );

	} // getName


	public double getSeedConversionProb( )
	{
		return seedConversionProbProperty( ).get( );

	} // getSeedConversionProb


	public double getSignalStrength( )
	{
		return signalStrengthProperty( ).get( );

	} // getSignalStrength


	public StringProperty nameProperty( )
	{
		return name;

	} // nameProperty


	public DoubleProperty seedConversionProbProperty( )
	{
		return seedConversionProb;

	} // seedConversionProbProperty


	public void setColor( Color color )
	{
		colorProperty( ).set( color );

	} // setColor


	public void setCoolingRate( double coolingRate )
	{
		coolingRateProperty( ).set( coolingRate );

	} // setCoolingRate


	public void setName( String name )
	{
		nameProperty( ).set( name );

	} // setName


	public void setSeedConversionProb( double seedConversionProb )
	{
		seedConversionProbProperty( ).set( seedConversionProb );

	} // setSeedConversionProb


	public void setSignalStrength( double signalStrength )
	{
		signalStrengthProperty( ).set( signalStrength );

	} // setSignalStrength


	public DoubleProperty signalStrengthProperty( )
	{
		return signalStrength;

	} // signalStrengthProperty

}
