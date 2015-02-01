package com.mnasser.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a path graph.  A path graph is a type of a graph
 * that consists solely of a single path over some number of vertices.
 * 
 * </p>
 * 
 * 
 * The only other restriction is a non-negative weight on vertices themselves.
 * Edges usually have no weight in the context of a simple path graph.
 * 
 * </p>
 * 
 * A path graph basically looks like a linked list :
 * <pre>
 * 1 --- 4 --- 5 --- 2 --- 1 --- 3
 * </pre>
 * 
 * Independent sets of the above path graph would be :
 * 
 * <pre>
 * {4, 2, 3}
 * or 
 * {1, 5, 1}
 * </pre>
 * 
 * The independent set that maximizes weight of the vertices in the set 
 * is <code>{4, 2, 3}</code>.
 * 
 * </p>
 * 
 * NOTE : Since this is a path graph, it follows that no single vertex can have more
 * than 2 edges.  With vertices on the left most or right most ends of the graph
 * having only 1 edge. 
 * </p>
 * 
 * @author Moe
 */
public class PathGraph  { //extends AdjacencyListGraph<Integer> {

	private List<Integer> path = new ArrayList<Integer>();
	
	public void appendWeightedVertex( int weight ){
		path.add( weight );
	}
	
	public synchronized void appendWeightedVertices( int... weights ){
		for( int w : weights ){
			path.add( w );
		}
	}
	
	public PathGraph(){}
	public PathGraph(int... weights){
		this();
		appendWeightedVertices(weights);
	}
	
	
	public int getVertexWeightAtIndex(int idx){
		return path.get(idx);
	}
	
	public void removeWeightedVertexAtIndex(int idx){
		path.remove( idx );
	}
	
	public void setWeightedVertexAtIndex(int idx, int weight) {
		path.set(idx, weight);
	}
	
	public int size(){
		return path.size();
	}
	
	public void clear(){
		path.clear();
	}
	
	public String toString(){
		return path.toString();
	}
	
	//	@Override
	//	public synchronized void addEdge(int ia, int ib, int cost) {
	//		addEdge( new Integer(ia) , new Integer(ib) ); // due to Integer/int ambiguity force the correct override
	//	};
	//	
	//	@Override
	//	protected synchronized void __addEdge(Edge<Integer> e) {
	//		if ( checkAddEdge( e.src, e.dst ) )
	//			super.__addEdge( e );
	//	}
	//	// enforces that there are not more than 2 edges incident on a single vertex 
	//	private boolean checkAddEdge(Vertex<Integer> a, Vertex<Integer> b){
	//		if ( a.hasNeighbor(b) ) return false;  // no to multiple edges between same 2 nodes
	//		if ( a.edges.size() > 1 || b.edges.size() > 1 ) // this can't happen
	//			throw new PathGraphException("Cannot add more than 2 edges maximum to any one node");
	//		return true;
	//	}
	//	// package visible
	//	@SuppressWarnings("serial")
	//	static class PathGraphException extends RuntimeException{
	//		public PathGraphException() {super();}
	//		public PathGraphException(String msg) {super(msg);}
	//	}
}
