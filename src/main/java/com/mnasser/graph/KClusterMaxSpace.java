package com.mnasser.graph;

import java.util.Scanner;

import com.mnasser.graph.Graph.Edge;
import com.mnasser.graph.Graph.Vertex;
import com.mnasser.graph.KruskalMST.UnionDecision;
import com.mnasser.util.LeftRight;


/**
 * A variant of Kruskal's minimum spanning tree algo
 * that stops before it has 1 all-encompassing tree that spans the entire graph. 
 * </p>
 * Specifically it will stop when there are {@code k} connected sub-trees;
 * (where as Kruskal would have kept going until {@code k}=1).
 *
 * @author Moe
 */
public class KClusterMaxSpace implements UnionDecision {

	private final int k; // number of clusters to stop at 
	private final int initialNodeCount;  // initial number of nodes in the graph (each is basically it's own cluster)
	private int clusters;
	
	private long maxSpacing = Long.MAX_VALUE; // sum of edges costs after we arrive at k-clusters 
	
	/**Private constructor*/
	private KClusterMaxSpace(int k, int nodes){
		if ( k > nodes ) throw new RuntimeException("K greater than total vertices in a graph");
		this.k = k;
		this.initialNodeCount = nodes;
		this.clusters = initialNodeCount;
	}
	
	/**
	 * The smallest distance between two points that are supposed to be in separate clusters.
	 * (We desire to maximize this value).
	 * */
	public long getMaxSpacing(){
		return maxSpacing;
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	/***
	 * For K-Clustering we want to keep merging into ever larger connected component groups until 
	 * we have exactly K groups. For that we simply do the same as in finding a minimum spanning tree 
	 * but keep track of how many merges we have done (ie, how many clusters are left) and stop when we reach k.
	 */
	public UnionResult union(final Edge origEdge, Vertex cluster1, Vertex cluster2, boolean useUnionByRank, Graph g) {
		if( clusters > k ){
			
			//if( clusters < 10 ) { //> (g.getVertexCount()-10) ){
			System.out.printf("Merging %s(%s members) + %s(%s members) ",
					cluster1.id, KruskalMST.getFollowerSize(cluster1) + 1 , 
					cluster2.id, KruskalMST.getFollowerSize(cluster2) + 1 );
			
			UnionDecision.DEFAULT_DECISION.union(origEdge, cluster1, cluster2, useUnionByRank, g); // merge 2 clusterss
			clusters--;		// number of clusters goes down by 1 when union merges two clusters
			
				
				System.out.printf("= %s (%s total members). (%s clusters total)\n",
						cluster1.leaderPointer.id, KruskalMST.getFollowerSize(cluster1.leaderPointer) + 1 ,
						clusters);
			//}
		}
		else {
			
			maxSpacing = ( origEdge.cost() < maxSpacing ) ?   origEdge.cost() : maxSpacing; 
			
			System.out.printf("Will NOT merge %s(%s members) & %s(%s members)" , 
					cluster1.id, KruskalMST.getFollowerSize(cluster1) + 1, 
					cluster2.id, KruskalMST.getFollowerSize(cluster2) + 1);
			System.out.println(" MAX Spacing so far : " + maxSpacing);
		}
		return ( clusters == k ) ?  UnionResult.STOP  : UnionResult.CONTINUE;   // have we reached our goal?
	}


	/**
	 * Given a graph {@code G}, clusters closely related nodes into ever larger clusters, stopping when
	 * there are only {@code k} clusters left.
	 * 
	 * @param G Graph to cluster into k groups 
	 * @param k number of groups to cluster nodes into 
	 * 
	 * @return A tuple containing the graph {@code G'} containing k clusters on the left; and the 
	 * smallest distance between any points crossing amongst these k clusters.  
	 */
	public static <V> LeftRight< Graph<V>, Long > KCluster(Graph<V> G, int k){
		
		KClusterMaxSpace kc = new KClusterMaxSpace(k, G.getVertexCount() );
		
		// should stop when G has k groups
		return LeftRight.lr( 
				KruskalMST.findMST(G, false, kc) , 
				kc.getMaxSpacing() ); 
	}
	
	
	
	//private static Log
}

