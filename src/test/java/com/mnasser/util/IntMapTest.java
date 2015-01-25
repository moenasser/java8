package com.mnasser.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.mnasser.util.IntMap.Entry;

public class IntMapTest {

	@Test
	public void testAdd(){
		IntMap<String> is = new IntMap<String>();
		Assert.assertTrue( is.isEmpty() );
		
		is.put( 1 , "sally");
		
		Assert.assertEquals( "sally" , is.get(1) );
		Assert.assertEquals( 1 , is.size() );
		Assert.assertFalse( is.isEmpty() );
		Assert.assertEquals( 256 , is.capacity() );
		
		// not found
		Assert.assertNull( is.get( 1313 ) );
		
		Assert.assertTrue( is.containsKey( 1 ));
		Assert.assertFalse( is.containsKey( 4 ));
		
		
		Set<Entry<String>> es = is.entrySet();
		Assert.assertEquals( 1, es.size() );
		
		for( IntMap.Entry<String> e : es ){
			Assert.assertEquals( 1, e.key );
			Assert.assertEquals( "sally" , e.value );
		}
		
		Collection<String> vals = is.values();
		Assert.assertEquals( 1, vals.size() );
		for( String s : vals ){
			Assert.assertEquals( "sally" , s );
		}
		
		// note this will auto-box ints to Integers
		for (@SuppressWarnings("unchecked")
			Iterator<Integer> iterator = (Iterator<Integer>)is.keySet().iterator(); 
				iterator.hasNext() ;   ) {
			int ii = iterator.next();
			Assert.assertEquals( 1, ii);
		}
		
		// empty everything immediately
		is.clear();
		
		Assert.assertEquals( 0 , is.size() );
		Assert.assertNull( is.get(1) );
		Assert.assertTrue( is.isEmpty() );
		
		Assert.assertTrue( is.keySet().isEmpty() );
		Assert.assertTrue( is.entrySet().isEmpty() );
		Assert.assertTrue( is.values().isEmpty() );
		
	}
	
	@Test
	public void testOverwrite(){
		IntMap<String> is = new IntMap<String>();
		Assert.assertTrue( is.isEmpty() );
		
		is.put( 1 , "sally");
		is.put( 1 , "john");
		
		Assert.assertEquals( "john" , is.get(1) );
		Assert.assertEquals( 1 , is.size() );
	}
	
	@Test
	public void testRemove(){
		IntMap<String> is = new IntMap<String>();
		Assert.assertTrue( is.isEmpty() );
		
		is.put( 1 , "sally");
		
		is.remove( 1 );
		
		Assert.assertEquals( 0 , is.size() );
		Assert.assertTrue( is.isEmpty() );
		Assert.assertNull( is.get( 1 ) );
		
		Assert.assertTrue( is.keySet().isEmpty() );
		Assert.assertTrue( is.entrySet().isEmpty() );
		Assert.assertTrue( is.values().isEmpty() );
	}
	
	
	@Test
	public void testMultipleAddRemove(){
		IntMap<String> is = new IntMap<String>();
		
		is.put( 1, "sally");
		is.put( 2, "john" );
		is.put( 3, "frank");
		
		Assert.assertEquals( "sally" , is.get(1) );
		Assert.assertEquals( "john"  , is.get(2) );
		Assert.assertEquals( "frank" , is.get(3) );
		
		Assert.assertTrue( is.containsKey(1) );
		Assert.assertTrue( is.containsKey(2) );
		Assert.assertTrue( is.containsKey(3) );

		is.remove( 2 );
		Assert.assertEquals( "sally" , is.get(1) );		
		Assert.assertEquals( "frank" , is.get(3) );		
		Assert.assertTrue( is.containsKey(1) );
		Assert.assertTrue( is.containsKey(3) );
		
		Assert.assertNull( is.get(2) );
		Assert.assertFalse( is.containsKey(2) );
	}
	
	@Test
	public void testResize(){
		IntMap<String> is = new IntMap<String>( 2 , 2 ); // 4 slots only!
		
		is.put( 1 , "sally"); // first  bucket
		is.put( 2 , "john");  // second bucket
		
		is.put( 3 , "frank"); // first bucket again 
		is.put( 4,  "alice"); // second bucket.
		// we should be full
		
		Assert.assertEquals( is.size() , is.capacity() );
		
		is.put( 5 ,  "bob"); // resize & re-hashing of all keys should have been triggered
		
		Assert.assertEquals( 5 , is.size());
		Assert.assertEquals( 16 , is.capacity() ); // 4x space 
		
		
		is.put( 16 , "joe");
		is.put( 32, "shmoe"); // will fill up both slots of the 0th buckets
		
		Assert.assertEquals( 7 , is.size());
		Assert.assertEquals( 16 , is.capacity() ); // hasn't changed yet
		
		is.put( 48 , "moe"); // resize
		
		Assert.assertEquals( 8 , is.size());
		Assert.assertEquals( 64 , is.capacity() ); // 4x again
	}
}
