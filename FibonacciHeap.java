
import java.util.ArrayList;

import java.util.List;


/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
  */
public class FibonacciHeap
{

	  private static final double oneOverLogPhi =
		        1.0 / Math.log((1.0 + Math.sqrt(5.0)) / 2.0);

 public int n;

 public HeapNode min;
 //countLink counts how many link functions happened since the start of running the program
 public static int countLink;
//countLink counts how many cut functions happened since the start of running the program
 public static int countCut;
 
	
   /**
    * public boolean empty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean empty()
    {
    	 return min == null;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    */
    
    public HeapNode insert(int key)
    {   //lazy insertion
    	HeapNode node= new HeapNode(key);
        if (min != null) {
            node.prev = min;
            node.next = min.next;
            min.next = node;
            node.next.prev = node;
            if (key < min.key) 
                min = node; 
        }
        else min = node;
        n++;
    	return node;
    }

 
   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    
    public void deleteMin()
    {
    	 HeapNode temp = min;

         if (temp != null) {
             int numChildren = temp.rank;
            HeapNode x = temp.child;
             HeapNode tempRight;
             //iterate over each of temp's children 
             while (numChildren > 0) {
                 tempRight = x.next;
                 
                 // remove x from temp's children
                 x.prev.next = x.next;
                 x.next.prev = x.prev;

                 // add x to the root list 
                 x.prev = min;
                 x.next = min.next;
                 min.next = x;
                 x.next.prev = x;

                 // set x parent to null
                 x.parent = null;
                 x = tempRight;
                 //temp's number of children --
                 numChildren--;
             }

             // remove temp=min from root list
             temp.prev.next= temp.next;
             temp.next.prev = temp.prev;
             //update min
             if (temp == temp.next) {
                 min = null;
             } else {
                 min = temp.next;
                 //after removing the min consolidate
                 consolidate();
             }
             n--;
         }

    }
    
  
    /**
     * Make node y a child of node x.
     *
     * <p>Running time: O(1) actual</p>
     *
     * @param y node to become child
     * @param x node to become parent
     */
    private void link(HeapNode y, HeapNode x)
    {
        // remove y from root list 
        y.prev.next = y.next;
        y.next.prev = y.prev;
        y.parent = x;

        if (x.child == null) {
            x.child = y;
            y.next = y;
            y.prev = y;
        } else {
            y.prev = x.child;
            y.next = x.child.next;
            x.child.next = y;
            y.next.prev = y;
        }
        x.rank++;
        y.mark = 0;
        countLink++;
    }
    
