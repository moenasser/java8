package com.mnasser;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import static com.mnasser.DynamicProgrammingExamples.MAX;

public class HackerRankSamples {
	/** returns index of the offending single character that would turn this into 
	 * a palindrome.*/
	public static int offByOnePalindrome(String palindrome){
		char[] p = palindrome.toCharArray();
		
		int badIdx = -1;
		int a = 0, z = palindrome.length() - 1;
		for( ;  a <= z ; a++, z-- ){
			if ( p[a] == p[z] ) continue;
			if ( p[a] == p[z-1]  && p[a+1] == p[z-2]){
				badIdx = z;
				z--; // skip a letter from the right
			}
			else if( p[a+1] == p[z] && p[a+2] == p[z-1]){
				badIdx = a;
				a++; // skip a letter from the left 
			}
		}
		
		return badIdx;
	}
	
	@Test
	public void testOff1Palindrome(){
		Assert.assertEquals( 3  , offByOnePalindrome("aaab"));
		Assert.assertEquals( 0  , offByOnePalindrome("baa") );
		Assert.assertEquals( -1 , offByOnePalindrome("aaa") );
		Assert.assertEquals( 44 , offByOnePalindrome("hgygsvlfcwnswtuhmyaljkqlqjjqlqkjlaymhutwsnwcwflvsgygh") );
		
		Map<String,Integer> ans = new LinkedHashMap<String,Integer>();
		ans.put("quyjjdcgsvvsgcdjjyq"                                                                                        , 1 );
		ans.put("hgygsvlfwcwnswtuhmyaljkqlqjjqlqkjlaymhutwsnwcflvsgygh"                                                      , 8 );
		ans.put("fgnfnidynhxebxxxfmxixhsruldhsaobhlcggchboashdlurshxixmfxxxbexhnydinfngf"                                    , 33);
		ans.put("bsyhvwfuesumsehmytqioswvpcbxyolapfywdxeacyuruybhbwxjmrrmjxwbhbyuruycaexdwyfpaloyxbcpwsoiqtymhesmuseufwvhysb", 23);
		ans.put("fvyqxqxynewuebtcuqdwyetyqqisappmunmnldmkttkmdlnmnumppasiqyteywdquctbeuwenyxqxqyvf"                          , 25);
		ans.put("mmbiefhflbeckaecprwfgmqlydfroxrblulpasumubqhhbvlqpixvvxipqlvbhqbumusaplulbrxorfdylqmgfwrpceakceblfhfeibmm"  , 44);
		ans.put("tpqknkmbgasitnwqrqasvolmevkasccsakvemlosaqrqwntisagbmknkqpt"                                                , 20);
		ans.put("lhrxvssvxrhl"                                                                                               , -1);
		ans.put("prcoitfiptvcxrvoalqmfpnqyhrubxspplrftomfehbbhefmotfrlppsxburhyqnpfmqlaorxcvtpiftiocrp"                      , 14);
		ans.put("kjowoemiduaaxasnqghxbxkiccikxbxhgqnsaxaaudimeowojk"		                                                 , -1);
		
		for( String palindrome : ans.keySet() ){
			Assert.assertEquals( ans.get(palindrome).intValue() , offByOnePalindrome( palindrome ) );
		}
		
	}
	
	public static void doublePalindrome(){
		
	}
	
