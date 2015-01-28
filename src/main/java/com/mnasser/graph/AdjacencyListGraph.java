package com.mnasser.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple implementation of a Graph as listing of all edges <code>E</code>
 * between vertices <code>V</code>.
 * 
 * </p>
 * TODO : replace array of vertices/edges with a Heap or Binary tree of sorts
 * @author Moe
 */
public class AdjacencyListGraph<E> extends Graph<E>{
	
	public AdjacencyListGraph(int initialSize) {
		vertices    = new ArrayList<  Vertex<E>  >(initialSize);
		edges       = new ArrayList<   Edge<E>   >(initialSize);
		_vertMap    = new HashMap<Integer,Vertex<E>>(initialSize);//new IntMap< Vertex<E> >(  );  
		_elementMap = new HashMap<    E , Vertex<E>    >(initialSize);
	}
	public AdjacencyListGraph() {
		this(100);
	}
	
	private List<Vertex<E>> vertices = null; 
	private List<Edge<E>>   edges    = null; 
	
	private HashMap<Integer, Vertex<E>> _vertMap    = null;      // map of primitive int -> Vertex
	//private IntMap<Vertex<E>>     _vertMap    = null;
	private Map<E , Vertex<E>>    _elementMap = null; // so we can look up vertices by their elements quickly
	
	private int connected_vertices = 0; // so we know if there are any nodes with zero edges in O(1) time

	@Override public List<Edge<E>>   getEdges()    { return edges;    }
	@Override public List<Vertex<E>> getVertices() { return vertices; }//Collections.sort(vertices); return vertices; }
	
	public int   getEdgeCount(){	return edges.size();	}
	public int getVertexCount(){	return vertices.size(); }
	
	/** Creates a new empty vertex with a generated unique id, no edges and adds it to the graph.*/
	public Vertex<E> addVertex(){
		int id = makeAnId();
		return addVertex(id);
	}
	/**Creates a new empty vertex with the given ID and no edges and adds it to the graph.*/
	@Override
	public Vertex<E> addVertex(int id){
		return addVertex( id , null );
	}
	/** Adds a vertex w/ given id and given element.
	 * </p> 
	 * TODO : As it stands, this will NOT allow multiple vertices w/ different IDs 
	 * to have the same element. Do we want to maintain 1:1 IDs to Elements?  Or can we have 
	 * multiple vertices w/ the same (.equals()) element but different IDs?
	 * 
	 * @throws BadVertexAddition if this {@code id} & {@code element} combo conflicts with and id 
	 * and or element already in the graph 
	 */
	@Override
	public synchronized Vertex<E> addVertex(int id, E element) throws BadVertexAddition {
		Vertex<E> test =  __testForAddition( id , element ); 
		return (test==null) ?  __addVertex( id , element )    :  test;
		// legacy implementation : 
		// --------------------------------------------
		//	if( ! _vertMap.containsKey(id) ){
		//
		//		Vertex<E> copy = new Vertex<E>(id, element);
		//		copy.directed = isDirected();
		//		vertices.add(copy);
		//		_vertMap.put(copy.id, copy);
		//		if( element != null ) {
		//			_elementMap.put( element, copy );
		//		}
		//		ensureMaxId( id );
		//	}
		//	return _vertMap.get(id);
	}
	
	/**Adds this vertex to the graph if it is not already present*/
	public Vertex<E> addVertex(Vertex<E> v){
		return addVertex( v.id , v.element );
		// legacy implementation : 
		// -----------------------------------------------
		//	if( ! _vertMap.containsKey(v.id) ){
		//		// clone it
		//		Vertex<E> copy = new Vertex<E>(v.id , v.element); 
		//		copy.directed = isDirected();
		//		vertices.add(copy);
		//		_vertMap.put(copy.id, copy);
		//		ensureMaxId( copy.id );
		//	}
		//	return _vertMap.get(v.id);
	}
	/**
	 * Adds a vertex based on the containing element. 
	 * @return Vertex<E> New vertex continaing the given element. 
	 * 		Or an existing element if  
	 * @throws NullPointerException if the given element is null.
	 */
	@Override
	public synchronized Vertex<E> addVertex(E element) {
		if ( element == null ) throw new NullPointerException("Cannot accept a vertex with a null element");
		Vertex<E> v = _elementMap.get( element );
		return (v==null)  ?   addVertex(makeAnId(), element)   :  v;
	}
	
	/**Internal addVertex() which doesn't create new objects nor checks vert/element Maps*/
	private Vertex<E> __addVertex(int id, E element){
		Vertex<E> v = new Vertex<E>(id , element);
		v.directed = isDirected();
		vertices.add( v );
		_vertMap.put( v.id , v );
		if( element != null )
			_elementMap.put( v.element, v );
		ensureMaxId( v.id );
		return v;
	}
	
