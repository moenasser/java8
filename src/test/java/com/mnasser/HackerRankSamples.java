package com.mnasser;

import static com.mnasser.DynamicProgrammingExamples.MAX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

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

	
	
	
	
	/**
	 * A node in a graph or tree.
	 * </p>
	 * Can have children and or a parent.
	 *
	 * @param <T> The type of element each node is wrapping. 
	 * 		Acts as unique identifier and equality measure
	 */
    public static class Node<T> {
        private List<Node<T>> edges = null; // our children - not needed here, force of habit
        final T data; // our unique name/identifier. (parameterized)
        private Node<T> parent = null; // all children have a parent. (Root does not)
        public Node(T data){ this.data = data; }
        private int depth = 0; // cheat - to save on our recursion later
        
        /**
         * Adds a child node beneath this parent.
         * @param child
         */
        public void addChild(Node<T> child){
            if ( edges == null ) edges = new ArrayList<Node<T>>();
            edges.add( child );
            child.parent = this;
        }
        /**
         * Returns true if this node has children and this child is one of them
         * @param child Child to check maternity for
         * @return true if this is our daughter or not.
         */
        public boolean hasChild(Node<T> child){
        	return (edges==null) ? false : edges.contains( child ); //O(n)
        }
        /**
         * Returns true if this node is strictly an ancestor of child.
         * Meaning you are not the ancestor of yourself.
         * 
         * @param child The child to check if we are the mother, grandmother, greatgrandmother ...etc .. of.
         * @return True if we are at least the parent of this child. False otherwise.
         */
        public boolean isAncestorOf(Node<T> child){
        	if( child == null || child.parent == null ) return false;
        	return (child.parent == this) || isAncestorOf(child.parent);
        }
        
        public int hashCode(){ return data.hashCode(); }
        public boolean equals(Object o){
            if ( o == null ) return false;
            if ( this == o ) return true;
            @SuppressWarnings({ "rawtypes" })
			Node no = (Node)o;
            return this.data.equals(no.data);
        }
        
        public String toString(){
        	//return data + ((edges==null)? "" : (Arrays.toString(edges.toArray())) );
		    StringBuilder sb = new StringBuilder();
		    _toString( sb, 0 );
		    return sb.toString();
        }
        private void _toString(StringBuilder sb, int depth){
            String tabs = "";
            for( int i = 0; i < depth; i ++ ){
                tabs = tabs + '\t' ;
            }
            sb.append( tabs );
            sb.append( data );
            if ( edges == null ) return;
            sb.append(" -> \n");
            for( Node<T> child : edges ){
                child._toString( sb , depth + 1 );
                sb.append( "\n" );
            }
        }
    }
    
    
    public static <T> void rankTree(Node<T> root){
    	rankTree( root,  0 );
    }
    private static <T> void rankTree(Node<T> parent, int depth){
    	parent.depth = depth;
    	if ( parent.edges != null )
    		for( Node<T> n : parent.edges )
    			rankTree( n , depth + 1 );
    }
	
    /**Maximum number of children beneath this node*/
	public static <T> int recurseDS(Node<T> n , int depth){
		if ( n == null ) return depth - 1;
        if ( n.edges == null || n.edges.size() == 0 ) return depth;
        int max = 0; 
        for ( Node<T> child : n.edges ){
            max = Math.max( max , recurseDS(child , depth + 1 ) );
        }
		return max;
	}
	
	/**Gets the root of a collection of nodes. The root being the one node w/ no parent*/
    public static <T> Node<T> getRoot( Collection<Node<T>> nodes ){
        List<Node<T>> roots = nodes.stream().filter( n -> n.parent == null ).collect(Collectors.toList());
        if ( roots.size() != 1 ) // problem?
        	throw new RuntimeException("This tree has MULITPLE ROOTS!");
        return roots.get(0);
    }
    
    
    @Test
    public void testAMPLIFYquestion(){
    	Map<String,Node<String>> tree = new HashMap<String, Node<String>>(0); 
    	
    	String[] names = "Clare, Gloria, Hazel".split("\\,\\s*");
    	System.out.println(Arrays.toString(names));
    	addToTree( tree, names );
    	
    	names = "Ann, Betty, Clare".split("\\,\\s*");
    	System.out.println(Arrays.toString(names));
    	addToTree( tree, names );
    	
    	
    	names = "Betty, Donna, Elizabeth, Flora".split("\\,\\s*");
    	System.out.println(Arrays.toString(names));
    	addToTree( tree, names );
    	

    	names = "Hazel, Ingrid".split("\\,\\s*");
    	System.out.println(Arrays.toString(names));
    	addToTree( tree, names );
    	
    	names = "Ingrid, Jezebel".split("\\,\\s*");
    	System.out.println(Arrays.toString(names));
    	addToTree( tree, names );
    	
    	names = "Donna, Karen".split("\\,\\s*");
    	System.out.println(Arrays.toString(names));
    	addToTree( tree, names );
    	
    	//System.out.println( tree );
    	
    	Node<String> root = getRoot( tree.values() );
    	rankTree( root );
    	
    	//System.out.println(root);
    	
    	Assert.assertEquals( "Clare" , findAntecedant(tree, "Hazel", "Gloria").data);
    	Assert.assertEquals( "Clare" , findAntecedant(tree, "Hazel", "Clare").data);
    	Assert.assertEquals( "Ann" , findAntecedant(tree, "Hazel", "Flora").data);
    	Assert.assertEquals( "Ann" , findAntecedant(tree, "Hazel", "Betty").data);
    	Assert.assertEquals( "Ann" , findAntecedant(tree, "Hazel", "Ann").data);
    	Assert.assertEquals( "Hazel" , findAntecedant(tree, "Hazel", "Hazel").data);
    	Assert.assertEquals( "Clare" , findAntecedant(tree, "Clare", "Jezebel").data);
    	Assert.assertEquals( "Ann" , findAntecedant(tree, "Karen", "Jezebel").data);
    	//Node<String> child1 = "Hazel";
    	//Node<String> child2 = "Gloria";
    }
    
    /** Convenience method to help build above tree */
    private static void addToTree(Map<String,Node<String>> tree, String[] names){
		Node<String> mom = tree.get( names[0] );
    	mom = (mom==null) ? new Node<String>(names[0]) : mom;
    	tree.put( mom.data , mom );
    	
    	for( int ii = 1; ii < names.length; ii ++){
    		Node<String> child = tree.get( names[ii] );
    		child = (child==null)? new Node<String>(names[ii]) : child;
    		mom.addChild( child );
    		tree.put( child.data , child );
    	}
        
    }
    
    
    public static <T> Node<T> findAntecedant(Map<T, Node<T>> tree, String c1, String c2 ){
    	Node<T> child1 = tree.get( c1 );
    	Node<T> child2 = tree.get( c2 );
    	return findAntecedant(child1, child2);
    }
    /**
     * Recursively looks up the family tree looking for closest ancestor. 
     * Ancestor could be either child if one is 
     * @param child1
     * @param child2
     * @return
     */
    public static <T> Node<T> findAntecedant(Node<T> child1, Node<T> child2){
    	// me myself I
    	if ( child1 == child2 ){
    		return child1;
    	}
    	// same level ? check if siblings
    	if ( child1.depth == child2.depth && child2.parent == child1.parent ){
    		return child1.parent;
    	}
    	// root?
    	if ( child1.depth == 0 ) return child1;
    	if ( child2.depth == 0 ) return child2;
    	
		//	// parents
		//	if ( child1.parent == child2 ){
		//		return child2;
		//	}
		//	if ( child2.parent == child1 ){
		//		return child1;
		//	}
		//	// grandparents 
		//	if ( child1.parent != null && child2.hasChild(child1.parent) ){
		//		return child2;
		//	}
		//	if ( child2.parent != null && child1.hasChild(child2.parent) ){
		//		return child1;
		//	}
    	
    	// higher up? check direct ancestry
    	if ( child1.depth < child2.depth   &&   child1.isAncestorOf(child2) ) return child1;
    	if ( child2.depth < child1.depth   &&   child2.isAncestorOf(child1) ) return child2;
    	
    	// totally different branches of the family. recurse
    	Node<T> parent1 = ( child1.parent == null ) ? child1 : child1.parent;
    	Node<T> parent2 = ( child2.parent == null ) ? child2 : child2.parent;
    	return findAntecedant(parent1, parent2);
    }
    
    
    /***
    public static void main(String[] args) {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
            
            String[] nm = br.readLine().split("\\s+");
            int N = Integer.parseInt( nm[0] ); // kinda worthless?
            int M = Integer.parseInt( nm[1] ); 
            
            // build our tree
            Map<Integer,Node> tree = new LinkedHashMap<Integer,Node>(); // kek
            for ( int m = 0; m < M; m++ ){
                String[] line = br.readLine().split("\\s+");
                int _id1 = Integer.parseInt( line[0] );
                int _id2 = Integer.parseInt( line[1] ); 
                int id1 = Math.min( _id1, _id2 ); // enforce children having smaller IDs
                int id2 = Math.max( _id1, _id2 ); // else it's free fall what is child/parent
                
                Node n1 = tree.get( id1 ); 
                if ( n1 == null ){
                    n1 = new Node( id1 );
                    tree.put( id1, n1 );
                }
                Node n2 = tree.get( id2 ); 
                if ( n2 == null ){
                    n2 = new Node( id2 );
                    tree.put( id2, n2 );
                }
                
                n1.addNode( n2 );
            }
            
            //System.out.println( tree.values() );
            
     ******/
    
    private void addToTree(Map<Integer,Node<Integer>> tree, int _id1 , int _id2){
        int id1 = Math.min( _id1, _id2 ); // enforce children having smaller IDs
        int id2 = Math.max( _id1, _id2 ); // else it's free fall what is child/parent
        
        Node<Integer> n1 = tree.get( id1 ); 
        if ( n1 == null ){
            n1 = new Node<Integer>( id1 );
            tree.put( id1, n1 );
        }
        Node<Integer> n2 = tree.get( id2 ); 
        if ( n2 == null ){
            n2 = new Node<Integer>( id2 );
            tree.put( id2, n2 );
        }
        n1.addChild( n2 );
    }
    
    public static <T> int getMinEvenTreeCuts(Map<Integer,Node<T>> tree){
            // find out how many children are in each branch beneath root
            Node<T> root = tree.get( 1 ); // wasn't defined if this is the root or not
            root = ( root.parent == null ) ? root : getRoot( tree.values() );
            
            //System.out.println( root );
            
            int cuts = 0, odds = 0;
            /*****
            for( Node child : root.edges ){
                int children = recurseDS( child , 1 );
                if ( children % 2 == 0 )
                    cuts++; // go ahaed and cut this sub tree
                else {
                    odds++; // if we have multiple odds we have a problem
                }
            }
            ****/
            
            for( Node<T> child : root.edges){
                //int children = 
                Tuple<Integer,Integer> res = pruneEven( child , 0 );
                cuts += res.cuts;
                
                if ( res.children % 2 == 0 )
                    cuts ++;
                else {
                    odds ++ ;
                }
            }
            
            if ( odds % 2 == 1 )  { // no porblem ...
                System.out.println( cuts ); 
                return cuts;
            }
            
            // EVEN number of odds!??  No good - the root links them but can only link and ODD number of odds
            throw new RuntimeException("This graph has an EVEN number of ");
            
        //}catch(Exception e){ e.printStackTrace(); }
    }
    
    private static class Tuple<L,R> {
        final L children;
        final R cuts;
        Tuple(L l, R r){
            children = l;
            cuts = r;
        }
        public static <L,R> Tuple<L,R> lr(L l, R r){
            return new Tuple<L,R>(l,r);
        }
        public String toString(){
            return children + " , " + cuts;
        }
    }
    
    // given a NON-root node, see if we can easily prune even sub-trees
    private static <T> Tuple<Integer,Integer>  pruneEven(Node<T> n, int cuts){
        if ( n == null ) return Tuple.lr( 0 , cuts );
        if ( n.edges == null || n.edges.size()==0 ) return Tuple.lr( 1 , cuts );
        int finalChildren = 0;
        List<Node<T>> toRemove = new ArrayList<Node<T>>();
        for ( Node<T> child : n.edges ) {
            
            Tuple<Integer, Integer> res = pruneEven( child , cuts );
            cuts = res.cuts;
            
            if ( res.children > 0 && res.children % 2 == 0 ){
                cuts ++; // we can prune number of children so far
                //System.out.println("Prunning "  + child);
                toRemove.add( child );
            }
            else{
                finalChildren += res.children;
            }
        }
        for( Node<T> remove : toRemove ) { // not necessary?
            n.edges.remove( remove );
            remove.parent = null ;
        }
        Tuple<Integer,Integer> res = Tuple.lr( finalChildren + 1 ,  cuts );
        //System.out.println( n.data + " final subtree size & cuts so far  = " + res);
        //return finalChildren + 1; // this part of the tree's total child count after pruning
        return res;
    }


	
	
	@Test
	public void testEvenTree(){
		Map<Integer,Node<Integer>> tree = new HashMap<Integer,Node<Integer>>();
		addToTree(tree, 1, 2);
		addToTree(tree, 3 , 1 );
		addToTree(tree, 4 , 3 );
		addToTree(tree, 5 , 2 );
		addToTree(tree, 6 , 1 );
		addToTree(tree, 7 , 2 );
		addToTree(tree, 8 , 6 );
		addToTree(tree, 9 , 8 );
		addToTree(tree, 10, 8 );
		
		int cuts = getMinEvenTreeCuts( tree );
		Assert.assertEquals( 2 ,  cuts );
		
	}
	
	@Test
	public void testEvenBiggerTree(){
		Map<Integer,Node<Integer>> tree = new HashMap<Integer,Node<Integer>>();
		addToTree(tree, 2  , 1);
		addToTree(tree, 3  , 1);
		addToTree(tree, 4  , 3);
		addToTree(tree, 5  , 2);
		addToTree(tree, 6  , 5);
		addToTree(tree, 7  , 1);
		addToTree(tree, 8  , 1);
		addToTree(tree, 9  , 2);
		addToTree(tree, 10 ,  7);
        addToTree(tree, 11 ,  10 );
		addToTree(tree, 12 ,  3  );
		addToTree(tree, 13 ,  7  );
		addToTree(tree, 14 ,  8  );
		addToTree(tree, 15 ,  12 );
		addToTree(tree, 16 ,  6  );
		addToTree(tree, 17 ,  6  );
		addToTree(tree, 18 ,  10 );
		addToTree(tree, 19 ,  1  );
		addToTree(tree, 20 ,  8  );
		
		int cuts = getMinEvenTreeCuts( tree );
		Assert.assertEquals( 4 ,  cuts );
		
	}
	
	
	
	
	
	
    private static class ClusterBuilder{
        // connected components
        private Map<Integer , Set<Integer>> cc = new HashMap<Integer,Set<Integer>>();
        private List<Set<Integer>> clusters = new ArrayList<Set<Integer>>();
        private int singletons = 0;
        
        public void addSingleton( int single ){
        	if ( ! cc.containsKey(single) ){
        		singletons++;
        	}
        }
        public void inferSingletons( int maxIndividuals ){
        	int clustered_members = cc.keySet().size(); // how many in clusters
        	int single = maxIndividuals - clustered_members;// rest are singletons
        	
        	singletons = single; // we don't care about those other singletons
        }

        
        public void add(int a, int b){
            Set<Integer> s = cc.get( a );
            if( s == null ) s = cc.get( b );
            else{
            	Set<Integer> s2 = cc.get( b );
            	if ( s2 != null && s2 != s ){
            		s.addAll( s2 );
            		clusters.remove( s2 );
            		s2 = null;
            	}
            }
            if( s == null ){
            	s =  new HashSet<Integer>();
            	clusters.add( s ); // unique set of clusters
            }
            
            s.add( a );  s.add( b );
            for( int x : s ){
            	cc.put( x , s ); // re-map everyone
            }
        }
        
        public int countClusters(){
            return clusters.size();
        }
        
        public void clear(){
        	cc.clear();
        	clusters.clear();
        	singletons = 0;
        }
        
		//    public Collection<Set<Integer>> getClusters(){
		//        return clusters;
		//    }
        
        public int[] getClusterSizes(){
            int[] sizes = new int[countClusters()];
            int ii = 0;
            for ( int size : clusters.stream().map( x -> x.size() ).collect(Collectors.toList()) ){
            	sizes[ii++] = size;
            }
            return sizes;
        }
        
        public long calcMyMax2pairCombos(){
        	return calcMax2pairCombos( getClusterSizes() , singletons);
        }
        public static long calcMax2pairCombos(int[] sizes){
        	return calcMax2pairCombos(sizes, 0);
        }
        public static long calcMax2pairCombos(int[] sizes, int singletons){
        	long sum = (sizes.length==0) ? 0 : sizes[0];
        	// add up all group pairings
        	long res = 0;
        	for( int ii = 0; ii < sizes.length - 1; ii ++ ){
        		sum += sizes[ii + 1];
        		for( int jj = ii+1; jj < sizes.length; jj++){
        			res += sizes[ii] * sizes[jj];
        		}
        	}
        	
        	// for the singleton groups, they effectively add copies of the running sum
        	res += (singletons < 0) ? 0 : sum * singletons;
        	
        	// the singletons amongts themselves make a Summation pattern
        	// 1, 1, 1, 1, 1,  =  4 + 3 + 2 + 1 = sum( n-1 ==> 1 );
        	for( int s = singletons - 1; s > 0; s-- ){
        		res += s;
        	}
        	
            return res;
        }
    }
    
	@Test
	public void testClusterBuilder(){
		int[] foo = new int[]{ 1, 2 , 3, 4 };
		Assert.assertEquals( 35,  ClusterBuilder.calcMax2pairCombos(foo, 0) );
		
		foo = new int[]{ 2 , 3, 4, 5 };
		Assert.assertEquals( 71 , ClusterBuilder.calcMax2pairCombos(foo) );
		
		ClusterBuilder cb = new ClusterBuilder();
		cb.add( 0, 1);
		cb.add( 2, 3);
		
		Assert.assertEquals( 4, cb.calcMyMax2pairCombos() );  // 2 * 2
		
		cb.add( 4, 3);
		Assert.assertEquals( 6, cb.calcMyMax2pairCombos() );  // 2 * 3 
		
		cb.add( 5, 1);
		Assert.assertEquals( 9, cb.calcMyMax2pairCombos() );  // 3 * 3
		
		cb.add( 6, 7); 
		Assert.assertEquals( 21, cb.calcMyMax2pairCombos() );  // {3, 3, 2} = 3*3  + 3*2 + 3*2
	
		
		cb.clear();
		
		cb.add( 0 , 2 );
		cb.add( 1 , 8 );
		cb.add( 1 , 4 );
		cb.add( 2 , 8 );
		cb.add( 2 , 6 );
		cb.add( 3 , 5 );
		cb.add( 6 , 9 );
		// (0,1,2,8,4,6,9) (3,5)
		Assert.assertEquals( 14, cb.calcMyMax2pairCombos() );  // {7, 2} = 7*2

		cb.addSingleton( 7 ); // (0,1,2,8,4,6,9) (3,5)  (7)
		Assert.assertEquals( 23, cb.calcMyMax2pairCombos() );  // {7, 2, 1} = 7*2 + 7*1 + 2*1 
		
		
		cb.clear();
		cb.add( 1, 2 );
		cb.add( 3, 4 );
		cb.inferSingletons( 100_000 );
		
		Assert.assertEquals( 4999949998L, cb.calcMyMax2pairCombos() );  // {7, 2, 1} = 7*2 + 7*1 + 2*1 
		
	}
	
	
	public static int[] move_column(int[] table, int from, int to){
		if ( table.length - 1  < from) throw new IndexOutOfBoundsException(from+" > "+(table.length-1));
		if ( from == to  ||  table[from] == table[to] ) 
			return table;
		table[from] ^= table[to];
		table[to] ^= table[from];
		table[from] ^= table[to];
		return table;
	}
	
	@Test
	public void testXOR(){
		System.out.println( Arrays.toString( move_column(new int[]{1, 2, 3, 4} , 3 ,1) ));
	}
	
	
}




