package com.mnasser.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the maximum weight independent set of a graph.
 * 
 * That is, if given a path graph {@code G}, find the set of vertices in {@code G}, 
 * call this set {@code S}, such that each {@code v} in {@code S} does not have an edge 
 * with any of its neighbors in {@code G} and this set {@code S} must have maximum 
 * possible weights.  
 * </p>
 * Weights are non-negative values given to the vertices 
 * themselves.  (Edges have no weight).
 * 
 * @author Moe
 *
 */
public class WeightIndependentSet {

	/**
	 * Given a graph G with weighted vertices, this method 
	 * will return a sub graph S that is guaranteed to be an 
	 * independent set (no two vertices in S had edges between them in G) with maximum weight.
	 * </p>
	 * NOTE : the vertices of G must have at most 2 edges. This means, 
	 * G is effectively a like linked list 
	 * 
	 * @param p
	 * @return The total weight of maximum 
	 */
	public static int findWIS(PathGraph p){
		// We will need to do a Dynamic Programming approach for this.
		// 1 - assume some random starting vertex IS in the optimal solution S.
		//     If so, then find all vertices 2 hops away from it.
		//   (while at the same time )
		// 2 - assume that random vertex  is NOT in the optimal solution S. 
		//     If this is so, then we should recursively solve for this as well.
		
		// But of course, we can solve this problem by induction by solving 
		// smaller sub-problems of this one. Namely, starting from a graph of size
		// 0, then a size of 1, and so on and so forth.
		
		// Algo : 
		// Step 1 - base case - empty graph : max weight = 0
		// Step 2 - add first entry.
		// step 3 - pick max of (base case + 2nd entry) or ( first entry ).
		// step 4 - repeat 3 until reach end of graph
		
		//Vertex<Integer> curr = getTailVertex(p);
		int curr = getTailVertex(p);
		
		List<Integer> subproblems = new ArrayList<Integer>();
		subproblems.add( 0 );    // 0 - initial (empty) case; no weights
		subproblems.add( curr ); // 1 - if first is part of optimal solution
		
		
		// get next vertex on our path and continue
		//Vertex<Integer> next = null;
		//Vertex<Integer> previous = null;
		for( int ii = 1 ; ii < p.size(); ii ++ ){ //p.getVertexCount(); ii++){
			//next = getNextVertex(curr, previous);
			
			int subprob_num      = ii + 1; //
			int weight           = p.getVertexWeightAtIndex( ii );
			int maximized_weight = solveSubProblem( subproblems, subprob_num , weight);//next.element);
			subproblems.add( maximized_weight ); // save best solution for ith subproblem so far
			
			//	previous = curr;
			//	curr = next;
			//System.out.println( subproblems );
		}
		
		// once done, we now have the max possible solution 
		return Math.max(subproblems.get(p.size()), subproblems.get(p.size()-1));
	}
	
	// Attempts to find an edge of the graph
	// O(n)
	//	private static <T> Vertex<T> getTailVertex(Graph<T> g){
	//		Vertex<T> h = g.getVertices().get(0); // grab what could be a random vertex
	//		while( h.edges.size() != 1 ) {  // if it's not at the end of G, 
	//			h = h.getNeighbor(h.edges.get(0)); // grab random edge and keep looking.
	//		}
	//		return h;
	//	}
	private static int getTailVertex(PathGraph p){
		return p.getVertexWeightAtIndex(0);
	}
	
	//	private static <T> Vertex<T> getNextVertex(Vertex<T> curr, Vertex<T> previous){
	//		if ( previous == null ) // base case 
	//			if ( curr.edges.size() == 1 ) {
	//				return curr.edges.get(0).otherSide( curr );
	//			}
	//		
	//		for( Edge<T> e : curr.edges ){
	//			if ( ! e.isIncidentOn(previous) )
	//				return e.otherSide(curr);
	//		}
	//		
	//		return null;
	//	}
	
	/**
	 * Given cached results  for sub problems, returns the value of the {@code ith} 
	 * subproblem by returning the max of either the ( <b>ith</b> - 1) subproblem 
	 * or the  ( <b>ith</b>-2 subproblem + new weight), whichever is larger.
	 * */
	private static int  solveSubProblem( List<Integer> subproblems,  int i , int weight){ //int  i_1 , int i_2 ){
		if ( i < 1 ) return subproblems.get( i );
		// we maximize the max-weight independent set S by considering the 2 possibilities
		// for each subproblem along the path :
		// 1 ) S does NOT include me but does include my previous neighbor (subproblem i-1). OR
		// 2 ) S includes me and vertices 1 hop away from me (subproblem i-2).
		return Math.max( subproblems.get(i-1), subproblems.get(i-2) + weight );
	}

	
}
