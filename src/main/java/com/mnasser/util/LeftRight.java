package com.mnasser.util;


/**
 * Represents a 2-Tuple.
 * 
 * Use when needing to return multiple values from functions.
 * 
 * @author mnasser
 *
 */
public class LeftRight<L,R> {
	
	public final L left;
	public final R right;
	
	public LeftRight(L l, R r){
		left = l;
		right = r;
	}

	public static <L,R> LeftRight<L,R> lr(L l, R r) {
		return new LeftRight<L,R>(l,r);
	}
}
