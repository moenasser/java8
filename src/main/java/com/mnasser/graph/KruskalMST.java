package com.mnasser.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mnasser.graph.Graph.Edge;
import com.mnasser.graph.Graph.Vertex;
import com.mnasser.graph.KruskalMST.UnionDecision.UnionResult;

/**
 * Kruskal's Minimum Spanning Tree algorithm.
 * 
 * Using the Union-Find technique we can quickly (in {@code O( m logn )} time)
 * procure a leader pointer 
 * 
 * @author Moe
 * @param <X>
 */
public class KruskalMST<X> {


	
	/**
	 * Find the MST of graph G using Kruskal's MST algorithm.
	 * This makes use of the Union-Find technique of keeping track of every
	 * connected component group and merging them as we keep spanning the tree.
	 * 
	 * @param G the graph whose minimum spanning tree is desired
	 * @return a minimum spanning tree of G
	 * @see KruskalMST.findMST
	 * */
	public static <X> Graph<X> findMSTNaive(Graph<X> G){
		return findMST( G , false );
	}
	/**
	 * Find the MST of graph G using Kruskal's MST algorithm.
	 * This makes use of the Lazy-Union-Find technique of keeping track of every
	 * connected component group and merging them as we keep spanning the tree. The 
	 * merge in this case is a lazy update of one group leader to point to the other
	 * group leader.
	 * </p>
	 * Ranks of each group leader are kept up in order to quickly determine which
	 * group already has a deep tree.
	 * </p>
	 * Since trees are being formed beneath leaders (and leaders are the roots of these trees)
	 * it would take O(log n) time for each find. We counter this with Path Compression.
	 * This is implemented by updating child nodes' parents to point directly to group leaders
	 * (roots) as soon as we encounter them in a find.  
	 * 
	 * @param G the graph whose minimum spanning tree is desired
	 * @return a minimum spanning tree of G
	 * @see Kruskal.findMST
	 */
	public static <X> Graph<X> findMSTLazyUnionByRank(Graph<X> G){
		return findMST( G , true );
	}
	/**
	 * Given a graph <code>G</code>, attempts to procure a second graph 
	 * <code>T</code> that contains only the least amount of edges at the 
	 * smallest cost that will span all vertices in <code>G</code>.  
	 * This spanning tree is called the minimum cost spanning tree. 
	 * </p>
	 * The algo works by first sorting every edge <code>e</code> in <code>G</code>
	 * by edge cost; adding progressively higher cost edges one by one if and 
	 * only if they meet the following requirements :
	 * <ul>
	 * <li>edge does not form a closed circle</li>
	 * <li>vertices aren't already in our growing spanning tree</li>
	 * <li>edge is the smallest cost to vertex</li>
	 * </ul>
	 * We determine if a closed circle is to be formed by keeping track of the
	 * cluster group each vertex is being added to.  This is what the <code>leader 
	 * pointer</code> on each vertex is for. 
	 * 
	 * </p>
	 * NOTE : this algorithm can be used in two ways. The first keeps track of connected
	 * component group leaders and the children of those leaders (essentially a graph of 
	 * depth 1).  See {@code findMSTNaive()} or this.  Set {@code useUnionByRank} to 
	 * false.  
	 * </p>
	 * The second way this algo works is w UnionByRank & Path Compression.  
	 * This can speed up the initial union-find technique by employing union-by-rank,  
	 * which will keep a rank of each group leader but instead of forming a flat depth-of-1 
	 * graph beneath each leader, we simply update each group leader's leader during the 
	 * {@code union()} operations.  In this way, small bushy trees are formed beneath the
	 * growing connected components.  Path compression is then used to speed up calls to 
	 * the {@code find()} operations by immediately updating a child vertex's parent pointer 
	 * if it is not already the leader of the entire group.    
	 * 
	 * @param G The graph whose minimum spanning tree is desired. 
	 * @param useUnionByRank True to use unionByRank & pathCompression. False for not.
	 * @param uo The implementation of the union operation. This was factored out to allow variations of the 
	 * 
	 * @return T A tree spanning all nodes in G using the set of edges 
	 * 
	 * @see KruskalMST.findMST
	 * @see KruskalMST.findMSTNaive
	 * @see KruskalMST.findMSTLazyUnionByRank
	 */
	public static <X> Graph<X> findMST(Graph<X> G, boolean useUnionByRank, UnionDecision uo ){
		// Our MST
		Graph<X> T = new AdjacencyListGraph<X>( G.getVertexCount() );
		
		//Ranked edges by their costs
		//Heap<Edge<X>> edgeHeap = new Heap<Edge<X>>( Comparator.comparingInt( e -> e.cost() ));
		// heap will add O(logn) time to our main for-loop below. 
		// Let's just sort once and then just iterate
		List<Edge<X>> sortedEdges = 
				G.getEdges().stream().sorted( Graph.getEdgeComparator() ).collect( Collectors.toList() );
		
		long start = System.nanoTime();
		
		//Initialization ...
		//Add edges to heap. (TODO : create "heapify" batch loading method)
		//for( Edge<X> e : G.getEdges()){
		//	edgeHeap.insert(e);
		
		for( Edge<X> e : sortedEdges ){ // 
			e.src.visited = false;
			e.dst.visited = false;
			e.src.leaderPointer = e.src; // each has itself as leader pointer
			e.dst.leaderPointer = e.dst; // ie, its own cluster of 1
			e.src.followers = null;
			e.dst.followers = null;
			//if (e.src.followers==null) e.src.followers = new ArrayList<Vertex>();
			//if (e.dst.followers==null) e.dst.followers = new ArrayList<Vertex>();
			if( useUnionByRank ){
				e.src.rank = 0 ;
				e.dst.rank = 0;
			}
		}
		
		long lap = System.nanoTime();
		String _time = String.format("Time to prep G   : " + (lap - start)/1_000_000.0  + "ms  " );
		
		start = System.nanoTime();
		
		//begin our loop by adding in edges and adjusting leader pointers
		//while ( edgeHeap.size() > 0 ) {
		//	Edge<X> e = edgeHeap.removeRoot(); // this is O(nlogn) 
		for ( Edge<X> e : sortedEdges ) {      // this is O(n)
			
			// since both of these are in connected component groups,
			// we need to find the leaders of each group.  If both Nodes are 
			// already in the same group then adding 
			// this edge will cause a CYCLE so we skill it.
			Vertex<X> cluster1 = find( e.src );
			Vertex<X> cluster2 = find( e.dst );
			
			if ( useUnionByRank ) {
				// since the recursive calls above would normally take O(log n) time, 
				// let's go ahead and save these values so we avoid the 
				if ( e.src.leaderPointer != cluster1 ){
					e.src.leaderPointer = cluster1; 
				}
				if( e.dst.leaderPointer != cluster2 ){
					e.dst.leaderPointer = cluster2;
				}
			}

			
			if(  cluster1  !=  cluster2  ) 
			{
				System.out.println("Consuming edge " + e);
				// add the edge + vertices to T
				T.addEdge( e );
				
				// make sure they are in the same group
				UnionResult ur = uo.union(e , cluster1, cluster2, useUnionByRank, G);
				
				if ( ur == UnionResult.STOP ){   // terminate early ?
					if ( T.getVertexCount() == G.getVertexCount() ) // well make sure we have the rest
						break;
				}
			}	
			
			if ( T.getVertexCount() == G.getVertexCount() ) // we're done. 
				break; // All vertices have been added to the MST. So stop early
		}
		
		lap  = System.nanoTime();
		System.out.println(_time + "Time to find MST : " + (lap - start)/1_000_000.0  + "ms");
		
		return T;
	}
	
