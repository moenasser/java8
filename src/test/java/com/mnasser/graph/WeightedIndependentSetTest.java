package com.mnasser.graph;

import org.junit.Assert;
import org.junit.Test;

public class WeightedIndependentSetTest {

	@Test
	public void testWIS(){
		PathGraph p = new PathGraph();
		
		// let's make the path graph : 1 - 4 - 5 - 2 - 1 - 3
		p.appendWeightedVertices( 1 , 4, 5, 2, 1, 3 ); 
		System.out.println( p );
		
		int max_weight = WeightIndependentSet.findWIS(p);
		
		Assert.assertEquals( 9, max_weight);
		
		
		Assert.assertEquals( 15, WeightIndependentSet.findWIS(new PathGraph(1,5,1,5,1,5,1)));
	}
}