	/**Checks for BadVertexAddition exception and if this vertex already exits.
	 * @return Returns the given vertex if it already exists in this graph. Null otherwise.
	 * @throws BadVertexAddition if this vertex would cause a conflict with an exiting vertex.*/
	private Vertex<E> __testForAddition(int id, E element) throws BadVertexAddition {
		Vertex<E> idtest = _vertMap.get( id );
		Vertex<E> eltest = (element==null)? null : _elementMap.get( element );
		if( idtest == null ){
			if ( eltest == null ) // if idtest==null, eltest should ==null as well.
				return null;      // this is the default case for new entries
			
			// hmmm ... we found it by element but not by id? Probably a BVA
			//if ( ! v.equals( eltest ) )
			if ( eltest.id != id )
				throw BVA( id, element );
			
			// ... this is weird! this vertex w/ same ID and same element shows up in the _elementMap
			// but not in _vertMap?!  should be there as well! This means we *already* have a problem
			// with consistency between the maps.
			//TODO_done : throw ex anyway? or fix our _vertMap?
			// We should fix _vertMap now
			_vertMap.put( eltest.id, eltest );
			integrityProblemsFixed++;
			
			return eltest; 
		}
		
		// idtest not null 
		if( eltest == null ){
			// we matched vertex by id but not by element? Only non-parameterized graphs have null elements.
			// again like above this is an integrity problem that *should not* happen. Let's see if we can fix

			// if our vertices have no elements anyway, then idtest & the input should match
			if ( idtest.element == null ) {
				if ( element == null  )
					return idtest;
				else 
					throw BVA( id, element ); // a mismatch
			}
			
			// we have elements? And they don't match? not good
			if ( ! idtest.element.equals(element)  )  
				throw BVA( id, element );
			
			// we have a match. And there are elements, fix our _elementMap 
			_elementMap.put( idtest.element, idtest ); // ... Fix it
			integrityProblemsFixed++;
			
			return idtest;
		}
		
		// both found? they had better be the same element (or else integrity constraint in our maps)
		if (  ! idtest.equals( eltest ) ) // ||  ! idtest.equals( v ) )
			throw BVA( id, element );
		
		return idtest;
	}

	
	/** indicates we did something wrong when keeping _vert/_element Maps in sync. Perhaps more synchronization?*/
	private volatile int integrityProblemsFixed = 0;
	
	/** This exception indicates that the rule that the parameterized elements must be unique just like 
	 * IDs are has been violated. Meaning this graph's {@code addVertex(int id, E element)} method was used
	 * to add a vertex parameterized on {@code element} but the given {@code element} already exists 
	 * in this graph or this vertex's {@code id} is already in use with another {@code element}.
	 * </p>
	 * Example : 
	 * <pre>
	 * graph.addVertex( 5 , "apple");
	 * graph.addVertex( 6,  "apple"); // throws exception
	 * </pre>
	 * */
	@SuppressWarnings("serial")
	public static class BadVertexAddition extends RuntimeException {
		public     BadVertexAddition( String msg ) { super(msg) ; }
		public <E> BadVertexAddition(int id, E element){
			super(String.format("Trying to add vertex with id=%s and element=%s, "
					+ "but this element already exists with another ID!" , id , element.toString()) );
		}
	}
	private static <E> BadVertexAddition BVA(int id, E element){ 
		return new BadVertexAddition(id, element); 
	}
	@SuppressWarnings("unused")
	private static <E> BadVertexAddition BVA(String msg){ 
		return new BadVertexAddition(msg); 
	}
	
	/**
	 * Returns true if there exists a vertex containing this element.
	 * False otherwise.
	 */
	@Override
	public boolean hasVertex(E element) {
		return (element==null)? false : _elementMap.get( element ) != null;
	}
	
