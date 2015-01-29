package com.mnasser;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import com.mnasser.util.Heap;
import com.mnasser.util.Heap.HEAP;
import com.mnasser.util.HeapComparable;

public class LotsOfInterviewQuestions {

	private static Random RAND = new Random();
	private static NumberFormat NF = NumberFormat.getNumberInstance(); 
	// rand num generator between [0 , MAX)
	private static IntStream rand = IntStream.generate( () -> RAND.nextInt(Integer.MAX_VALUE) );
			
	@Test
	public void test1MillionRange(){
		List<Integer> nums = IntStream
				.generate( () -> RAND.nextInt(10) )
				.limit( 1_000_000 )
				.boxed() // turns back into a stream
				.collect( Collectors.toList() );
		
		// sort in Big-O( n ) time these numbers that we know only range from 0-9
		// ... easy. Just pass over each number incrementing how many times we've seen that value
		int[] counts = new int[10];
		
		// ... as we see each one keep our histogram counts going and ...
		nums.stream().forEach( n -> counts[n]++ );
		
		// ... just print them out 
		for( int c = 0; c < 10 ; c++ ) 
			System.out.println( c + " -> " + NumberFormat.getInstance().format( counts[c] ));
		
	}
	
	@Test
	public void oddNumberXORproblem(){
		int[] ar = { 1 ,1 ,2, 2, 3, 3,  4 , 5, 5, 6, 6 };
		
		int odd =  0;
		
		for( int i : ar ){
			System.out.print( odd + " XOR " + i + " = "  );
			odd = odd ^ i; // XOR our running counter.  
			System.out.println(odd);
			// First pass will be 0 XOR 1 = 1. ((b/c XOR'ing anything w/ 0 returns itself ).
			// 2nd pass is 1 XOR 1 = 0 (b/c XOR'ing anything w/ itself returns 0.)
			// 3rd pass is 0 XOR 2 = 2. (b/c again XOR'ing anything w/ 0 returns itself ).
			// ... etc ... until ...
			// 0 XOR 4 = 4
			// 4 XOR 5 = 1 ... AHA!  4 didn't repeat so it didn't flip back to zero.  We will now
			//				be carrying over the excess 'baggage' of the single odd-occurring number 4.
			// 1 XOR 5 = 4 ... and now the 4 reappears since we "cancelled-out" the XOR-ing of the pair of 5s
			// so long as we have *repeated* (meaning an even number of) occurrences,  that single 
			// oddly occurring integer can be resurfaced  
		}
		
		System.out.println();
		
		
		// let's try it for something that occurs 3 times (still odd - the first 2 appearances 'cancel-out').
		ar = new int[]{ 1, 1, 2, 2, 2, 3, 3, 4, 4 };
		
		odd = 0;
		
		for( int i : ar ){
			System.out.print( odd + " XOR " + i + " = "  );
			odd = odd ^ i; // XOR our running counter.  
			System.out.println(odd);
		}
		
		System.out.println();
		
		
		// the ORDERING of the other evenly occurring numbers doesn't even matter. The 'XORing' will 
		// will eventually 'catch up' to the repeated ints.
		ar = new int[]{ 1, 2, 3, 4, 5, 4, 3, 2, 1 };
		
		odd = 0;
		
		for( int i : ar ){
			System.out.print( odd + " XOR " + i + " = "  );
			odd = odd ^ i; // XOR our running counter.  
			System.out.println(odd);
		}
		
		System.out.println();
	}
	
	@Test
	public void bitWiseOdd(){
		int[] ar = { 1, 2, 3, 4 ,5 };
		
		for( int i : ar ){
			if ( (i & 1) == 1 ){ // bit wise and w/ 1 removes all leading zeros giving  
					// only the last bit of i to be saved if and'ed w/ 1's last positive bit.  
				System.out.println("Odd : " + i);
			}
		}
	}
	
