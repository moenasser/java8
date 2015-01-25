package com.mnasser.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A generic representation of a graph.  It consists of edges E and vertices V.
 * 
 * @author Moe
 */
public abstract class Graph<V> {

	/**Factory constructor**/
	public static <X> Graph<X> getInstance(){
		return new AdjacencyListGraph<X>();
	}
	
	/**Factory method returning a simple comparator which uses edge costs
	 * to determine ordering. */
	public static <X> Comparator<Edge<X>> getEdgeComparator(){
		// Use lambda shorthand to let jvm know we should compare on the int cost of an edge 
		return Comparator.comparingInt( (Edge<X> e) -> e.cost() );
	}
	
	/**Returns a list of all vertices in this graph.*/ 
	public abstract List<Vertex<V>> getVertices();
	/**Returns a list of all edges in this graph.*/ 
	public abstract List<Edge<V>>   getEdges();
	
	/**Inserts a new vertex with element {@code v}.  An ID is generated for the newly formed
	 * vertex if one isn't given*/
	public abstract Vertex<V> addVertex(V element);
	/**Inserts a new vertex with element {@code v} and ID {@code id}.*/
	public abstract Vertex<V> addVertex(int id , V element);
	public abstract Vertex<V> addVertex(int id) ;
	public abstract Vertex<V> addVertex(Vertex<V> v);
	
	public abstract void      addEdge  (Vertex<V> a, Vertex<V> b);
	public abstract void      addEdge  (Vertex<V> a, Vertex<V> b, int cost);
	public abstract void      addEdge  (Edge<V> e);
	public abstract void      addEdge  (int a, int b);
	public abstract void      addEdge  (int a, int b, int cost);
	public abstract void      addEdge  (V a , V b);
	public abstract void      addEdge  (V a , V b, int cost);
	
	/**Returns true if there exists a Vertex with internal 
	 * element equal to {@code v}. 
	 * @throws NullPointerException If {@code v} is null*/
	public abstract boolean   hasVertex(V element);
	public abstract boolean   hasVertex(Vertex<V> a);
	public abstract boolean   hasVertex(int id);
	
	public abstract boolean   hasEdge  (Edge<V> e);
	public abstract boolean   hasEdge  (Vertex<V> a, Vertex<V> b);
	public abstract boolean   hasEdge  (int a, int b);
	
	/**Returns the first Vertex with element equal to {@code v}. 
	 * @throws NullPointerException If {@code v} is null*/
	public abstract Vertex<V> getVertex(V element);
	public abstract Vertex<V> getVertex(int id);
	public abstract Vertex<V> getVertex(Vertex<V> a);
	
	public abstract Edge<V>   getEdge  (Vertex<V> a, Vertex<V> b);
	public abstract Edge<V>   getEdge  (int a, int b);
	
	
	
	
	
	public boolean isDirected(){ return false ; }
	public abstract boolean hasDisjointNodes();
	
	public abstract int getEdgeCount();
	public abstract int getVertexCount();
	
	
	public abstract void removeVertex(Vertex<V> v);
	public abstract void removeEdge(Edge<V> e);
	
	// For generating unique IDs in this graph
	private volatile int _ids = 0;
	// Tries to return a unique internal ID for vertices.
	protected int makeAnId(){
		return ++ _ids;
	}
	protected void ensureMaxId(int currentID){
		if ( currentID > _ids )
			_ids = currentID;
	}
	
	/** Deep copy of {@code g}. Can be very Slow.*/
	public static <X> Graph<X> copyOf(Graph<X> G){
		Graph<X> Q = new AdjacencyListGraph<X>( G.getVertexCount() / 2 );
		for ( Vertex<X> v : G.getVertices() ){
			Q.addVertex( v );
		}
		for( Edge<X> e : G.getEdges() ){
			//if( ! q.hasEdge( e ))
			Q.addEdge(e);
		}
		return Q;
	}

