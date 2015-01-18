package com.mnasser.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Generic, binary tree implementation of a heap.
 * 
 * The main point of a heap is to maintain that each parent element has 
 * children that are larger than it.  This means the root of the tree will obviously
 * have the smallest element. 
 * 
 * 2 basic functions are provided : insert & remove root
 * 
 * By default this Heap will use the given comparator to decide on the comparison 
 * ordering of the given elements in order to figure out what the "minimum" key value 
 * to return should be.
 * 
 * This Heap can be initialized with the boolean flag <code>returnMax</code> set to true
 * in order to use the given comparator to return maximums instead of minimums.
 * 
 * @author mnasser
 *
 */
public class Heap<K> implements Iterable<K>{
	
	public static enum HEAP { 
		MAX_HEAP(true) , 
		MIN_HEAP(false);
		
		private final boolean type;
		HEAP(boolean b) { type = b;}
		public boolean toBoolean(){ return type; }
	};
	
	
	protected Comparator<K> comp = null;
	private final boolean returnMax;
	private int maxCapacity = Integer.MAX_VALUE;
	
	/**Creates a heap returning minimums with the given K comparator allowing no more than 
	 * maxCapacity elements to be inserted*/
	public Heap(Comparator<K> comparator, int maxCapacity){
		this(comparator, HEAP.MIN_HEAP, maxCapacity);
	}
	// Returns minimum values using the given comparator to determine minimalness
	public Heap(Comparator<K> comparator){
		this( comparator , HEAP.MIN_HEAP ); // returns minimum by default
	}
	// Returns either minimum or maximum values using the given comparator.
	public Heap(Comparator<K> comparator, HEAP returnMax){
		comp = comparator;
		this.returnMax = returnMax.toBoolean();
	}
	// Returns either minimum or maximum values using the given comparator.
	public Heap(Comparator<K> comparator, HEAP returnMax, int maxCapacity){
		comp = comparator;
		this.returnMax = returnMax.toBoolean();
		this.maxCapacity = maxCapacity;
	}
	
	// our heap will be in array form. We'll use math to detect positions of 
	// children and parents
	// TODO : Replace with dynamically growing array
	private List<K> heap = new ArrayList<K>();
	
	private int maxIndex = 0; 

	/**
	 * Attempts to add the element <code>key</code> to this heap.
	 * @param key The element to add
	 * @throws RuntimeException if the maximum capacity of this heap has been reached.
	 * @see offer(K key)
	 */
	public synchronized void insert(K key){ //, E element){
		if( heap.size() >= maxCapacity ) throw new RuntimeException("Maximum Heap capacity reached! Cannot insert new elements");
		
		heap.add( maxIndex, key ); // insert at the end of the array ( last open leaf in the tree)
		
		bubbleUp( maxIndex ); // move it up the tree if need be
		
		maxIndex++; // increment our internal counter 
	}
	
	/**
	 * Like <code>insert(K key)</code> but checks to see if there is still
	 * room in this heap's capacity for another element. 
	 * </p>
	 * @param key
	 * @return True if this element was added. False otherwise.
	 */
	public synchronized boolean offer(K key){
		if( heap.size() >= maxCapacity )
			return false;
		
		insert(key);
		
		return true;
	}
	
	/**
	 * Like <code>insert(K key)</code> but if there is no more
	 * room in this heap's capacity will <code>removeRoot()</code> 
	 * and then insert <code>key</code> into the heap. 
	 * 
	 * @param key New element to force into this heap
	 * @return The popped off root element that has been forced out. 
	 * Null if there was still capacity for more elements.
	 */
	public synchronized K force(K key){
		K oldRoot = null;
		if ( heap.size() >= maxCapacity ){
			oldRoot = removeRoot();
		}
		
		insert( key );
		
		return oldRoot;
		
	}
	
	/**
	 * Recursively moves a leaf node up the tree until it has a parent that
	 * maintains the heap invariant
	 * @param current
	 * @param k
	 */
	private void bubbleUp(int current){
		if ( current == 0 ) return; // we started from the bottom now we here .. at the top
		
		// let's check if this current node is greater than its parent
		int parent = getParent(current);
		
		if ( needsSwap( heap.get( parent ) ,  heap.get( current ) ) ){
			swap( parent, current );
			bubbleUp( parent ); // since a swap was needed, lets check if we need more
		}
		
	}
	
	// swaps the elements at the two given locations
	private void swap( int left, int right){
		K swap = heap.set( left ,  heap.get( right ) ); // push second element into first spot
		heap.set( right, swap); // shove first element into second spot
	}
	