	@Override
	public Vertex<E> getVertex(E element) {
		return (element==null)? null : _elementMap.get( element );
	}
	
	
	public synchronized void addEdge(Vertex<E> a, Vertex<E> b){
		addEdge(a , b , 0);
	}
	public synchronized void addEdge(Vertex<E> a, Vertex<E> b, int cost){
		Vertex<E> av = addVertex( a );
		Vertex<E> bv = addVertex( b );
		Edge<E> e = new Edge<E>( av, bv , cost);
		__addEdge( e );
	}
	public synchronized void addEdge(int ia, int ib){
		addEdge( ia, ib, 0 );
	}
	public synchronized void addEdge(int ia, int ib, int cost){
		Vertex<E> a = addVertex(ia);
		Vertex<E> b = addVertex(ib);
		Edge<E> e1 = new Edge<E>( a, b, cost );
		__addEdge( e1 );
		//	a.edges.add(e1);
		//	b.edges.add(e1);
		//	edges.add( e1 );
		//	
		//	if( a.edges.size() == 1 ) connected_vertices++;
		//	if( b.edges.size() == 1 ) connected_vertices++;
	}
	public synchronized void addEdge(Edge<E> e){
		Vertex<E> a = addVertex(e.src);
		Vertex<E> b = addVertex(e.dst);
		Edge<E> e1 = new Edge<E>( a, b , e.cost());
		__addEdge( e1 );
		// Vertex<E> a = getVertex(e.src); 
		// Vertex<E> b = getVertex(e.dst);
		//	a.edges.add(e1);
		//	b.edges.add(e1);
		//	edges.add( e1 );
		//	
		//	if( a.edges.size() == 1 ) connected_vertices++;
		//	if( b.edges.size() == 1 ) connected_vertices++;
	}
	public synchronized void addEdge(E a, E b){
		addEdge( a, b, 0 );
	}
	public synchronized void addEdge(E a, E b, int cost){
		Vertex<E> va = addVertex( a );
		Vertex<E> vb = addVertex( b );
		Edge<E> e1 = new Edge<E>( va , vb, cost ); // can have multiple edges between edges// so don't look up
		__addEdge( e1 );
	}
	/**Internal addEdge which doesn't create new objects*/
	private void __addEdge( Edge<E> e ){
		e.src.edges.add( e );
		e.dst.edges.add( e );
		edges.add( e );
		if ( e.src.edges.size() == 1 ) connected_vertices++;
		if ( e.src.edges.size() == 1 ) connected_vertices++;
	}
	
	
	
	@Override
	public boolean hasEdge(Edge<E> e){
		return  edges.contains(e);
	}
	@Override
	public boolean hasEdge(Vertex<E> a, Vertex<E> b){
		return ( hasVertex(a) && hasVertex(b) && getVertex(a).hasNeighbor(b));
	}
	@Override
	public boolean hasEdge(int a, int b){
		return (_vertMap.containsKey(a) && _vertMap.containsKey(b)
			&& _vertMap.get(a).hasNeighbor(_vertMap.get(b)));
	}
	@Override
	public boolean hasEdge(E a, E b){
		return ( _elementMap.containsKey(a) && _elementMap.containsKey(b)
			&& _elementMap.get(a).hasNeighbor(_elementMap.get(b)) ); 
	}
	
	
	@Override
	public boolean hasVertex(int id){
		return _vertMap.containsKey(id);
	}
	@Override
	public boolean hasVertex(Vertex<E> a){
		return (a==null)? false : _vertMap.containsKey(a.id);
	}
	
	@Override
	public Vertex<E> getVertex(int id){
		return (hasVertex(id))? _vertMap.get(id) : null;
	}
	@Override
	public Vertex<E> getVertex(Vertex<E> a){
		return (a==null)? null : getVertex(a.id);
	}

	@Override
	public Edge<E> getEdge(Vertex<E> a, Vertex<E> b){
		Vertex<E> A = getVertex(a);
		return A.getEdge(b);
	}
	@Override
	public Edge<E> getEdge(int a, int b) {
		Vertex<E> A = getVertex(a);
		Vertex<E> B = getVertex(b);
		if ( A != null && B != null){
			return A.getEdge(B);
		}
		return null;
	}
	

	
	/**Returns true iff there exists at least 1 vertex with
	 * no edges leading to or from it.*/
	public boolean hasDisjointNodes(){
		return connected_vertices < vertices.size();
		//	for( Vertex v : vertices){
		//		if ( v.edges.isEmpty() )
		//			return false;
		//	}
		//	return true;
	}

	/**Removes the given edge from this graph.  
	 * </p>
	 * The vertices incident on this edge will also no longer have this edge 
	 * connecting them */
	public synchronized void removeEdge(Edge<E> e){
		e.src.removeEdge(e);
		e.dst.removeEdge(e);
		edges.remove(e);
	}
	
	/**Removes the vertex from this graph.
	 * </p>
	 * After this operation all other vertices will have edges incident on 
	 * <code>v</code> removed as well. ie, all edges touching <code>v</code> 
	 * will also be removed */
	public synchronized void removeVertex(Vertex<E> v){
		// find the edges in v and remove them
		// then remove v
		List<Edge<E>> toRemove = new ArrayList<Edge<E>>();
		toRemove.addAll(v.edges);
		
		for(Edge<E> e : toRemove){
			removeEdge(e);
		}
		
		vertices.remove(v);
		_vertMap.remove(v.id);
		if( v.element != null )
			_elementMap.remove(v.element);
	}
}