	/**
	 * Logical representation of a vertex point on a graph. 
	 * </p>
	 * Keeps a list of all edges between it and other vertices. 
	 * </p>
	 * Can be parameterized and act as a container of an element type that can be returned.  
	 * 
	 * @author Moe
	 */
	public static class Vertex<V> implements Comparable<Vertex<V>> {
		/**
		 * For non-parameterized graphs this acts as the sole identifier between
		 * different vertices.  Otherwise acts as an internal identifier which
		 * graph objects will
		 * </p>
		 * TODO_DONE : allow for parameterized IDs and/or contents.
		 * ex: String, Integer, Object etc. This will mean the parameterized
		 * contents will have to be comparable and added to hashCode & equals.*/
		public final int id;

		/**The element which is to be represented by this vertex.
		 * Can be null if this graph isn't parameterized with a type. */
		public final V element;
		
		protected List<Edge<V>> edges;
		protected boolean visited = false;
		protected int order = -1;
		protected boolean directed = false;
		
		/**The leader pointer is used for Union-Find algorithms
		 * By default each vertex is in its own cluster and thus is its
		 * own leader pointer*/
		protected Vertex<V> leaderPointer = this;
		/**How many other vertices point to this vertex.*/
		protected List<Vertex<V>> followers = null;
		/**For Lazy-Union find & Path compression when finding minimum spanning trees*/
		protected int rank = 0;
		

