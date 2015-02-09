package com.mnasser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import com.mnasser.util.CountingMap;



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
	
	
	/**************************************************/
	
	/**
	 * This is a game that given a string will find 2, non-overlapping, palindromic
	 * sub-sequences such that the product of their lengths is optimal.
	 * </p>
	 * Example : 
	 * <pre> eeegeeksforskeeggeeks </pre>
	 * has optimal 2 non overalapping palindromic sub-sequences of :
	 * <pre> eeeee</pre>  and <pre> skeeggeeks </pre>.
	 * That gives product of lengths of <pre> 5 * 10 = 50 </pre> 
	 * 
	 * @param word
	 */
	public static int playWithWords(String word){
		
		int subproblems[][] = getMaxPalindromicSubsequenceSubProblems(word);
		// now that we have all sub problems, we can try to find the maximum lenght product
		
		int len = word.length();
		int product = 0;
		for( int pivot = 0, end = len - 1; pivot < end; pivot ++ ){
			product = Math.max( product  ,  subproblems[0][pivot]  *  subproblems[pivot+1][end] );
		}
		
		return product;
		
		// there will be 2, non-overlapping, palindromes that are sub-sets of characters
		// in the given word.  Find the two longest palindromes such that we 
		// maximize  pal_1.length * pal_2.length 
		// For this, we are in theory trying to 'construct' palindromes from a given
		// group of letters.  We can do this by 
		//	int n = word.length();
		//	int subproblems[][] = new int[n][n];
		//	for( int ii = 0; ii < n ; ii ++){
		//		subproblems[0][ii] = 0 ; // degenerate case of left or right length == 0
		//		subproblems[ii][0] = 0 ;
		//	}
		//	
		//	for ( int left = 1; left < n; left ++  ){
		//		for( int right = 1; right < (n - left); right ++ ){
		//			int l = 0, r = 0;
		//			if ( isPalindrome( word , 0, left) ){
		//				l = left;
		//			}
		//			if ( isPalindrome( word , (n - right), n )){
		//				r = (n-right);
		//			}
		//			int prod = l * r;
		//			
		//		}
		//	}
	}
	
	/** Maintains a palindrome as more and more */
	static class GrowingPalindrome{
		private StringBuilder palindrome = new StringBuilder();
		private int size = 0; // current palindrome size
		//private int capacity = 0; // total characters in palindrome and leftovers
		private CountingMap<Character> leftOvers = new CountingMap<Character>();
		private CountingMap<Character> charCount = new CountingMap<Character>();
		
		void addString( String word ){
			for( char c : word.toCharArray() ){
				addChar( c );
			}
		}
		
		void addChar(char c){
			switch( size ){
			case 0 : append( c ); break;
			case 1 : 
				if ( palindrome.charAt(0) == c ) {
					append(c);
				}
				else {
					// see if we can use leftOvers to build out a palindrome
					if ( leftOvers.has(c) ){
						prepend( c ); 
						leftOvers.dec(c);
						append( c );
					}
					else {
						leftOvers.inc(c); // nothin' doin'. Save for later 
					}
				}
				break;
			default :
				switch ( size % 2 ) {
				case 0 : // even sized palindrome - stick us in the middle
					midpend( c );
					break;
				case 1 :
					int center = center();
					if ( palindrome.charAt(center) == c ) { // great
						midpend( c );
					}
					else{
						leftOvers.inc( c );
					}
					break;
				}
			}
		}
		
		private int center(){
			return (size % 2 == 0) ? size / 2  : (size + 1) / 2; 
		}
		
		private void prepend(char c){palindrome.insert(0, c); charCount.inc(c); size++; }
		private void  append(char c){palindrome.append(c);    charCount.inc(c); size++; }
		private void midpend(char c){palindrome.insert(center(), c); charCount.inc(c); size++; }
		
		public int size(){ return size; }
		public int capacity(){ return size + leftOvers.size() ;}
	}
	
	//	public static void hasPalindrome(String word, int start, int end){
	//		// need to see if there is a palindrome in the sub-string given.
	//		// also need it to be the longest palindrome if so.  
	//		// can do this buy removing characters if need be.
	//		int n = start - end;
	//		int[] subproblems = new int[n];
	//		subproblems[0] = 0;
	//		subproblems[1] = 1;
	//		for( int ii = 2; ii < n ; ii ++ ){
	//			subproblems[ii] = isPalindrome(word, start, start + ii - 1) ?  ii : subproblems[ii]; 
	//		}
	//	}
	
	// Is substring of word p a palindrome from start index to end index (exclusive)? 
	public static boolean isPalindrome(String p, int start, int end){
		int n = end-start;
		if ( n == 0 ) return false; // huh?
		else if ( n == 1 ) return true;
		for ( int ii = start, jj = end - 1; ii <= jj ; ii++, jj-- ){
			if ( p.charAt(ii) != p.charAt(jj) ) return false;
		}
		return true;
	}
	public static boolean isPalindrome(String p){
		return isPalindrome(p , 0 , p.length() );
	}
	
	
	@Test
	public void testIsPal(){
		Assert.assertTrue( isPalindrome("a", 0, 1) ) ;
		Assert.assertTrue( isPalindrome("abc", 0, 1) ) ;
		Assert.assertTrue( isPalindrome("abc", 1, 2) ) ;

		Assert.assertTrue( isPalindrome("a", 0, 1) ) ;
		Assert.assertTrue( isPalindrome("aba", 0, 3) ) ;
		
		Assert.assertFalse( isPalindrome("ab", 0, 2) ) ;
		Assert.assertFalse( isPalindrome("ab") ) ;
		Assert.assertFalse( isPalindrome("abc") ) ;
		Assert.assertFalse( isPalindrome("abc" , 0 , 3) ) ;
		
		Assert.assertTrue( isPalindrome("appa" , 0 , 4) ) ;
		Assert.assertTrue( isPalindrome("appa" , 1 , 3) ) ;
		Assert.assertTrue( isPalindrome("appa") ) ;
	}
	
	
	/**
	 * Returns the optimal solutions for all possible maximum palindromic subsequences 
	 * of lengths 1  thru word.length() for all substrings of given word.
	 * </p>
	 * Uses a Dynamic Programming approach to produce a 2-dimensional (NxN where N = word.length)
	 * array of subproblem results. The x and y coordinates of this 2-D array are the 
	 * start and end indices of {@code word} and the values at those coordinates are 
	 * the lengths of the maximum palindromic sub-sequence of characters from indices 
	 * x thru y.
	 */
	// O(n^2)
	public static int[][] getMaxPalindromicSubsequenceSubProblems(String word){
		int n = word.length();
		int[][] subproblems = new int[n][n]; // start & end characters within the word 
		for( int ii = 0; ii < n ; ii ++){
			subproblems[ii][ii] = 1; // start == end means a palindrome of length 1
		}
		
		// try out word lengths of 2 and more.
		// max of each becomes the problem needed for larger and larger lengths
		for( int len = 2; len <=n; len++){
			for( int start = 0; start <= n - len; start ++  ){
				
				int end = start + len - 1; 
				
				int subprob = 
						
					(word.charAt(start) == word.charAt(end))  
					
					?   2 +  (  (len==2) ?  0  : subproblems[start+1][end-1]  )
							
					:   Math.max( subproblems[start][end-1]  , subproblems[start+1][end] );
					
				subproblems[start][end] = subprob; 
			}
		}
		
		return subproblems;
	}
	
	public static int getMaxPalindromicSubsequenceLength(String word){
		return getMaxPalindromicSubsequenceSubProblems(word)[ 0 ][word.length() - 1 ];
	}
	
	
	
	@Test
	public void testPlayWithWords(){
		System.out.println( getMaxPalindromicSubsequenceLength("ee"));
		System.out.println( getMaxPalindromicSubsequenceLength("abccba"));
		System.out.println( getMaxPalindromicSubsequenceLength("eeggeeksskeeggeeks"));
		System.out.println( getMaxPalindromicSubsequenceLength("eeegeeksforskeeggeeks"));
		System.out.println( playWithWords("eeegeeksforskeeggeeks"));
		System.out.println( playWithWords("eeggeeksskeeggeeks"));
		System.out.println( playWithWords("1234567890987654321"));
	}
	
	
	
	/*******************************************************************/
	
	
	
	
	
	/***************   LONGEST COMMON SUBSEQUENCE  ****************/
	
	
	public static int LCS(String a, String b){
		int alen = a.length(), blen = b.length();
		int[][] subproblems = new int[ alen + 1 ][ blen + 1 ];
		for( int ii = 0; ii < alen; ii++ ){
			subproblems[ii][0] = 0;
		}
		for( int jj = 0; jj < blen; jj++ ){
			subproblems[0][jj] = 0;
		}
		
		for( int ii = 1; ii <= alen; ii++ ){
			for( int jj = 1; jj <= blen; jj++){
				if ( a.charAt(ii-1) == b.charAt(jj-1) ){
					subproblems[ii][jj] = 1 + subproblems[ii-1][jj-1];
				}
				else{
					subproblems[ii][jj] = Math.max( subproblems[ii][jj-1]  , subproblems[ii-1][jj] );
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		int ax = alen , bx = blen;
		while( ax > 0 && bx > 0 ){
			int s = subproblems[ax][bx];
			if ( a.charAt(ax-1) == b.charAt(bx-1) ){
				sb.insert( 0 , " " + a.charAt(ax-1)); // backwards
				ax--; bx--;
			}else{
				if ( s == subproblems[ax][bx-1] ){
					bx--;
				}else{
					ax--;
				}
			}
		}
		System.out.println( sb.toString().trim() );
		
		return subproblems[alen][blen];
	}

	public static int LCS(int[] a, int[] b){
		int alen = a.length, blen = b.length;
		int[][] subproblems = new int[ alen + 1 ][ blen + 1 ];
		for( int ii = 0; ii < alen; ii++ ){
			subproblems[ii][0] = 0;
		}
		for( int jj = 0; jj < blen; jj++ ){
			subproblems[0][jj] = 0;
		}
		
		for( int ii = 1; ii <= alen; ii++ ){
			for( int jj = 1; jj <= blen; jj++){
				if ( a[ii-1] == b[jj-1] ){
					subproblems[ii][jj] = 1 + subproblems[ii-1][jj-1];
				}
				else{
					subproblems[ii][jj] = Math.max( subproblems[ii][jj-1]  , subproblems[ii-1][jj] );
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		int ax = alen , bx = blen;
		while( ax > 0 && bx > 0 ){
			int s = subproblems[ax][bx];
			if ( a[ax-1] == b[bx-1] ){
				sb.insert( 0 , " " + a[ax-1]); // backwards
				ax--; bx--;
			}else{
				if ( s == subproblems[ax][bx-1] ){
					bx--;
				}else{
					ax--;
				}
			}
		}
		System.out.println( sb.toString().trim() );
		
		return subproblems[alen][blen];
	}

	
	@Test
	public void testLongestCommonSubsequence(){
		System.out.println( LCS( "12341" , "341213" ) );
		
		System.out.println( LCS( new int[]{16, 27, 89, 79, 60, 76, 24, 88, 55, 94, 57, 42, 56, 74, 24, 95, 55, 33, 69, 29, 14, 7, 94, 41, 8, 71, 12, 15, 43, 3, 23, 49, 84, 78, 73, 63, 5, 46, 98, 26, 40, 76, 41, 89, 24, 20, 68, 14, 88, 26} ,
		new int[]{27, 76, 88, 0, 55, 99, 94, 70, 34, 42, 31, 47, 56, 74, 69, 46, 93, 88, 89, 7, 94, 41, 68, 37, 8, 71, 57, 15, 43, 89, 43, 3, 23, 35, 49, 38, 84, 98, 47, 89, 73, 24, 20, 14, 88, 75}  ));

	}
	
	
	/** Given 1st (A) and 2nd (B) terms of a sequence,
	 *  computes the Nth term in the sequence defined as :
	 *  
	 *  T3 = T2*T2 + T1
	 *  
	 * @param A
	 * @param B
	 * @param N
	 */
	public static BigDecimal fibSquare(long a, long b, int N){
		BigDecimal A = new BigDecimal(a);
		BigDecimal B = new BigDecimal(b);
		BigDecimal next = new BigDecimal(0);
		
	    for(int ii = 3; ii <= N; ii ++){
	        next = B.multiply(B).add(A);
	        A = B;
	        B = next;
	    }
	    return next;
	}
	
	@Test
	public void testFibonacciSquared(){
		System.out.println(  fibSquare(0, 1, 5) );
		System.out.println(  fibSquare(0, 1, 10) );
	}
	
	
}






