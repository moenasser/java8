package com.mnasser.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mnasser.graph.Graph.Edge;
import com.mnasser.graph.Graph.Vertex;

/**
 * We'll run the Kruskal MST algorithm on a simple graph and assert that
 * it spits out the correct minimum spanning tree.
 * </p>
 * 
 * @author Moe
 */
public class KruskalMSTTest {

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void simpleTest(){
		Graph G = Graph.getInstance();
		
		G.addEdge( new Edge( new Vertex(1), new Vertex(2) , 1) );
		G.addEdge( new Edge( new Vertex(1), new Vertex(3) , 5) );
		G.addEdge( new Edge( new Vertex(1), new Vertex(4) , 3) );
		G.addEdge( new Edge( new Vertex(1), new Vertex(5) , 3) );
		
		G.addEdge( new Edge( new Vertex(2), new Vertex(3) , 7) );
		G.addEdge( new Edge( new Vertex(3), new Vertex(4) , 6) );
		G.addEdge( new Edge( new Vertex(4), new Vertex(5) , 2) );

		System.out.println(G);
		
		Graph T = KruskalMST.findMSTNaive(G);
		
		System.out.println(T);
		
		Assert.assertTrue( T.getEdgeCount() == 4);
		Assert.assertTrue( T.getVertexCount() == 5 );
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void largeMSTtest() throws IOException, URISyntaxException{
		long start = System.currentTimeMillis();
		Graph G = loadTestGraph("edges_graph.txt");
		
		long fileLoad = System.currentTimeMillis();
		Graph T = KruskalMST.findMSTNaive( G );
		
		long mstTime = System.currentTimeMillis();
		
		System.out.printf("MST edges : %s , nodes : %s%n", T.getEdgeCount(), T.getVertexCount());
		
		System.out.println("Time to load graph : " + (fileLoad-start));
		System.out.println("Time to find MST   : " + (mstTime-fileLoad));
	
		int total_cost = 0;
		for( Edge e : (List<Edge>) T.getEdges() )
			total_cost += e.cost();
		
		System.out.println("Total cost of MST  : " + total_cost);
	}
	
	/*Looks in resources/ dir for MST test graph edge/vertices info*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static Graph loadTestGraph(String file) throws IOException, URISyntaxException{
		File f = new File( KruskalMSTTest.class.getResource(file).toURI() );
		System.out.print("Loading file " + file + "...");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = null;
		
		line = br.readLine(); // first line is edge/vert counts.
		String[] counts = line.trim().split("\\s+");
		int total_nodes = Integer.parseInt(counts[0]);
		int total_edges = Integer.parseInt(counts[1]);
		System.out.printf("Graph should have %s vertices & %s edges.%n", total_nodes, total_edges);
		
		int cnt = 0;
		Graph G = new AdjacencyListGraph();
		while( (line=br.readLine())!=null){
			cnt++;
			String[] edge = line.trim().split("\\s+");
			Vertex a = new Vertex(Integer.parseInt(edge[0]));
			Vertex b = new Vertex(Integer.parseInt(edge[1]));
			int cost = Integer.parseInt(edge[2]);
			Edge e = new Edge( a, b, cost );
			if( ! G.hasEdge(e) )
				G.addEdge( e );
		}
		System.out.println(cnt + " total lines read.");
		System.out.println(G.toInfoLine());
		br.close();
		return G;
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void largeLazyUnionByRankMSTtest() throws IOException, URISyntaxException {
		long start = System.currentTimeMillis();
		Graph G = loadTestGraph("edges_graph.txt");
		
		long fileLoad = System.currentTimeMillis();
		
		Graph T = KruskalMST.findMSTLazyUnionByRank( G );
		
		long mstTime = System.currentTimeMillis();
		
		System.out.printf("MST edges : %s , nodes : %s%n", T.getEdgeCount(), T.getVertexCount());
		
		System.out.println("Time to load file  : " + (fileLoad-start));
		System.out.println("Time to call MST() : " + (mstTime-fileLoad));
	
		int total_cost = 0;
		for( Edge e : (List<Edge>) T.getEdges() )
			total_cost += e.cost();
		
		System.out.println("Total cost of MST  : " + total_cost);
		
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void randomTest(){
		int N = 100_000; //3_000_000; // 1_000_000;
		Graph G = Graph.makeRandomGraph( N  );
		System.out.println( G.toInfoLine() );
		
		Graph T = KruskalMST.findMSTNaive( G );
		Graph T2  = KruskalMST.findMSTLazyUnionByRank( G );
		
		System.out.println( T.toInfoLine() );
		System.out.println( T2.toInfoLine() );
		
		long total_cost = 0 ;
		long timeOld = 0, timeStream = 0, timeParallelStream = 0;
		
		// take timings. reapeat upto 10 times. use last values 
		for ( int cycles = 0 ; cycles < 10 ; cycles ++ ){
			total_cost = 0;
			
			long start = System.nanoTime();
			for( Edge e : (List<Edge>) T.getEdges() )   // as N gets higher ... the old way wins out
				total_cost += e.cost();
			timeOld += System.nanoTime() - start;
			
			start = System.nanoTime();     // faster for N between [10K-100K]. Break even ~75k. Slower after 
			total_cost = T.getEdges().stream().mapToInt( e -> ((Edge) e).cost() ).sum();
			timeStream += System.nanoTime() - start;
			
			start = System.nanoTime();     // Always slower than old way. Faster than stream() when N > ~1 million
			total_cost = T.getEdges().parallelStream().mapToInt( e -> ((Edge) e).cost() ).sum();
			timeParallelStream += System.nanoTime() - start;
		}
		System.out.println("Time to sum() all costs old way             : " + (timeOld/10.0)/1_000_000 + "ms");
		System.out.println("Time to sum() all costs w/ Streams          : " + (timeStream/10.0)/1_000_000 + "ms");
		System.out.println("Time to sum() all costs w/ Parallel Streams : " + (timeParallelStream/10.0)/1_000_000 + "ms");
		System.out.println("Total cost of MST  : " + NumberFormat.getInstance().format(total_cost) );
	}

	/*  Sample results for above : 
	 * 
	 *  N = 3,000,000 nodes
	 *  
	Time to fill nodes        : 5528ms
	Time random edges         : 10688ms
	Time more random edges    : 2324ms
	Time connect single nodes : 63ms
	
	Total Vertices = 3000000. Total Edges = 4500000
	Time to prep G : 1031
	Time to find MST : 19569
	Total Vertices = 3000000. Total Edges = 2999999
	Time to sum() all costs old way : 363.50519799999995ms
	Time to sum() all costs w/ Streams : 1148.59149ms
	Time to sum() all costs w/ Parallel Streams : 522.390404ms
	Total cost of MST  : -846,383,096
	
	 *
	 * N = 3,000  nodes
	 * 	
	Time to fill nodes        : 469.4468ms
	Time random edges         : 101.19013ms
	Time more random edges    : 25.08681ms
	Time connect single nodes : 90.73857ms
	
	Total Vertices = 3000. Total Edges = 4500
	Time to prep G   : 6.582416ms
	Time to find MST : 11.536563ms
	Total Vertices = 3000. Total Edges = 2999
	Time to sum() all costs old way             : 0.37408020000000003ms
	Time to sum() all costs w/ Streams          : 0.5013531ms
	Time to sum() all costs w/ Parallel Streams : 16.153909300000002ms
	Total cost of MST  : -822,480

	 */
}