	/**Given a word, will try to return the next lexicographically greater
	 * word made of the characters in word. */
	// O(n)
	public static String nextBiggerString(String word){
		if( word.length() == 1 ) return "no answer";
		
		// treat each character as a digit in a Base-26 numerical system.
		char[] digits = word.toCharArray();
		
		// special case of only descending digits; thus nothing bigger
		// do this in O(n) time here, instead of O(n^2) time the hard way below
		boolean noAnswer = true;
		int y = digits.length - 1;
		for( ; y > 0 ; y-- ){
			if ( digits[y-1] < digits[y]  ){
				noAnswer = false; // there will be an answer
				y--;
				break;
			}
		}
		if( noAnswer ) return "no answer";
		
		
		// start from the right side of the given 'number'. 
		for( int x = word.length()-1; x > 0; x -- ){
			// we know where the first 'valley' will be : ii
			// search ahead looking for the next smaller digit
			//for( int y = x -1; y >= 0 ; y -- ){
				if( digits[y] < digits[x] ){
					// found our first place we can swap in x.
					char cy = digits[y];
					digits[y] = digits[x];
					digits[x] = cy; // swap
					Arrays.sort( digits , y+1, word.length() );
					
					return new String(digits);
				}
			//}
		}
		
		return "no answer";
	}

	@Test
	public void testNextBiggerString(){
		Assert.assertEquals( "ba"  , nextBiggerString("ab")  );
		Assert.assertEquals( "no answer"  , nextBiggerString("bb")  );
		Assert.assertEquals( "hegf"  , nextBiggerString("hefg"));
		Assert.assertEquals( "hgjo"  , nextBiggerString("gojh"));
		Assert.assertEquals( "no answer"  , nextBiggerString("zyxwcba"));
	}

	
	
	
	
	
	/**Returns maximum length of a string, c, that is comprised of ordered letters
	 * in both a and b */
	public static int dualStringCommonChild(String a, String b){
		StringHisto sa = new StringHisto( a );
		StringHisto sb = new StringHisto( b );
		
		int max = 0;
		for( int ii=0, len=a.length();  ii < len ; ii++ ){
			max = Math.max( max ,  findMaxCommonChild(ii, sa , sb)) ;
		}
		
		return max;
	}
	
	public static int findMaxCommonChild(int startIdx, StringHisto left, StringHisto right ){
		int overlappingOrderedChars = 0;
		left.remap(); right.remap();
		
		StringBuilder sb = new StringBuilder();
		
		for( int ii = startIdx; ii < left.length(); ii ++ ){
			char lc = left.charAt( ii );
			// see if a character in a exists in b 
			if ( ! right.contains( lc ) ) 
				continue;
			
			int idx = right.earliestIdx( lc );
			// consume. remove everything to the left of idx from b (inclusive)
			right.consume(idx);
	
			sb.append( lc );
			overlappingOrderedChars ++ ;
			
			if ( right.isConsumed() || left.isConsumed() ) break;
		}
		
		return overlappingOrderedChars;
	}
	
	public static class StringHisto {
		public final String a;
		public final Map<Character, Set<Integer>> map;
		private int consumed = 0;
		StringHisto(String s){
			a = s;
			map = new LinkedHashMap<Character, Set<Integer>>((int)(s.length()*1.5));
			removed = new LinkedHashMap<Character, Set<Integer>>( (int)(s.length()) );
			_init();
		}
		// make histogram of indices of charcaters
		private void _init(){
			map.clear();
			// make histogram of indices of charcaters
			for( int ii = 0 ; ii < a.length(); ii++) {
				char ac  = a.charAt(ii);
				Set<Integer> aset = map.get( ac );
				if ( aset == null ){ 
					aset = new TreeSet<Integer>();
					map.put( ac , aset );
				}
				aset.add( ii );
			}
		}
		public final Map<Character, Set<Integer>> removed;
		