	/**Returns a minimum spanning tree of {@code G}. */
	public static <X> Graph<X> findMST(Graph<X> G, boolean useUnionByRank){
		return findMST( G, useUnionByRank, UnionDecision.DEFAULT_DECISION);
	}
	
	
	
	
	
	
	/**The default */
	//final static UnionDecision<> ALWAYS_CONTINUE = new DefaultUnionDecision<>(); 

	/** 
	 * Package private implementation of how and when Kruskal's algorithm should continue
	 * during calls to {@code union}.
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	interface UnionDecision {
		
		public UnionResult union(Edge e, Vertex cluster1, Vertex cluster2, boolean useUnionByRank, Graph g);
		
		/**Defines a type of result each call to union is allowed to return. */
		static enum UnionResult {
			CONTINUE , // by default this MST algo would keep going after every union; 
			STOP;  // however for K-clustering we want to stop early
		}
		
		// Default implementation of our 1-method interface
		/**The legacy implementation before factoring out this step of the algorithm.
		 * This does the expected union or lazy union operation and returns an instruction
		 * to continue. */
		public static UnionDecision DEFAULT_DECISION = (e, c1 , c2, byRank, g) -> { // mmm ... lambdas
			if( byRank )
				KruskalMST._lazyUnionByRank( c1, c2 );
			else
				KruskalMST._union( c1 ,  c2 );
			
			return UnionResult.CONTINUE;
		}; 
	}
	
	//	/**The legacy implementation before factoring out this step of the algorithm.
	//	 * This does the expected union or lazy union operation and returns an instruction
	//	 * to continue. */
	//	protected static class DefaultUnionDecision<X> implements UnionDecision<X> {
	//		@Override
	//		public UnionResult union(Vertex<X> cluster1, Vertex<X> cluster2, boolean useUnionByRank) {
	//			// make sure they are in the same group
	//			if( useUnionByRank )
	//				KruskalMST._lazyUnionByRank( cluster1, cluster2 );
	//			else
	//				KruskalMST._union( cluster1 ,  cluster2 );
	//			
	//			return UnionResult.CONTINUE;
	//		}
	//	}
	
	//	protected static <X> void union(Vertex<X> cluster1, Vertex<X> cluster2, boolean useUnionByRank){
	//		// make sure they are in the same group
	//		if( useUnionByRank )
	//			_lazyUnionByRank( cluster1, cluster2 );
	//		else
	//			_union( cluster1 ,  cluster2 );
	//	}
	
	/** Finds the cluster group (connected components group) that <code>v</code>
	 * is a part of.*/
	protected static <X> Vertex<X> find(Vertex<X> v){
		Vertex<X> leader = v.leaderPointer;
		// in Lazy-Union-Find we need multiple recursive calls to find()
		return  ( leader.leaderPointer != leader )? find(leader) : leader ;
	}
	
	
	/** Given the leaders of 2 connected component groups, will merge them into 
	 * 1 by updating all leader pointers of each group.*/
	protected static <X> void _union(Vertex<X> v, Vertex<X> u){
		Vertex<X> leader = null , follower = null;
		if( getFollowerSize(v) >= getFollowerSize(u) ){
			leader = v;  follower = u;
		}else{
			leader = u;  follower = v;
		}
		
		if ( leader.followers == null )
			leader.followers = new ArrayList<Vertex<X>>();
		
		//Vertex leader = (v.followers.size() >= u.followers.size())? v : u;
		//Vertex follower = (u.followers.size() < v.followers.size() )? u : v;
		
		// NOTE: This will create a "flat" tree of followers 1-level below the leader
		// For Lazy-Union-Find you would allow multiple levels.
		if( follower.followers != null ){
			
			for( Vertex<X> f : follower.followers ){
				f.leaderPointer = leader;
				if( f.followers != null ) { 
					f.followers.clear(); // for Lazy-Union-Find, allow follower to retain followers
					f.followers = null; // clear memory
				}
			}
			
			leader.followers.addAll(follower.followers);
			follower.followers.clear();
			follower.followers = null;
		}
		
		leader.followers.add( follower );
		follower.leaderPointer = leader;
		// TODO : how do we find all followers beneath a leader vertex?
		// TODO : must add follower array to each vertex
	}
	
	protected static <X> int getFollowerSize(Vertex<X> v){
		return ( v.followers == null )? 0 : v.followers.size(); 
	}
	

	
	
	/** Instead of keeping track of every single follower beneath a leader vertex, (and thus
	 * creating very flat 1-level trees beneath leaders), we will simply update the pointer of
	 * one leader to point to the next.  In this method, since these aren't leader pointer but just 
	 * parent pointers really, we are making bushy trees. 
	 * </p>
	 * This will increase the time it takes to do a find() call from {@code O(1)} to {@code O(log n)}.  However
	 * over a sequence of calls we will not need to be doing this that much and, very importantly,
	 * we will introduce <strong>Path Compression</code> to help speed up our lazy union approach. 
	 * </p>
	 * */
	protected static <X> void _lazyUnionByRank(Vertex<X> v, Vertex<X> u){
		Vertex<X> parent = null, child = null; 
		if ( v.rank  > u.rank ){
			parent = v;  child = u;
		}else{
			parent = u;  child = v;
		}
		
		child.leaderPointer = parent;
		
		if ( parent.rank == child.rank )  // if nodes were same rank, promote one
			parent.rank ++ ;
	}
}
