package com.mnasser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;



public class DynamicProgrammingExamples {

	
	//	public static void main(String[] args) {
	//		DynamicProgrammingExamples.veryLargeMaxSubArrayFile();		
	//	}
	
	/**Given array of numbers, will find the maximum possible value
	 * of a continuous subset of the given array. */
	public static int findMaxContiguousValue(int... ar){
		if ( ar.length == 1 ) return ar[0];
	
		// there are up to n possible contiguous subset window sizes. we will
		// attempt to find the maximum value for each window size and
		// return the maximum value seen overall
		//int i = 0, j = 1, max = 0;
		int max = Integer.MIN_VALUE;
		
		
		// for window size = 1 
		for( int a : ar ){
			max = Math.max( max, a);
		}
		
		int last_window_initial_sum = ar[0];
		
		// current window size we are working with  
		for( int window = 2; window <= ar.length; window ++){
			
			int window_sum = 0;
			
			// determine the sum of the window-sized subset starting
			// from each j'th position
			
			
			for ( int i = 0; i <= ar.length - window; i ++ ){
				
				
				for( int j = i ; j <  i + window; j ++){
					// window > 2
					if ( i > 0  && j == i ){ // optimization for each subsquent window being calculated
						window_sum -= ar[i-1]; // remove start of last window ..
						window_sum += ar[i + window - 1]; // add end of new window
						// this brings us down from O(n^3) down to -> O(n^2)
						break;
					}
					if ( i == 0 && j == 0 ) { // the first subset of a new window size...
						// .. is just the last start of a new window size + 1
						window_sum = last_window_initial_sum + ar[i + window - 1];
						last_window_initial_sum = window_sum; // save for later 
						// this cuts out about 1/2 of our O(n^2) work  
						break;
					}
					
					window_sum += ar[j]; // will probably never reach (?)
					
				}
				
				//System.out.println( "sum("+ Arrays.toString( Arrays.copyOfRange(ar, i, i + window) ) + ") = " + window_sum );
				
				max = Math.max( window_sum, max );
			}
		}
		
		return max;
	}
	
	/** returns maximum value of a continuous subarray of ar */
	// O(n)
	public static int findMaxContiguousValueDynamic(int... ar){
		int optimal_so_far = ar[0]; // degenerate case of ar.length = 1
		int max = optimal_so_far;
		
		for( int j = 1; j < ar.length ; j ++ ){
			optimal_so_far = Math.max( ar[j] , ar[j] + optimal_so_far );
			max = Math.max( max , optimal_so_far ); // keep track of hi-water mark
		}
		
		return max;
	}
	
	
	/**Given array of numbers, will find the maximum possible value 
	 * from any combination of a subset of numbers.
	 * </p>
	 * NOTE : will only consider subarrays of size 1 or more.  Will not 
	 * consider the EMPTY SET (value=0).
	 * */
	// O( n )
	public static int findMaxNonContiguousValue(int... ar){
		// we'll use a bit of dynamic programming here
		int[] subproblem = new int[ar.length]; // optimum solutions for subproblems
		subproblem[0] = ar[0]; // base case of array of 1
		
		int i = 1;
		while( i < ar.length ) {
			// next value of optimal solution either includes i^th value or not
			subproblem[ i ] = MAX( ar[i], ar[i] + subproblem[i-1] , subproblem[i-1] );
			i++;
		}
	
		return subproblem[ar.length - 1];
	}
	
	public static int MAX(int... ar){
		int max = Integer.MIN_VALUE;
		for( int i : ar ){
			if ( max < i ) max = i;
		}
		return max;
	}
	
	
	@Test
	public void testMaxContigValue(){
		
		Assert.assertEquals( 1 , findMaxNonContiguousValue( 1 ) );
		
		Assert.assertEquals( 10 , findMaxContiguousValue( 1, 2, 3, 4 ) );
		Assert.assertEquals( 10 , findMaxContiguousValue( 2, -1, 2, 3, 4, -5 ) );
		
		Assert.assertEquals( 10 , findMaxNonContiguousValue( 1, 2, 3, 4 ) );
		Assert.assertEquals( 11 , findMaxNonContiguousValue( 2, -1, 2, 3, 4, -5 ) );
		
		Assert.assertEquals( -1 , findMaxContiguousValue(-1, -2, -3, -4, -5, -6) );
		Assert.assertEquals( -1 , findMaxNonContiguousValue(-1, -2, -3, -4, -5, -6) );

		
		Assert.assertEquals( -1 , findMaxContiguousValue(-6, -5, -4, -3, -2, -1) );
		Assert.assertEquals( -1 , findMaxNonContiguousValue(-6, -5, -4, -3, -2, -1) );
		
		Assert.assertEquals( -3 , findMaxContiguousValue(-5, -3) );
		Assert.assertEquals( -3 , findMaxNonContiguousValue(-5, -3) );
		
		Assert.assertEquals( 21225 ,   findMaxContiguousValue(42, 6654, -678, 5665, 77 ,1 , 10, 20, 100, 101, -322, 9555) );
		Assert.assertEquals( 22225 , findMaxNonContiguousValue(42, 6654, -678, 5665, 77 ,1 , 10, 20, 100, 101, -322, 9555) );
	}
	
