package com.mnasser.util;

import java.util.HashMap;
import java.util.Map;

public class CountingMap<K> {

	private final Map<K,Integer> map = new HashMap<K, Integer>();
	private int size = 0;
	
	public int inc(K key){
		Integer i = map.get(key);
		size ++;
		return (i==null || i < 0) ?  map.put(key, 1 )  : map.put( key , i + 1 );
	}
	
	public boolean has(K key){
		Integer i = map.get( key );
		return (i==null || i == 0 );
	}
	
	public int dec(K key){
		Integer i = map.get(key);
		size--;
		return (i==null || i < 1) ? 0 : map.put( key, i - 1);
	}
	
	public void      clear() { map.clear(); }
	public boolean isEmpty() { return map.isEmpty();  }
	
	//total count of elements 
	public int  size() { return size; }
	
}
