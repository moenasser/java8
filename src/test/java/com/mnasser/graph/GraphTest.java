package com.mnasser.graph;

import org.junit.Assert;
import org.junit.Test;

import com.mnasser.graph.AdjacencyListGraph.BadVertexAddition;
import com.mnasser.graph.Graph.Vertex;

// added to test Generic (paramterized) graphs
public class GraphTest {

	@Test
	public void testParameterizedGraph() {
		String john = "John";
		
		Graph<String> s = Graph.getInstance();
		
		Vertex<String> vj = s.addVertex( john );
		
		Assert.assertEquals( 1 , s.getVertexCount() );
		Assert.assertEquals( 1 , vj.id );
		
		Assert.assertEquals( vj, s.getVertex( 1 ) );
		Assert.assertEquals( john , s.getVertex( "John" ).element );
		Assert.assertTrue( vj == s.getVertex( "John" ) ); // should be same reference in this graph instance as well
		
		Assert.assertTrue( s.hasVertex( vj ) );
		Assert.assertTrue( s.hasVertex( 1 ) );
		Assert.assertTrue( s.hasVertex( "John" ) );
		
		Assert.assertFalse( s.hasVertex( new Vertex<String>(-1 , "apple" ) ) ); 
		Assert.assertFalse( s.hasVertex( 100_000 ) ); // unkown ID
		Assert.assertFalse( s.hasVertex( "apple" ) ); // unknown element 'apple'
		
		
		Vertex<String> vs = s.addVertex( 75 , "Sally" ); // this should increment graph's internal ID generator to 76
		
		s.addEdge( vj , vs );
		
		Assert.assertEquals( 1 , s.getEdgeCount() );
		
		s.addEdge( "Sally", "John" );
		
		Assert.assertEquals( 2 , s.getEdgeCount() );
	}
	
	
	@Test
	(expected=BadVertexAddition.class)
	public void testBadVertexAdditionException(){
		Graph<String> s = Graph.getInstance();
		
		s.addVertex( "john" );
		
		s.addVertex( 55 , "john" ); //  throws
	}
	
	@Test
	(expected=NullPointerException.class)
	public void testNullElementException(){
		Graph<String> s = Graph.getInstance();
		
		// s.addVertex( null  ); //  ambiguous since we don't know if it's a String or Vertex overload
		
		s.addVertex( (String) null ); // throws
	}
	
	@Test
	(expected=NullPointerException.class)
	public void testNullVertexException(){
		Graph<String> s = Graph.getInstance();
		
		// s.addVertex( null  ); //  ambiguous since we don't know if it's a String or Vertex overload
		
		s.addVertex( (Vertex<String>) null ); // throws
	}
	
	@Test
	public void testIntOrInteger(){
		Graph<Integer> i = Graph.getInstance();
		
		i.addVertex( 1 ); // the non-autoBoxed method is called. Meaning this is an ID.
		i.addVertex( new Integer( 1 ) ); // this is a class - so the element function is called
		
		Assert.assertEquals( 2 , i.getVertexCount() );
		Assert.assertEquals( 0 , i.getEdgeCount() );
		
		Vertex<Integer> v1 = i.getVertex( 1 );
		Vertex<Integer> v2 = i.getVertex( new Integer( 1 ) );
		
		Assert.assertNotEquals( v1, v2 );
		Assert.assertNull( v1.element );
		Assert.assertNotNull( v2.element );
		
		Assert.assertEquals( 2 , v2.id );
	}
	
	@Test
	public void testRandomGraph(){
		@SuppressWarnings("rawtypes")
		Graph g = Graph.makeRandomGraph( 10_000 );
		
		Assert.assertEquals( 10_000 , g.getVertexCount() );
		Assert.assertEquals( 15_000 , g.getEdgeCount() );
	}

}