		public Vertex(int id) {
			this( id , null );
		}
		public Vertex(int id, V element) {
			this.id = id;
			this.element = element;
			this.edges = new ArrayList<Edge<V>>();
		}
		/**Given another vertex <code>b</code> returns the edge
		 * incident on both us and <code>b</code>.  Returns null otherwise.*/
		public Edge<V> getEdge(Vertex<V> b){
			return edges.stream()
				.filter( e -> e.otherSide(this).equals( b ) )
				.findFirst()
				.orElse(null);
			//	for( Edge e : edges ){
			//		if( b.equals( e.otherSide(this)) )
			//			return e;
			//	}
			//	return null;
		}
		/**Returns a copy of all edges incident on this vertex */
		public List<Edge<V>> getEdges(){
			return edges.stream().collect( Collectors.toList() );
			// make copy
			//	List<Edge> l = new ArrayList<Edge>();
			//	l.addAll(edges);
			//	return l;
		}
		/**Given an edge <code>e</code> returns the vertex opposite us on the 
		 * other side of <code>e</code>. */
		public Vertex<V> getNeighbor(Edge<V> e){
			return edges.stream()
				.filter( myE -> myE.equals(e) )
				.findFirst()
				.map( myE -> myE.otherSide(this) )
				.orElse(null);
			//	if( hasEdge(e) ){
			//		return e.otherSide(this);
			//	}
			//	return null;
		}
		boolean hasEdge(Edge<V> e){
			return edges.contains(e);
		}
		void removeEdge(Edge<V> e){
			if( edges.contains(e) ){
				edges.remove(e);
			}
		}
		/**Returns true iff there is an edge incident on both this vertex 
		 * and <code>b</code>*/
		boolean hasNeighbor(Vertex<V> b){
			/*
			 * Causes ConcurrentModificationException if we create  a loop of the form :
			 * 
			 * for( Edges e : edges )
			 * 		e.hasNeighbor( b );   // hasNeighbor() loops over edges as a stream.
			 * 
			 */ 
			//	return edges
			//			.stream()
			//			.anyMatch( e -> b.equals(e.otherSide(this)) );
			for(Edge<V> e : edges){
				if( b.equals( e.otherSide(this) ))
					return true;
			}
			return false;
		}
		/**Returns the number of edges incident on both this vertex and on
		 * the given vertex <code>b</cdoe>*/
		int numEdges(Vertex<V> b ){
			return ( b == null ) ?	0 : 
				edges.stream()
				.filter( e -> e.otherSide(b) == this )
				.mapToInt(e->1)
				.sum();
			//	int ii = 0;
			//	for(Edge e : edges){
			//		if( e.otherSide(this).equals(b) ) 
			//			ii ++;
			//	}
			//	return ii;
		}
		/**Returns a list of all edges where this vertex is a source vertex. */
		List<Edge<V>> getOutBound(){
			return edges.stream()
					.filter( e -> e.src == this && e.dst != this )
					.collect(Collectors.toList());
			//	List<Edge> out = new ArrayList<Edge>();
			//	for( Edge e : edges ){
			//		if( e.src.equals(this) && ! e.dst.equals(this) ){
			//			out.add(e);
			//		}
			//	}
			//	return out;
		}
		/**Returns a list of all edges where this vertex is a destination vertex.*/
		List<Edge<V>> getInBound(){
			return edges.stream()
					.filter( e -> (e.dst == this &&  e.src != this) )
					.collect( Collectors.toList() );
			//	List<Edge> in = new ArrayList<Edge>();
			//	for( Edge e : edges ){
			//		if( ! e.src.equals(this) && e.dst.equals(this) ){
			//			in.add(e);
			//		}
			//	}
			//	return in;
		}
		/**Returns true if this vertex has been visited.*/
		boolean isVisited(){ return this.visited; }
		boolean isVisited(Edge<V> e, boolean reverse){
			if( ! e.isIncidentOn(this) ){
				throw new RuntimeException("I couldn't have traveresed an edge not incident on me!");
			}
			if( ( ! e.src.equals(this) && ! reverse) || ( reverse && ! e.dst.equals(this) ) ){
				throw new RuntimeException("Can't check visited if I'm the "+((reverse)?"reverse":"")+" end of the edge");
			}
			return (reverse)? e.src.isVisited() : e.dst.isVisited();
		}
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(id).append(" "+((order==-1)?"":"(#"+order+")")+" -> [");
			List<Edge<V>> es = (this.directed)? getOutBound() : edges;
			for( Edge<V> e : es ){
				Vertex<V> o = e.otherSide(this);
				if( o == null ){
					throw new RuntimeException("@ vert :" + this + " on Edge "+ e);
				}
				sb.append( o.id )
				  .append(',');
			}
			if( es.size()>0 ) sb.deleteCharAt(sb.length()-1);
			sb.append(']');
			return sb.toString();
		}
		@Override
		public int hashCode() {
			return id * 31 * ((element==null)? 1 : element.hashCode());
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			Vertex<V> other = (Vertex<V>) obj;
			if (id != other.id)
				return false;
			// Parameterized vertices now have elements whose equality needs 
			// to be checked as well
			if( element != null )
				return element.equals( other.element );
			return true;
		}
		public int compareTo(Vertex<V> o) {
			return Integer.valueOf(this.id).compareTo(o.id);
		}
	}
	/**Logical representation of an edge between to vertices on a graph.
	 * Can be weighted by given it a cost during construction.  Can be thought
	 * of as being directed since it can remember the vertices as source and 
	 * destination points.  
	 * */
	public static class Edge<V>   {
		final Vertex<V> src, dst;
		private int cost;
		public Edge(Vertex<V> a, Vertex<V> b) {
			if( a == null || b == null )
				throw new RuntimeException("Can't have null vertices in and edge : ("
						+((a==null)?"null":a.id) +','+((b==null)?"null":b.id) );
			this.src = a;
			this.dst = b;
		}
		public Edge(Vertex<V> a, Vertex<V> b, int cost){
			this(a,b);
			this.cost = cost;
		}
		/**Returns the vertex at the source end of this edge */
		public Vertex<V> getSrcVertex(){ return src; }
		/**Returns the vertex at the destination end of this edge */
		public Vertex<V> getDstVertex(){ return dst; }
		
		/**Vertices use this to get what's on the other side of the edge*/
		public Vertex<V> otherSide(Vertex<V> head){
			if( this.src.equals(head) )
				return this.dst;
			if( this.dst.equals(head) )
				return this.src;
			return null;
		}
		/**Returns true iff this edge touches vertex <code>a</code>.*/
		public boolean isIncidentOn( Vertex<V> a ){
			return  src.equals(a) || dst.equals(a);
		}
		/**Returns a string representing the endpoints along this edge in the 
		 * following format : <code>(src,dst)</code>*/
		public String toString(){
			return "("+src.id +","+dst.id+")"+cost;
		}
		/**Returns true iff this edge's end points are one and the same*/
		public boolean isSelfLoop(){
			return src.equals(dst);
		}
		/**Returns an edge (not currently connected to the graph) with its
		 * source and destination reversed.  
		 */
		public Edge<V> reverse(){
			return new Edge<V>(dst, src, cost);
		}
		/**Returns the cost of traversing this edge if this is to be 
		 * used in a weighted graph*/
		public int cost(){
			return cost;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (src.hashCode() + dst.hashCode());
			result = prime * result + cost; //+ ((tail == null) ? 0 : tail.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			Edge<V> other = (Edge<V>) obj;
			if( src.equals(other.src) ){
				return dst.equals(other.dst);
			}
			if( src.equals(other.dst) ){
				return dst.equals(other.src);
			}
			return false;
			/*
			if (head == null) {
				if (other.head != null)
					return false;
			} else if (!head.equals(other.head))
				return false;
			if (tail == null) {
				if (other.tail != null)
					return false;
			} else if (!tail.equals(other.tail))
				return false;
			return true;
			*/
		}
	}

	
	public String toAdjListString() {
		StringBuilder sb = new StringBuilder();
		sb.append(toInfoLine()).append('\n');
		Vertex<V> _v = null;
		try{
			for(Vertex<V> v : getVertices()){
				_v = v;
				sb.append(v.toString()).append('\n');
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(_v);
		}
		return sb.toString();
	}
	
	@Override
	public String toString(){
		return toMatrixString() +  toAdjListString();
		//return toAdjListString();
	}
	protected String toInfoLine(){
		StringBuilder sb = new StringBuilder();
		sb.append("Total Vertices = ").append(getVertices().size())
		  .append(". Total Edges = ").append(getEdges().size()) ;
		  //.append(". Connected = " + hasDisjointNodes())
		return sb.toString();
	}
	public String toMatrixString() {
		StringBuilder sb = new StringBuilder();
		sb.append(toInfoLine()).append('\n').append('\t');
		for( Vertex<V> v : getVertices() ){
			sb.append("  ").append(v.id).append(' ');
		}
		sb.append("\n");
		for( Vertex<V> ii : getVertices() ){
			sb.append(ii.id + " :\t|");
			for( Vertex<V> jj : getVertices() ){
				sb.append(' ');
				//if( hasEdge(ii, jj) ){
				if( ii.hasNeighbor(jj) ){
					Edge<V> e = getEdge(ii, jj);
					if( ii.equals(e.src) || ! isDirected() )
						sb.append('X');
					else sb.append('O');
				}else{
					sb.append('_');
				}
				sb.append(' ');
				sb.append('|');
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	// package private
	static Random RAND = new Random();
	/**
	 * Attempts to return a random number between -1000 & 1000 exclusive. 
	 * @return pseudorandom number between -1,000 and 1,0000.
	 */
	public static int getRandomCost(){
		return RAND.nextInt() % 1000;
		// random number [0-999)  ...  random sign
		//return RAND.nextInt(1000) * (int)(Math.pow( -1.0 , ( RAND.nextInt(2)+1) ));
	}
	

	/**Returns a randomly selected edge from graph <code>G</code>. */
	public static <X> Edge<X> getRandomEdge(Graph<X> G){
		int esize = G.getEdges().size();
		if( esize == 0 ) throw new RuntimeException("Attempt to choose an edge from graph w/ no edges!");//return null;

		//int idx = (int)(Math.random() * esize) % esize;
		return G.getEdges().get( RAND.nextInt(esize) );
	}
	/**If graph <code>G</code> is not empty, returns a randomly selected vertex */
	public static <X> Vertex<X> getRandomVertex(Graph<X> G){
		int esize = G.getVertices().size();
		if( esize == 0 )throw new RuntimeException("Attempt to choose a vertex from empty graph!");
		
		//int idx = (int)(Math.random() * esize) % esize;
		return G.getVertices().get( RAND.nextInt(esize) );
	}
	/**If graph <code>G</code> is not empty, returns a randomly selected vertex that is NOT <code>a</code>*/
	public static <X> Vertex<X> getRandomVertexOther(Graph<X> G, Vertex<X> a){
		int esize = G.getVertices().size();
		if( esize == 0 )throw new RuntimeException("Attempt to choose a vertex from empty graph!");
		if( esize == 1 )throw new RuntimeException("Attempt to choose a different vertex from graph with only 1 entry!");
		Vertex<X> b;
		do {
			//int idx = (int)(Math.random() * esize) % esize;
			b = G.getVertices().get( RAND.nextInt(esize) );
		}while ( a == b );
		return b;
	}

	
	/**
	 * Attempts to construct a graph with at least {@code vertexSize} 
	 * vertices. 
	 * </p>
	 * The graph is guaranteed to have at least one edge for every vertex.
	 * (But there is no guarantee the graph is wholly connected). 
	 * @param vertexSize
	 * @return A graph of size {@code vertexSize} with a random number of edges.
	 */
	public static <X> Graph<X> makeRandomGraph(int vertexSize){
		long start = System.nanoTime();
		AdjacencyListGraph<X> G = new AdjacencyListGraph<X>(vertexSize);
		
		Stream<Vertex<X>> vertGen = Stream.generate( () -> new Vertex<X>(G.makeAnId()) );
		vertGen = vertGen
			.limit(vertexSize);
			//.parallel()         // parallel() slows it down since they are threads just hammering G all at once
			//.skip( 10_000 )	// slows a bit down .. w/ parallel() slows down considerably
			//.unordered()		// unnecessary and slows a bit down
			//if( vertexSize > 10_000 ) 
			//	vertGen = vertGen.parallel(); // parallel() also gives you =< than the limit() size. 
		
		vertGen
			.forEach( v -> G.addVertex( v ) );
		
		long nodes = System.nanoTime();
		
		// Make connections to every node
		G.getVertices().stream()
			.forEach( a -> {
				Vertex<X> b;
				do {
					b = //G.getVertex( RAND.nextInt(vertexSize) + 1  );
						Graph.getRandomVertexOther( G , a );
				}while( G.hasEdge( a, b )  ); 
			
				G.addEdge(  a, b, Graph.getRandomCost()  );
				
			});
		long edges = System.nanoTime();
		

		// Make a bunch of random edges
		int rand_edges = vertexSize / 2;
		for( int ii = 0 ; ii < rand_edges ; ii++ ) {
		//while( G.hasDisjointNodes() ) {
			Vertex<X> a = //G.getVertex( RAND.nextInt(vertexSize) + 1 );
					Graph.getRandomVertex( G );
			Vertex<X> b;
			do {
				b = //G.getVertex( RAND.nextInt(vertexSize) + 1);
					Graph.getRandomVertexOther( G , a );
			}while( G.hasEdge( a, b )  ); 
			
			G.addEdge(  new Edge<X>( a, b, Graph.getRandomCost() )  );
		}
		long more_rand_edges = System.nanoTime();

		
		// Find any lonely singleton nodes... 
		List<Edge<X>> moreEdges = G.getVertices().stream()
			.filter( a -> a.edges.isEmpty() )
			.map( a -> new Edge<X>( a , Graph.getRandomVertexOther(G , a) , Graph.getRandomCost()) )
			//.forEach(  e -> G.addEdge( e ) );   // ConcurrentModificationException
			.collect( Collectors.toList());
		
		for( Edge<X> me : moreEdges )
			G.addEdge( me );
		
		long singles = System.nanoTime();
		
		System.out.println();
		System.out.printf("Time to fill nodes        : %sms%n", (nodes - start)/1_000_000.0);
		System.out.printf("Time random edges         : %sms%n", (edges - nodes)/1_000_000.0);
		System.out.printf("Time more random edges    : %sms%n", (more_rand_edges - edges)/1_000_000.0);
		System.out.printf("Time connect single nodes : %sms%n", (singles - more_rand_edges)/1_000_000.0);
		System.out.println( G.toInfoLine() );
		
		return G;
	}

	
}