	/**
	 * If this child doesn't compare properly to its
	 * parent, then a swap is warranted. 
	 * 
	 * @param parent
	 * @param child
	 * @return Returns true if the parent is strictly less than 
	 * (not equal to or greater than) the child when this Heap is initialized to 
	 * returning minimums.  Returns the maximums when initialized to return maximums
	 */
	private boolean needsSwap( K parent, K child ){
		int res = compare( parent, child );
		res = returnMax ? res * -1  : res ;
		
		if( res <= 0 )
			return false;
		else
			return true;
	}
	
	/**
	 * 
	 * @param parent
	 * @param child
	 * @return
	 */
	protected int compare( K parent, K child ){
		return comp.compare(parent, child);
	}
	
	/**
	 * Removes the root element. 
	 * 
	 * Rebalances the tree.
	 * @return
	 */
	public synchronized K removeRoot(){
		if( heap.isEmpty() ) throw new RuntimeException("Heap is empty! Cannot remove anything.");
		
		K root = heap.get(0);  // save for later
		
		maxIndex--;
		K lastLeaf = heap.remove( maxIndex );
		if ( ! heap.isEmpty() ) {
			heap.set( 0 , lastLeaf );  // massive promotion 
			bubbleDown( 0 ); // push down until it rests at a location that maintains heap invariant
		}
		
		return root;
	}
	
	/**
	 * Pushes the current element down the tree iff one of it's children
	 * does not maintain the heap invariant (that a parent is "less than" its children).
	 * 
	 * If this current element has children, and either of the children isn't greater 
	 * than it, then it will promote the smallest of its children and swap with it.
	 * @param index  Current 
	 * @param current
	 */
	private void bubbleDown(int current){
		K parent = heap.get(current);
		
		int child = getFirstChild( current );
		if( child >= maxIndex )	
			return; // no children. we are a leaf in the tree
		
		int sibling = child + 1 ; // does this parent have another child? ...
		
		if ( sibling >= maxIndex ) {
			// ...no, it has only 1 child. So check if it needs a swap and do so
			if ( needsSwap( parent, heap.get(child)) ){
				swap( current , child );
				
				bubbleDown( child ); // the parent has been pushed into the child's location
					// see if more push downs are required.
			}
		}
		else
		{
			// has 2 children.  Check if they are both smaller than the parent
			// swap with the smallest of the three
			boolean childSwap = needsSwap( parent, heap.get(child) );
			boolean siblingSwap = needsSwap( parent, heap.get(sibling) );
			
			int swapIdx = -1;
			if( childSwap ){
				if( siblingSwap){
					// both less than parent. Promote smallest 
					if( needsSwap( heap.get(child), heap.get(sibling)) ){
						// child is > sibling. Promote sibling as smallest
						swapIdx = sibling; 
					}
					else 
					{
						// child is small enough. 
						swapIdx = child;
					}
				}
				else{
					// swap with first child only
					swapIdx = child;
				}
			}
			else if( siblingSwap ){
				// swap with second child only
				swapIdx = sibling;
			}
			
			if( swapIdx > -1 ) {
				swap( current, swapIdx );
				
				bubbleDown( swapIdx );
			}
		}
	}

	/**Returns the number of elements in this heap*/
	public int size(){
		return heap.size();
	}
	
	/**Returns the maximum number of elements that can be added to this heap before 
	 * it's maximum capacity is reached.*/
	public int capacity(){
		return maxCapacity;
	}
	
	/**Returns true iff there is still room in this heap for more elements to add*/
	public boolean hasRoom(){
		return maxCapacity > heap.size();
	}
	
	@Override
	public Iterator<K> iterator() {
		return heap.iterator();
	}
	
	public Stream<K> stream() {
		return heap.stream();
	}
	
	@Override
	public String toString(){
		return heap.toString();
	}
	
	
	/** Returns the root element without removing it **/
	public K peek(){
		return  heap.isEmpty()? null :  heap.get(0);
	}
	
	/** Given index of a child, finds its parent **/
	public static int getParent(int child){
		return (int) Math.ceil( ((double) child) / 2.0 ) - 1;
	}
	/**
	 * Given index of a parent, finds its first (left most) child's index.
	 * The index of the second (right most) child is the left child index + 1. **/
	public static int getFirstChild(int parent){
		return (parent * 2) + 1; 
	}
	
	
	
	
	public static void print(Heap<? extends Comparable<?>> h){
		System.out.print("[");
		for( Comparable<?> c : h.heap ){
			System.out.print(c + ", ");
		}
		System.out.println("]");
	}
	
	public static void main(String[] args) {
	}
	
}
