package com.mnasser.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A hash map that uses primitive {@code int}s as keys
 * to save on the over head of working with Integer objects
 *
 * @param <V> The type of Values to map to.
 * @author Moe
 */
@SuppressWarnings("rawtypes")
public class IntMap<V> implements Map {

	public IntMap(){}
	public IntMap(int initialCapacity){
		bucketSize = 4;
		bucketCount = initialCapacity / bucketSize;
		if ( bucketCount < 32 ) bucketCount = 32;
		_init();
	}
	public IntMap(int bucketSize, int bucketCount){
		this.bucketCount = (bucketCount < 2)? 2 : bucketCount;
		this.bucketSize = (bucketSize < 2)? 2 : bucketSize;
		_init();
	}
	
	@SuppressWarnings("unchecked")
	private void _init(){
		slots = bucketCount * bucketSize;
		bucketUsage = new int[bucketCount];
		buckets = new Entry[slots];
	}
	
	
	private int bucketSize  = 4; // size of each bucket
	private int bucketCount = 32; // total number of buckets
	private int slots = bucketSize * bucketCount;
	
	@SuppressWarnings("unchecked")
	// our actual backing array where we throw things into  
	// notice, it's not actual buckets - rather a range of entries in an array
	private Entry<V>[] buckets = new Entry[ slots ];
	
	// how many entries in each bucket have been used
	private int[] bucketUsage = new int[bucketCount];
	private int entries = 0;  // current entries into this map 
	
	@SuppressWarnings({"unchecked"})
	public static class Entry<V>{
		final int key;
		final V value; // optional parameterized value to hold
		
		Entry(int k)       {  key = k; value = null; }
		Entry(int k, V val){  key = k; value = val; }
		
		public boolean equals(int other){
			return key == other;
		}
		
		public boolean equals(Object obj) {
			if( this == obj ) return true;
			if( obj instanceof Entry<?> ){
				Entry<V> e = (Entry<V>)obj;
				if (this.key  != e.key ) return false;
				if (this.value != null ) return this.value.equals(e.value);
			}
			return false;
		}
		
		@Override
		public String toString(){
			return String.format("{%s->%s}", key, value);
		}
	}
	
	// figures out which 'bucket' this key would hash into
	private int bucket(int key){
		int mod = key % bucketCount;
		if ( mod < 0 ) mod = mod + bucketCount; // corrected modulus
		return mod;
	}
	
	public V put( int key , V value ){
		V val = null;
		while( (val = _addEntry( buckets, bucket( key ), new Entry<V>(key , value) )) == null ){
			resize();
		}
		return val;
	}
	
	/**Enters the given entry into the given bucket array.
	 * @return The entry value that was added or the previous value if 
	 * this key is being over written.  Null if there was no room in this bucket.*/
	private V _addEntry(Entry<V>[] buckets, int bucket, Entry<V> entry){
		int start = bucket * bucketSize; // first slot available 
		for( int ii = start, end = start+bucketSize ; ii < end; ii++){
			if( buckets[ii] == null ){
				buckets[ii] = entry;
				entries ++;
				bucketUsage[bucket] ++;
				return entry.value;
			}else{
				if( buckets[ii].key == entry.key ){ // replace an old value
					V old = buckets[ii].value;
					buckets[ii] = entry;
					return old;
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private synchronized boolean resize(){
		this.bucketCount = this.bucketCount * 4; // more buckets
		this.slots = bucketCount * bucketSize;   // more slots 
		this.bucketUsage = new int[bucketCount]; 
		this.entries = 0; //start over
		// start filling new bucket area
		Entry<V>[] newBuckets = new Entry[ slots ];
		boolean success = true ;
		for( Entry<V> e : buckets ){
			if ( e != null ) {
				if ( _addEntry( newBuckets,  bucket(e.key), e ) == null ){
					// couldn't add entry even after a resize? 
					// fail
					newBuckets = null;
					success = false;
					break;
				}
			}
		}
		
		if( success && ! needsMoreRoom() ){
			this.buckets = null;
			this.buckets = newBuckets;
			//System.out.println(" IntMap resized! New capacity : " + slots);
			return true;
		}
		return resize(); // do it again until we actually fit everything
	}
	/**Returns true if any of our buckets are already at capacity*/
	private boolean needsMoreRoom(){
		for( int usage : bucketUsage ){
			if ( usage >= bucketSize )
				return true; // we already don't have room.
		}
		return false;
	}

	@Override
	public int size() {
		return entries;
	}

	@Override
	public boolean isEmpty() {
		return entries == 0;
	}

	public int capacity(){
		return slots;
	}
	
	@Override
	public boolean containsKey(Object key) {
		throw new RuntimeException("Unsupported Method! Use containsKey(int k) instead.");
	}
	public boolean containsKey( int key ){
		int bucket = bucket( key );
		if ( bucketUsage[bucket] == 0 ) return false; // we have nothing here
		for( int ii = bucket*bucketSize, len = ii + bucketSize; ii < len; ii ++ ){
			if( buckets[ii].key == key  )
				return true;
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO: can implement in O(n) time
		throw new RuntimeException("Unsupported Method!");
	}

	@Override
	public Object get(Object key) {
		throw new RuntimeException("Unsupported Method! Use get(int k) instead.");
	}
	public V get(int key){
		int bucket = bucket( key );
		if ( bucketUsage[bucket] == 0 ) return null; // we have nothing here
		for( int ii = bucket*bucketSize, len = ii + bucketSize; ii < len; ii ++ ){
			if( buckets[ii] != null && buckets[ii].key == key  )
				return buckets[ii].value;
		}
		return null;
	}

	@Override
	public Object put(Object key, Object value) {
		throw new RuntimeException("Unsupported Method! Use get(int k) instead.");
	}

	@Override
	public Object remove(Object key) {
		throw new RuntimeException("Unsupported Method! Use remove(int key) instead.");
	}
	public V remove(int key){
		int bucket = bucket( key );
		if ( bucketUsage[bucket] == 0 ) return null; // we have nothing here
		for( int ii = bucket*bucketSize, len = ii + bucketSize; ii < len; ii ++ ){
			if( buckets[ii].key == key  ){
				Entry<V> e = buckets[ii];
				buckets[ii] = null;
				bucketUsage[bucket]--;
				entries --;
				return e.value;
			}
		}
		return null;
		
	}

	@Override
	public void putAll(Map m) {
		throw new RuntimeException("Unsupported Method!");
	}

	@Override
	@SuppressWarnings("unchecked")
	public synchronized void clear() {
		buckets = null;
		entries = 0;
		bucketUsage = new int[bucketCount];
		buckets = new Entry[ slots ];
	}

	@Override
	public Set keySet() {
		return Arrays.stream( buckets )
				.filter( b -> b != null )
				.map( b -> b.key )
				.collect( Collectors.toSet() );
	}

	@Override
	public Collection<V> values() {
		return Arrays.stream( buckets )
				.filter( b -> b != null )
				.map( b -> b.value )
				.collect( Collectors.toList() ); // might hurt resources if extremely large 
	}

	@Override
	public Set<Entry<V>> entrySet() {
		return Arrays.stream( buckets )
				.filter( e -> e != null )
				.collect( Collectors.toSet() );
	}
	
	
}
