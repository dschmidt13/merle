/*
 * GenerateAlgorithm.java
 * 
 * Created: Sep 21, 2020
 */
package org.jdawg.merle.algorithms;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * GenerateAlgorithm contains information used to label pattern generation algorithms to a
 * set of descriptions that are accessible to the user. They provide a static external
 * handle to code apart from the fully-qualified class name (which may yet change).
 * <p>
 * Currently, they serve specifically as the name and factory key for the algorithm.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
@Retention( RUNTIME )
@Target( TYPE )
public @interface GenerateAlgorithm
{
	/**
	 * The unique value of the handle describing this algorithm.
	 */
	String value( );

}