    private void consolidate() {
    	 int listSize =
    	            ((int) Math.floor(Math.log(n) * oneOverLogPhi)) + 1;

    	        List<HeapNode> list =
    	            new ArrayList<HeapNode>(listSize);
    	        
    	        for (int i = 0; i < listSize; i++) {
    	            list.add(null);
    	        }

    	        // Find the number of roots in  heap
    	        int numRoots = 0;
    	        HeapNode x = min;

    	        if (x != null) {
    	            numRoots++;
    	            x = x.next;

    	            while (x != min) {
    	                numRoots++;
    	                x = x.next;
    	            }
    	        }
    	        //Iterate over all roots
    	        while (numRoots > 0) {
    	        	//Find rank of root
    	            int dg = x.rank;
    	            HeapNode next1 = x.next;

    	            // check if there is another root with the same rank
    	            while(true) {
    	                HeapNode y = list.get(dg);
    	                //Check if there is not
    	                if (y == null) {
    	                    break;
    	                }
    	                // There is so make one of the nodes a child of the other node
    	                if (x.key > y.key) {
    	                    HeapNode z = y;
    	                    y = x;
    	                    x = z;
    	                }
    	             
    	                link(y, x);
    	                // Handle the next rank
    	                list.set(dg, null);
    	                dg++;
    	            }

    	            //We might have next another node with the same 
    	            //rank therefore we save this one for possible future use
    	            list.set(dg, x);
    	            
    	            x = next1;
    	            numRoots--;
    	        }

    	        // rebuild the root list from list
    	        min = null;

    	        for (int i = 0; i < listSize; i++) {
    	            HeapNode y = list.get(i);
    	            if (y == null) {
    	                continue;
    	            }
    	            if (min!= null) {
    	                // Delete node from root list
    	                y.prev.next = y.next;
    	                y.next.prev = y.prev;

    	                // Insert node to root list
    	                y.prev = min;
    	                y.next = min.next;
    	                min.next = y;
    	                y.next.prev = y;

    	                // Check if we found a new min
    	                if (y.key < min.key) {
    	                    min = y;
    	                }
    	            } else {
    	                min = y;
    	            }
    	        }	
    }
   
   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    	return min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)
    {

         if (this != null && heap2 != null) {
           

             if (min != null) {
                 if (heap2.min != null) {
                     min.next.prev = heap2.min.prev;
                     heap2.min.prev.next = min.next;
                     min.next = heap2.min;
                     heap2.min.prev = min;

                     if (heap2.min.key < min.key) {
                         min = heap2.min;
                     }
                 }
             } else {
                 min = heap2.min;
             }

             n += heap2.n;
         }
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return n;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
    	//default automatically initializes arr to contain 0's
    	//amount of trees in heap <arr.length (according to the lecture)
    	if(n==0) 
    		return null;
    	if(min==null)
    		return null;
    	int[] arr=new int[(int)Math.ceil(1.5*((double)Math.log(Integer.MAX_VALUE)/Math.log(2)))];
    	HeapNode node1=min.next;
    	arr[min.rank]+=1;
    	while (node1!=min) {
    		arr[node1.rank]+=1;
    		node1=node1.next;
    	}
        return arr; 
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {   // make x the min 
        decreaseKey( x, Double.NEGATIVE_INFINITY );

        // remove the min
        deleteMin(  );
    }
        
        
           
    

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	int k=x.key-delta;
    	 if (k > x.key) {
             throw new IllegalArgumentException(
                 "invalid argument");
         }

         x.key = k;
         HeapNode y = x.parent;

         if (y != null && x.key < y.key) {
             cut(x, y);
             cascadingCut(y);
         }

         if (x.key < min.key) 
             min = x;
         
    }
    /**
     * public void decreaseKey(HeapNode x, double k)
     *
     * The function decreases the key of the node x to the new argument k . The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     ** Running time: O(1) amortized
     *@param x node to decrease the key of
     *@param k new key value for node x
     */
    public void decreaseKey( HeapNode x,double k ) {
        if( k > x.key ) {
            throw new IllegalArgumentException( 
                "invalid arguments" );
        }

        x.key = (int)k;
        HeapNode y = x.parent;

        if(  y != null  &&  x.key < y.key  ) {
            cut( x, y );
            cascadingCut( y );
        }

        if( x.key < min.key ) 
            min = x;
    }


    /**
     * removes x from the child list of y. 
     * Running time: O(1)
     * @param x child of y to be removed from y's child list
     * @param y parent of x 
     */
    private void cut(HeapNode x, HeapNode y)
    {
        // remove y's child x from it's children list
        x.prev.next = x.next;
        x.next.prev = x.prev;
        y.rank--;
        
        if (y.child == x) {
            y.child = x.next;
        }

        if (y.rank == 0) {
            y.child = null;
        }

        // insert x to root list 
        x.prev= min;
        x.next = min.next;
        min.next = x;
        x.next.prev = x;
        //x is a root so set it to be parent-less
        x.parent = null;
        x.mark = 0;
        countCut++;
    }
    /**
     * Running time: O(1\log n);
     * @param y node to perform cascading cuts on
     */
    private void cascadingCut(HeapNode y)
    {
       HeapNode parent_y = y.parent;
        if (parent_y != null) {
            if (y.mark==0) {
                y.mark = 1;
            } else {
                // y is marked so cut it from parent_y
                cut(y, parent_y);
                // cascading cut the parent_y
                cascadingCut(parent_y);
            }
        }
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {   
    	if(min==null)
		 return 0;
    	int cMarked=0;
    	int cTrees=numTrees();
    	cMarked+=countMarked(min);
    	HeapNode node= min.next;
    	while(node!=min) {
    		cMarked+=countMarked(node);
    		node=node.next;
    	}
    	return cTrees+2*cMarked;
    }

    
    //this function gets as a parameter the root of a tree 
    //returns how many marked nodes are in the tree
    //recursive function, runs over all nodes in tree so worst case scenario complexity is O(n) 
    private int countMarked(HeapNode root) {
    	int countMarked=0;
    	if(root==null)
    		return 0;
    	if(root.mark==1)
    		countMarked++;
    	if(root.child==null)
    		return countMarked;
    	countMarked+=countMarked(root.child);
    	//if root.child doesn't have siblings then root.child.next=root.child
    	HeapNode node= root.child.next;
    	while(node!=root.child) {
    		countMarked+=countMarked(node);
    		node=node.next;
    	}
    	return countMarked;
    }
    
    //this function returns number of trees in heap
    //number of trees in the worst case is O(n)
    private int numTrees() {
    	if(min==null)
    		return 0;
    	//already c=1 because we count the tree that first is it's root
    	int c=1;
    	HeapNode temp= min.next;
    	//if first has no siblings then first.next=first
    	//run over all the roots of the heap(first's siblings)
    	while(temp!=min) {
    		c++;
    		temp=temp.next;
    	}
    	return c;
    	
    }
    
   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    //O(1)
    public static int totalLinks()
    {    
    	return countLink;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return countCut;
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{

	public int key;
	public String info;
	public int rank;
	public int mark;
	public HeapNode child;
	public HeapNode next;
	public HeapNode prev;
	public HeapNode parent;
	//constructor sets parent and child to be null and all other pointers to point at him
  	public HeapNode(int key) {
	    this.key = key;
	    this.next=this;
	    this.prev=this;
      }
  	

  	public int getKey() {
	    return this.key;
      }

    }
}
  

