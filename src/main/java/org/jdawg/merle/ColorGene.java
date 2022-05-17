/*
 * ColorGene.java
 * 
 * Created on May 19, 2018
 */
package org.jdawg.merle;

import java.util.Objects;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

/**
 * ColorGene is a model object that associates a name, a color, and some algorithm parameters. The parameters are
 * related to tuning the occurrence of that color within a generated merle image.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class ColorGene
{
	// TODO - Decouple config data model from FX view model.

	// Data members.
	private StringProperty name = new SimpleStringProperty( );
	private ObjectProperty<Color> color = new SimpleObjectProperty<>( );
	private DoubleProperty seedConversionProb = new SimpleDoubleProperty( );
	private DoubleProperty coolingRate = new SimpleDoubleProperty( );
	private DoubleProperty signalStrength = new SimpleDoubleProperty( );

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


	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;

		if ( !( obj instanceof ColorGene ) )
			return false;

		ColorGene other = ( ColorGene ) obj;
		return ( Objects.equals( this.color.getValue( ), other.color.getValue( ) )
				&& Objects.equals( this.coolingRate.getValue( ), other.coolingRate.getValue( ) )
				&& Objects.equals( this.name.getValue( ), other.name.getValue( ) )
				&& Objects.equals( this.seedConversionProb.getValue( ), other.seedConversionProb.getValue( ) )
				&& Objects.equals( this.signalStrength.getValue( ), other.signalStrength.getValue( ) ) );

	} // equals


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


	@Override
	public int hashCode( )
	{
		return Objects.hash( this.color.getValue( ), this.coolingRate.getValue( ), this.name.getValue( ),
				this.seedConversionProb.getValue( ), this.signalStrength.getValue( ) );

	} // hashCode


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