	@Test
	public void isPowerOf2(){
		int[] ar = { 0 ,1 ,2 ,3, 4, 8, 15, 16, 17, 31, 32, 33, 64, 128, 129 };
		
		// if a number is a power of 2 ... then it has only one '1' bit and all other bits are 0.
		// If we subtract 1 from this number we will have another number with all 1's starting 
		// from the position to the right of the single '1' in the original power-of-2 number.
		// If we were to bit-wise AND these two numbers we will get 0.  If we don't get 0 then
		// this number wasn't a power of 2.
		// Ex : 
		//     00010000 (2^5)
		// &   00001111 (2^5  - 1)
		// _______________________
		//     00000000 (0)
		
		for( int i : ar ){
			if ( (i != 0) &&   (i  & (i-1)) == 0 )
				System.out.println( "Power of 2 : " + i);
		}
	}
	
	@Test
	public void swap2numWOswapSpace(){
		// given 2 numbers how do you swap them *in place*.  That is, without a third
		// int to act as a swap space?
		
		// ... easy!  XOR!  XOR the 2 numbers together to perform the swap:
		// a ^= b;
		// b ^= a;
		// a ^= b;
	
		int i = RAND.nextInt(Integer.MAX_VALUE);
		int j = RAND.nextInt(Integer.MAX_VALUE); 
		
		int observerI = i, observerJ = j;
		
		System.out.println("i = " + i);
		System.out.println("j = " + j);
		
		i ^= j; System.out.println( "i ^= j;  i = " + i);
		j ^= i; System.out.println( "j ^= i;  j = " + j);
		i ^= j; System.out.println( "i ^= j;  i = " + i);
		
		Assert.assertEquals( observerI , j );
		Assert.assertEquals( observerJ , i );
		
		
		System.out.println("i = " + i);
		System.out.println("j = " + j);
	}
	
	@Test
	public void reverseBitsOfIntegerAndPalindrome(){
		// Given a number how do you reverse the bits that comprise that number.
		// Also ... can we easily check if the bits make a palindrome?
		
		int n = RAND.nextInt( Integer.MAX_VALUE );
		
		int m = Integer.reverse( n );
		
		// in theory we could check if m == n but may not work for negative.
		// XOR will give us what we want
		
		System.out.printf("Bits of %s are %sa palindrome : %s\n", n, ((m ^ n)==0)?"":"NOT " ,m );
	}
	
	@Test
	public void exponents(){
		Assert.assertEquals( 1000 , recursiveExpBySquaring(10, 3));
		Assert.assertEquals( 256, recursiveExpBySquaring(2, 8));
	}
	private long recursiveExpBySquaring(long x, long n){
		System.out.printf("%s ^ %s" , x , n);
		if ( n == 1 ){
			System.out.println();
			return x;
		}else if(n % 2 == 0){  // n is even
			System.out.println();
			return recursiveExpBySquaring(x*x, n/2);
		}
		else { // n is odd
			System.out.printf("  --- >  %s * ( %s ^ %s  )\n" , x , x*x , (n-1)/2 );
			return x * recursiveExpBySquaring(x*x, (n-1)/2);
		}
	}
	
	@Test
	public void findSmallestOfaLot(){
		// Keep the smallest 10,000 numbers in a random sequence of 1 Million  
		// we will add to a Heap w/ max capacity 10k
		// It is a max-Heap, meaning the largest value in the group appears at the root.
		// As we go over the sequence of random numbers, we insert until we hit capacity
		// and then we only insert into the heap if the next value is *smaller* than that
		// largest value at the root.
		
		Heap<Integer> heap = new HeapComparable<Integer>(HEAP.MAX_HEAP, 10_000);
		
		
		rand//.parallel()  // FIXED causes race-condition w/ multiple concurrent calls to heap.insert
			//.unordered() //
			.limit( 1_000_000 )
			.forEach( x -> {
				if ( ! heap.offer(x) ) { // no room left if offer fails
					if( x < heap.peek() )
						heap.force( x ); // force our way in; this removes root
				}
		});
		
		Assert.assertEquals( 10_000 , heap.size() );
		
		System.out.println( "Largest  element of bottom 10k : " + NF.format( heap.peek() ) );
		System.out.println( "Smallest element of bottom 10k : " +
				NF.format(heap.stream().mapToInt( x -> x ).min().getAsInt()) );
		
	}
	