	@Test
	public void testMaxContiguValueDynamic(){
		Assert.assertEquals( 10 , findMaxContiguousValueDynamic( 1, 2, 3, 4 ) );
		Assert.assertEquals( 10 , findMaxContiguousValueDynamic( 2, -1, 2, 3, 4, -5 ) );
		Assert.assertEquals( -1 , findMaxContiguousValueDynamic(-1, -2, -3, -4, -5, -6) );
		Assert.assertEquals( -1 , findMaxContiguousValueDynamic(-6, -5, -4, -3, -2, -1) );
		Assert.assertEquals( -3 , findMaxContiguousValueDynamic(-5, -3) );
		Assert.assertEquals(21225,findMaxContiguousValueDynamic(42, 6654, -678, 5665, 77 ,1 , 10, 20, 100, 101, -322, 9555) );
		
	}
	
	public static final Random RAND = new Random();
	
	@Test
	public void testMaxContigValueRandom(){
		int[] ar = IntStream.generate( () -> RAND.nextInt(1000) ).limit( 10 ).toArray();
		// all positive integes,  so max contiguous subarray includes all elements
		int sum  = Arrays.stream( ar ).sum();
		Assert.assertEquals( sum , findMaxContiguousValue( ar ) );
		
		// larger
		ar = IntStream.generate( () -> RAND.nextInt(1000) ).limit( 1000 ).toArray();
		sum = Arrays.stream( ar ).sum();
		
		Assert.assertEquals( sum , findMaxContiguousValue( ar ) );
		
		// much larger
		ar = IntStream.generate( () -> RAND.nextInt(1000) ).limit( 100_000 ).toArray();
		sum = Arrays.stream( ar ).sum();
		
		// Assert.assertEquals( sum , findMaxContiguousValue( ar ) ); // takes toooo long to finish
		Assert.assertEquals( sum , findMaxContiguousValueDynamic( ar ) ); // takes toooo long to finish
		
		IntStream.range(0, 10).forEach( (i) -> {
			int[] arr = IntStream.generate( () -> RAND.nextInt()% 10 ).limit( 10 ).toArray();
			//sum = Arrays.stream( ar ).sum();
			int max = findMaxContiguousValueDynamic( arr );
			
			System.out.println("max = " + max + ". " + Arrays.toString(arr));
			
			Assert.assertEquals( max , findMaxContiguousValue( arr ));
			Assert.assertEquals( max , findMaxContiguousValueDynamic( arr ));
		} );
		
		ar = new int[]{ -1, -1, -1, -1, -1,   1    , -1, -1, -1, -1,};
		sum = findMaxContiguousValue( ar ); 
		Assert.assertEquals( 1 , sum );
		Assert.assertEquals( sum , findMaxContiguousValueDynamic( ar ));
		
		ar = new int[]{ 1, 1, 1, 1,   -1   , 1, 1, 1, 1};
		sum = findMaxContiguousValue( ar ); 
		Assert.assertEquals( 7 , sum );
		Assert.assertEquals( sum , findMaxContiguousValueDynamic( ar ));
		
		ar = new int[]{ 1, 1, 1, 2,   -10   , 1, 1, 1, 20};
		sum = findMaxContiguousValue( ar ); 
		Assert.assertEquals( 23 , sum );
		Assert.assertEquals( sum , findMaxContiguousValueDynamic( ar ));
		
		// Assert.assertEquals( sum , findMaxContiguousValue( ar ) ); // takes toooo long to finish
		//Assert.assertEquals( sum , findMaxContiguousValueDynamic( ar ) ); // takes toooo long to finish
	}
	
	
	@Test
	public void testmaxNonContiguousRandom(){
		int[] ar = IntStream.generate( () -> RAND.nextInt()%1000 ).limit( 10 ).toArray(); // could be neg or pos
		int sum = Arrays.stream(ar).filter( x ->  x > -1  ).sum(); // max any-set is just all positive numbers
		Assert.assertEquals( sum , findMaxNonContiguousValue( ar ) );
		
	}

	
	@Test
	public void testVeryLargeMaxSubArrayFile(){
		veryLargeMaxSubArrayFile();
	}
	public static void veryLargeMaxSubArrayFile(){
		int[][] answers = new int[][] { 
			{2617065, 172083036},
			{1274115, 193037987},
			{2202862, 163398048},
			{2454939, 240462364},
			{3239908, 186256172},
			{2486039, 202399661},
			{1092777, 137409985},
			{962621 , 135978139},
			{3020911, 224370860},
			{1755033, 158953999} 
		};
		
		try( BufferedReader br = new BufferedReader(new InputStreamReader(
				DynamicProgrammingExamples.class.getResourceAsStream("max_subarray_input.txt") ));
		){
			int T = Integer.parseInt( br.readLine() );
			for( int t = 0; t < T ; t ++){
				int N = Integer.parseInt( br.readLine() );
				int[] A = new int[N];
				String[] Ns = br.readLine().split("\\s+");
				for( int n = 0; n < N ; n ++ ){
					A[n] = Integer.parseInt(Ns[n]);
				}
				
				int ans1 = answers[t][0];
				int ans2 = answers[t][1];
			
				//System.out.print(t + ")  A[ "+N+" ] ");
				int max = findMaxContiguousValueDynamic(A);
				//System.out.print("max = " + max);
				Assert.assertEquals( max , findMaxContiguousValueDynamic(A));
				Assert.assertEquals( max , ans1);
				
				int maxNon = Arrays.stream( A ).filter( x ->  x>=0 ).sum();
				//int maxSort= Arrays.stream( A ).sorted().filter( x -> x>= 0 ).sum();
				//System.out.println(". max Non Contiguous = " + maxNon + ", max sorted Non Contiguous = " + maxSort);
				Assert.assertEquals( maxNon , findMaxNonContiguousValue(A));
				Assert.assertEquals( maxNon , ans2);
				
				System.out.printf("%s %s", 
	                    findMaxContiguousValueDynamic( A ) ,
	                    findMaxNonContiguousValue( A )
				);
				if( t < T - 1) 
					System.out.printf("\n");
			}
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	/**
	 * Implements a variant of the knapsack problem in which items can be 
	 * placed in the knapsack any number of times. 
	 * @param K
	 * @param items 
	 * @return returns the closest value to k using the n given items 
	 */
	public static int unlimitedKnapsack(int K, int... items){
		if ( items.length == 0 || K < 1 ) return 0;
		
		Arrays.sort( items ); //O(nlogn)
		int[][] subproblems = new int[ K + 1 ][ items.length ];
		for(int i = 0; i < items.length; i++ )
			subproblems[0][i] = 0; // degenerate case of knapsack that can hold 0 weight
		
		// what is the best solution for each value of k ...
		for( int i=0 , len=items.length; i < len; i++ ){
			// ... and is the current item part of that best solution?
			for( int weight = 1; weight <= K ; weight++ ){
				int n = items[i];
				if ( K % n == 0 ) return K; // Short-circuit if n fits perfectly we're done done.
				if ( n > weight ){  // can't fit. Best solution doesn't include me 
					subproblems[weight][i] = (i == 0)? 0 : subproblems[weight][i-1];  
				}
				else{
					int max_fit = weight - (weight % n); //how many can we fit?
					int weight_wo_me = weight - max_fit;
					weight_wo_me = ( weight_wo_me < 1 )? 0 : weight_wo_me;
					int subprob = ( i - 1 < 0)? 0 :  subproblems[weight_wo_me][ i - 1 ];
					subproblems[weight][i] = MAX( max_fit + subprob ,  (i==0)?0: subproblems[weight][i-1]  );
				}
				
				//printDblAr(subproblems);
				
			}
		}
		
		return subproblems[K][items.length-1];
	}
	
	//	private static void printDblAr(int[][] ar){
	//		for( int ii = 0; ii < ar.length; ii++ ){
	//			System.out.print( ((ii==0)?'[':' ') );
	//			for( int jj = 0; jj < ar[ii].length; jj++)
	//				System.out.print(ar[ii][jj] + ", ");
	//			System.out.println( ((ii==ar.length-1)?']':' ') );
	//		}
	//	}

	@Test
	public void testUnlimitedKnapSack(){
		Assert.assertEquals( 10  , unlimitedKnapsack(10, 1, 1));
		Assert.assertEquals( 10  , unlimitedKnapsack(10, 2, 3));
		Assert.assertEquals(  9  , unlimitedKnapsack(10, 3, 4));
		Assert.assertEquals(  9  , unlimitedKnapsack(10, 8, 9));
		Assert.assertEquals(2000 , unlimitedKnapsack(2000, 1, 1999));
		Assert.assertEquals(1999 , unlimitedKnapsack(1999, 1, 2000));
		Assert.assertEquals( 12  , unlimitedKnapsack(12, 1, 6, 9));
		Assert.assertEquals(  9  , unlimitedKnapsack(9, 3, 4, 4, 4, 8));
		
		System.out.println(unlimitedKnapsack(9875654, 54, 789, 225, 22, 899));
	}
	
}


