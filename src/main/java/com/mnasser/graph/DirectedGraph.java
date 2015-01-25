package com.mnasser.graph;


/**
 * A directed graph. 
 * </p>
 * This means that all edges have a direction from a source vertex to a 
 * destination vertex.  Being direct it means that one can only traverse edges
 * on this graph in one direction : from outbound to inbound. 
 * </p>
 * To make a bi-directional pair of vertices (from which you can traverse back
 * and forth between them) you will need 2 edges : one for each direction. 
 * 
 * @author Moe
 *
 */
public class DirectedGraph<V> extends AdjacencyListGraph<V>{

	@Override public boolean isDirected() { return true;}
	
	//private boolean reversed = false;
	private Vertex<V> max = null;
	private Vertex<V> min = null;
	

	public DirectedGraph() {
		super();
	}
	public DirectedGraph(int i) {
		super(i);
	}

	
	@Override
	public void addEdge(Edge<V> e) {
		// no parallel edges 
		if( ! hasEdge(e) )
			super.addEdge(e);
	}
	
	@Override
	public Vertex<V> addVertex(Vertex<V> node) {
		Vertex<V> v1 = super.addVertex(node);
		max = (max == null)? node : (max.id < node.id)? node : max;
		min = (min == null)? node : (min.id > node.id)? node : min;
		return v1;
	}
	
	Vertex<V> getMax(){ return this.max; }
	Vertex<V> getMin(){ return this.min; }
	
	public void clearVisited(){
		for( Vertex<V> a : getVertices() ){
			a.visited = false;
		}
	}
	
	public void clearOrdering(){
		for( Vertex<V> a : getVertices() ){
			a.order = -1;
		}
	}

	/*
	void reverse(){
		this.reversed = true;
	}
	void unReverse(){
		this.reversed = false;
	}
	void setReverse(boolean rev){
		this.reversed = rev;
	}
	boolean isRevered(){ return this.reversed; }
	*/
	
	/*
	DirectedGraph reverseClone(){
		DirectedGraph g = (DirectedGraph) Graph.copyOf(this);
		g.reversed = ! this.reversed;
		return g;
	}
	*/
}
