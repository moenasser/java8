package com.mnasser.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Test;

import com.mnasser.util.LeftRight;

public class KClusteringTest {

	@Test
	public void testSimpleCluster(){
		Graph<String> g = Graph.getInstance();
		
		g.addEdge( "john", "sally", 1 );
		
		g.addEdge( "moe", "shmoe", 1 );
		
		g.addEdge( "john", "moe", 10 );
		
		Assert.assertEquals( 4, g.getVertexCount() );
		
		LeftRight<Graph<String>,Long> res = KClusterMaxSpace.KCluster(g, 2);
		Graph<String> T = res.left;
		
		System.out.println(g);
		System.out.println(T);
		
		Assert.assertTrue( T.hasEdge("john", "sally") );
		Assert.assertTrue( T.hasEdge("moe", "shmoe") );
		
		Assert.assertFalse( T.hasEdge("john", "moe") );
		
		Assert.assertEquals( 4, g.getVertexCount() );
		
		Assert.assertEquals( 10L, res.right.longValue() ); // the max space between the 2 clusts is the edge cost '10'
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void test4Cluster() throws IOException{
		Graph g = loadClusterFile();
		System.out.println("G : "  + g.toInfoLine());
		
		LeftRight<Graph, Long> res = (LeftRight<Graph, Long>) KClusterMaxSpace.KCluster(g, 4);
		
		System.out.println("T       : "  + res.left.toInfoLine());
		System.out.println("Spacing : "  + res.right);
		
	}
	
	@SuppressWarnings("rawtypes")
	static Graph loadClusterFile() throws IOException{
		
		long start = System.nanoTime();
		
		try (
			InputStream is = KClusteringTest.class.getResourceAsStream("clustering.txt");
			BufferedReader br = new BufferedReader( new InputStreamReader(is) );
			//Scanner s = new Scanner( br ); // slower
		){
			Graph G = Graph.getInstance();
			
			String line = br.readLine();
			int total_nodes =  //s.nextInt(); 
					Integer.parseInt(line);
			int node1 , node2, cost;
			//while( s.hasNext() ) {
			while( (line=br.readLine()) != null ){
				String[] edge_info = line.split("\\s+");
				node1 = /*s.nextInt();*/  Integer.parseInt( edge_info[0] );
				node2 = /*s.nextInt();*/  Integer.parseInt( edge_info[1] );
				cost  = /*s.nextInt();*/  Integer.parseInt( edge_info[2] );
				G.addEdge( node1, node2, cost );
			}
			
			System.out.printf("Loaded %s lines. Graph G has %s nodes. Done in %sms\n", total_nodes, G.getVertexCount(),
					((System.nanoTime()-start) * 1_000_000.0));
			
			return G;
		}
	}
}
