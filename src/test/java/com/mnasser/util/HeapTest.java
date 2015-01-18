package com.mnasser.util;

import org.junit.Assert;
import org.junit.Test;

import com.mnasser.util.Heap.HEAP;

public class HeapTest {

	@Test
	public void testHeap(){
		
		
		// Assert math for indices of children is sound  
		Assert.assertEquals( 1 , Heap.getFirstChild(0) );
		Assert.assertEquals( 2 , Heap.getFirstChild(0) + 1 );
		Assert.assertEquals( 3 , Heap.getFirstChild(1));
		Assert.assertEquals( 4 , Heap.getFirstChild(1) + 1 );
		Assert.assertEquals( 5 , Heap.getFirstChild(2));
		Assert.assertEquals( 6 , Heap.getFirstChild(2) + 1 );
		
		Assert.assertEquals( 0 , Heap.getParent(1) );
		Assert.assertEquals( 0 , Heap.getParent(2) );
		Assert.assertEquals( 1 , Heap.getParent(3) );
		Assert.assertEquals( 1 , Heap.getParent(4) );
		Assert.assertEquals( 2 , Heap.getParent(5) );
		Assert.assertEquals( 2 , Heap.getParent(6) );
		
		// use Lambda shorthand for an Integer comparator 
		Heap<Integer> h = new Heap<Integer>( (Integer i1, Integer i2) -> i1.compareTo(i2) );
		
		h.insert( 13 );
		h.insert( 13 );
		h.insert( 13 );
		h.insert( 13 );
		h.insert( 3 );
		h.insert( 5 );
		
		Heap.print(h);
		Assert.assertEquals ( 3 , h.peek().intValue() );
		
		h.insert( 1 );
		Assert.assertEquals ( 1 , h.peek().intValue() );
		Heap.print(h);
		
		h.insert( 2 );
		Assert.assertEquals ( 1 , h.peek().intValue() );
		Heap.print(h);
		
		System.out.println( h.removeRoot() );
		Assert.assertEquals ( 2 , h.peek().intValue() );
		Heap.print(h);
		
		System.out.println( h.removeRoot() );
		Assert.assertEquals ( 3 , h.peek().intValue() );
		Heap.print(h);
		
		h.insert( 13 );
		Assert.assertEquals ( 3 , h.peek().intValue() );
		Heap.print(h);
		
		System.out.println( h.removeRoot() );
		Assert.assertEquals ( 5 , h.peek().intValue() );
		Heap.print(h);
		

		h.insert( 1 );
		Assert.assertEquals ( 1 , h.peek().intValue() );
		Heap.print(h);
		
		System.out.println( h.removeRoot() );
		Assert.assertEquals ( 5 , h.peek().intValue() );
		Heap.print(h);
	}
	
	@Test
	public void testHeapComparable(){
		Heap<String> h = new HeapComparable<String>( HEAP.MIN_HEAP );
		
		h.insert("hi");
		h.insert("hello");
		h.insert("moe");
		h.insert("-");
		h.insert("go");
		h.insert("apple");
		h.insert("zebra");
		
		Heap.print(h);
		Assert.assertEquals( 7 , h.size() );
		
		
		String root = h.removeRoot();
		
		Heap.print(h);
		Assert.assertEquals("-", root);
		Assert.assertEquals( 6 , h.size() );
		
		root = h.removeRoot();
		
		Heap.print(h);
		Assert.assertEquals("apple", root);
		Assert.assertEquals( 5 , h.size() );
		
		h.insert("apple");
		h.insert("A-Team");
		
		Heap.print(h);
		Assert.assertEquals("A-Team",  h.peek() );
		Assert.assertEquals( 7 , h.size() );

	}
	
	@Test( expected = RuntimeException.class )
	public void testMaxCapacity(){
		Heap<Integer> h = new HeapComparable<Integer>( 2 );
		Assert.assertEquals( 2 , h.capacity() );
		
		Assert.assertTrue( h.hasRoom() );
		
		h.insert(1);
		h.insert(2);
		
		Assert.assertEquals( 2 , h.size() );
		
		Assert.assertFalse( h.hasRoom() );
		
		// throws
		h.insert(3);
	}
	
	@Test
	public void testRemove(){
		Heap<Integer> h = new HeapComparable<>();
		
		h.insert( 1 );
		h.insert( 2 );
		
		Assert.assertEquals( 1, h.removeRoot().intValue() );
		Assert.assertEquals( 2, h.removeRoot().intValue() );
		
		Assert.assertEquals( 0 , h.size() );
	}
	
	@Test
	public void testOfferAndForce(){
		Heap<Integer> h = new HeapComparable<>( 1 );
		
		h.insert( 1 );
		
		Assert.assertFalse( h.hasRoom() ); // no more room
		
		Assert.assertFalse( h.offer( 2 ) ); // offer will fail; no room
		
		Assert.assertEquals( 1 , h.force( 2 ).intValue() ); // force will pop root
		
		
	}
	
}