	@Test
	public void findSmallestOfaBunch(){
		int[] ar = { 1, 2, 3123, 32,12 ,123 ,1232355,4, 6647,34, 34, 563,67,647 };
		
		Heap<Integer> heap = new HeapComparable<Integer>( HEAP.MAX_HEAP,  5  );
		
		for( int x : ar ){
			if( ! heap.offer( x ) ){
				if ( x < heap.peek() )
					heap.force( x );
			}
		}
		
		Assert.assertEquals( 32 , heap.peek().intValue() );
		Assert.assertEquals( 1 , heap.stream().mapToInt( x -> x ).min().getAsInt() );
		
		System.out.println(heap);
	}
	
	
	public int g(int x ){
		return x * x + 1 ;
	}
	
	public int f(int x){
		return x + 5;
	}
	
	@Test
	public void functions(){
		System.out.println("Hi Ali : ");
		IntStream.range(-10, 10).boxed().forEach(
				x -> {  
					int y = g(x);
					System.out.println("[ "+x + "] g(x) = " + y  + " f(x) =" + f(x) + " f("+y+") = " + f(g(x)));}
				);
	}
	
	@Test
	public void testRecursion(){
		recursion(0 ,  3);
	}
	public void recursion(int running_total, int current_adder){
		if( current_adder == 100 ) return;
		System.out.println( "running total = "+ running_total + ". Current_adder = " + current_adder);
		running_total = running_total +  current_adder;
		
		recursion( running_total, current_adder + 1 ); 
	}
	
	
	
	@Test
	public void testUtopianTree(){
		utopianTree(0);
		utopianTree(1);
		utopianTree(2);
		utopianTree(3);
		utopianTree(4);
		utopianTree(5);
		utopianTree(6);
		Assert.assertEquals( 1   , grow(0 ) );
		Assert.assertEquals( 2   , grow(1 ) );
		Assert.assertEquals( 3   , grow(2 ) );
		Assert.assertEquals( 6   , grow(3 ) );
		Assert.assertEquals( 7   , grow(4 ) );
		Assert.assertEquals( 14  , grow(5 ) );
		Assert.assertEquals( 15  , grow(6 ) );
		Assert.assertEquals( 30  , grow(7 ) );
		Assert.assertEquals( 31  , grow(8 ) );
		Assert.assertEquals( 62  , grow(9 ) );
		Assert.assertEquals( 63  , grow(10) );
		Assert.assertEquals( 126 , grow(11) );
	}
	public static void utopianTree(int cycles){
		System.out.println(cycles + " -> " + grow(cycles));
	}
	
	/** O(1) time*/
	public static int grow(int cycles){
		int temp = (cycles % 2 == 0)? cycles >> 1 : (cycles + 1) >> 1; // odd number is dealt like the even number ahead of it
		// temp +1 is how many bits we need to shift to the right while adding ones to it.
		int res = 1 << (temp); // we want to right shift and pad res w/ 1's a total of 'temp+1' bits 
		res += (res-1); // fill in all the 0's on the right of our left-shifted 1. 
		return (cycles % 2 == 0)? res : res - 1; // if we were dealing w/ odd cycles subtract 1
		
		/* This function should return the following mapping
		        0 = 1 // only 1st bit on
		        1 = 2
		        2 = 3  // both 1&2 bit on
		        3 = 6
		        4 = 7  // 1, 2 & 3rd bits on 
		        5 = 14
		        6 = 15 // 1 - 4th bits on
		        7 = 30
		        8 = 31 // 1 - 5th bits on
		        9 = 62
		        10 = 63 // 1 - 6th bits on
		        11 = 126
		        12 = 127 // 1 - 7th bits on
		        13 = 254 
		        14 = 255 // 1 - 8th bits on
		 */
	}
	
	@Test
	public void testMaximizeXOR(){
		Assert.assertEquals( 15 , maximizeXOR(1, 10) );
		Assert.assertEquals( 7 , maximizeXOR(10,15) );
		Assert.assertEquals( 16383 , maximizeXOR(356,9852) );
	}
	