		public char charAt(int idx){ return a.charAt(idx) ; }
		public int length() { return a.length(); }
		public boolean contains(char c){ return map.containsKey(c); }
		public Set<Integer> get(char c){ return map.get(c); }
		public Set<Integer> remove(char c){ return map.remove(c); }
		public boolean remove(char c, int idx){
			Set<Integer> set = map.get(c);
			return (set==null) ? false : set.remove(idx);
		}
		public char[] toCharArray(){ return a.toCharArray(); }
		public int earliestIdx(char c){
			if ( ! map.containsKey(c) ) return -1;
			return ((TreeSet<Integer>)map.get(c)).first();
		}
		public void consume(int idx){
			for( int ii = consumed; ii <= idx; ii ++ ){
				char c = a.charAt( ii );
				Set<Integer> consume =  map.get( c );
				consume.remove( ii );
				if ( consume.isEmpty() )
					map.remove( c );
				if ( ! removed.containsKey( c ) )
					removed.put( c , new TreeSet<Integer>() );
				removed.get( c ).add( ii );
			}
			consumed = idx + 1;
		}
		public boolean isConsumed(){ return consumed == a.length(); }
		public synchronized void remap(){ 
			//_init();
			map.putAll( removed );
			removed.clear();
			consumed = 0;
		}
		public String toString(){ return a.substring(consumed); }
	}
	

	@Test
	public void testCommonChild(){
		System.out.println( dualStringCommonChildDynamic("HARRY", "SALLY"));
		System.out.println( dualStringCommonChildDynamic("AA", "BB"));
		System.out.println( dualStringCommonChildDynamic("SHINCHAN", "NOHARAAA" ));
		System.out.println( dualStringCommonChildDynamic("ABCDEF", "FBDAMN" ));
		
		System.out.println( dualStringCommonChild( // WRONG!
			"WEWOUCUIDGCGTRMEZEPXZFEJWISRSBBSYXAYDFEJJDLEBVHHKS" ,
			"FDAGCXGKCTKWNECHMRXZWMLRYUCOCZHJRRJBOAJOQJZZVUYXIC" ));  // should be 15
		
		System.out.println( dualStringCommonChildDynamic(
				"WEWOUCUIDGCGTRMEZEPXZFEJWISRSBBSYXAYDFEJJDLEBVHHKS" ,
				"FDAGCXGKCTKWNECHMRXZWMLRYUCOCZHJRRJBOAJOQJZZVUYXIC" ));  // should be 15
		System.out.println( dualStringCommonChildDynamic(
				"FDAGCXGKCTKWNECHMRXZWMLRYUCOCZHJRRJBOAJOQJZZVUYXIC" ,
				"WEWOUCUIDGCGTRMEZEPXZFEJWISRSBBSYXAYDFEJJDLEBVHHKS" 
				));  // should be 15
	}
	
	
	// correctly counts common child strings. 
	// O(n^2)
	public static int dualStringCommonChildDynamic(String a , String b){
		int len = a.length();
		
		int[][] subproblems = new int[len+1][len+1];
		for( int ii = 0; ii < len ; ii ++ ){
			subproblems[ii][0] = 0 ;  // degenerate cases
			subproblems[0][ii] = 0 ;
		}
		
		
		for( int ii = 1; ii <= len; ii ++ )  {
			char ac = a.charAt(ii - 1);
			for( int jj = 1; jj <= len; jj ++ ){
				char bc = b.charAt(jj-1);
				
				subproblems[ii][jj] = MAX( 
							(ac==bc ? 1 : 0 ) + subproblems[ii-1][jj-1] , 
							subproblems[ii][jj-1] ,
							subproblems[ii-1][jj] );
			}
		}
		
		return  subproblems[len][len] ;
	}
	
	
	
	public static void countRegex(){
		// need to make a Finite State Machine
	}
	
	public static enum FINITE_STATE_TYPE {
		START,
		END,
		REG,
		REG2,
		OR,
		STAR;
	}
	
	public static class Reg {
		public String r;
	}
	
	public static class RegEx {
		private FINITE_STATE_TYPE type = null;
		private Reg r = null; 
		private Reg r2 = null;
		
		int min_length; // minimum valid string length this state could accept 
		
		String nextValidChar(){
			switch( type ){
			case REG :
				return r.r;
				
			case REG2 :
				return r.r + r2.r;
						
			case OR : 
				break;
			case STAR : 
				break;
			default :
				break;
			}
			return null;
		}
	}

	
	
	
}