	public static int maximizeXOR(final int L, final int  R){
		if ( L == R ) return 0;
		// start with the right most bits.
		// as we walk up the number of bits in both numbers we should be able
		// to construct 2 numbers w/ *alternating* 1's or 0's in order to maximize the XOR of said 2 numbers.
		// if the two bits at the nth position are already different, do nothing; 
		// if they are the same, see if you can add 1 to the lower number or subtract 1 from the larger.
		
		int bit = 1;
		int max_xor = 0;
		while( bit <= R ){
			if ( (L & bit) != (R & bit) ){
				max_xor += bit;
			}
			else{
				// can we add 1 to the lower? or vise versa?
				if ( L + bit <= R  ||  R - bit >= L ){
					max_xor += bit; // this is also acceptable.
				}
			}
			// else we can' do anything w/ this bit
			bit = bit << 1; 
		}
		
		return max_xor;
	}
	
	@Test
	public void testAlternateChars(){
		Assert.assertEquals( 3  , alternateChars( "AAAA" )  );
		Assert.assertEquals( 4  , alternateChars( "BBBBB" )  );
		Assert.assertEquals( 0  , alternateChars( "ABABABAB" )  );
		Assert.assertEquals( 0  , alternateChars( "BABABA" )  );
		Assert.assertEquals( 4  , alternateChars( "AAABBB" )  );
	}
	
	
	public static int alternateChars(final String word){
		int deletions = 0;
		int idx = 0;
		char previous_c = (char) -1;
		do {
			char c = word.charAt(idx);
			if ( previous_c != (char) -1  &&  previous_c == c )
				deletions ++;
			
			previous_c = c;
			idx++;
		}
		while( (idx < word.length()) );
		
		return deletions;
	}
	
	@Test
	public void testFindSmallestMax(){
		Assert.assertEquals( 20 , findSmallestMaximum(new int[]{10,100,300,200,1000,20,30}, 3) );
		Assert.assertEquals( 3 , findSmallestMaximum(new int[]{1,2,3,4,10,20,30,40,100,200} , 4) );
		Assert.assertEquals( 2 , findSmallestMaximum(new int[]{10,20,30,100,101,102} , 3) );

	}
	
	/**
	 * Given a set of numbers, finds which group of K elements within nums 
	 * having the smallest difference between the smallest and largest elements of K.  
	 *  
	 * @param nums array of numbers to find the smallest set of K members that have
	 * the smallest distance between them.
	 * @param K size of subgroup
	 * @return the smallest distance between any grouping of K members of nums*/
	public static int findSmallestMaximum(int[] nums, int K){
		// we will use the space between consecutive sorted numbers as a sort of 'edge cost'
		// between two nodes on a singly-linked graph
		Arrays.sort(nums);
		
		int[] edges = new int[ K - 1 ]; // our differences
		int smallest_difference = -1;
		int current_diff = 0;
		
		for( int ii=0; ii< nums.length - 1; ii++  ){
			
			int diff = nums[ii + 1] - nums[ii];
			
			if ( ii < edges.length - 1 ){
				
				edges[ ii ] = diff;
				
			}else{
				// we have potential sub groups. Keep track of our total edge costs
				if ( smallest_difference == -1 ){
					edges[ ii % edges.length ] = diff;
					current_diff =  Arrays.stream(edges).sum();
					smallest_difference = current_diff;
				}
				else{
					// this is a new diff, so let's push out the old k and put in the new one
					current_diff -= edges[ ii % edges.length ];
					current_diff += diff;
					
					// is the new max distance smaller than our reigning small champion?
					if ( current_diff < smallest_difference )
						smallest_difference = current_diff; // we have a new lower total edge cost
					
					// move our edge window along 
					//edges = Arrays.copyOfRange(edges, 1, edges.length);
					edges[ ii % edges.length ] = diff; 
				}
			}
		}
		
		return smallest_difference;
	}

	@Test
	public void testIsFibonacci(){
		Assert.assertTrue( isFib(0) );
		Assert.assertTrue( isFib(1) );
		Assert.assertTrue( isFib(34) );
		
		Assert.assertFalse( isFib( Integer.MAX_VALUE ) );
		
	}
	
    public static boolean isFib(final long f){
        if( f == 0 || f == 1 ) return true;
        long r1 = 1 , r2 = 1 , c = 0;
        do {
            c = r1 + r2;
            //System.out.println( c );
            r1 = r2;
            r2 = c;
        }while( c < f );
            
        return ( c == f );
    }
	
}

