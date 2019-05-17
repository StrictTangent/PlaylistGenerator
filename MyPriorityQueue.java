//Custom priority queue
//Anna T  

import java.util.ArrayList;

public class MyPriorityQueue<E extends Comparable<E>> implements MinPriorityQueue<E> {   
	private ArrayList<E> heap;
 
	public HeapMinPriorityQueue() {  
		heap = new ArrayList<E>();
	} 

   //removes obejct with greatest priority 
	public E remove() {
		if (heap.size() <= 0)
			return null;
		else {  
			E min = heap.get(0);
			heap.set(0, heap.get(heap.size()-1));  // Move last to hole 
			heap.remove(heap.size()-1);
			percolateDown(heap, 0);      
			return min; 
		}  
	} 

	//insert into array 
  	public void insert(E e) {
		heap.add(e);       //append e
		int position = heap.size()-1;  

		// percolateUp 
		while (position > 0 && heap.get(position).compareTo(heap.get(parent(position))) < 0) {
			swap(heap, position, parent(position));    
			position = parent(position);    
		}
	} 
   
   //checks if pqueue is empty 
	public boolean isEmpty() {
		return heap.size() == 0;
	}


	//returns root 
  	public E getMin() {
		if (heap.size() <= 0)
			return null;
		else
			return heap.get(0);     
	}

	//percolateDown to restore heap order property 
  	private static <E extends Comparable<E>> void percolateDown(ArrayList<E> a, int i) {
		int left = leftChild(i);    
		int right = rightChild(i);  
		int smallest;    //smallest value out of left child and right child 

		//check left child 
		if (left <= a.size()-1 && a.get(left).compareTo(a.get(i)) < 0){
			smallest = left; 
		} else {
			smallest = i;   
      }  

		//check right child and set right child to smallest if less than left and parent 
		if (right <= a.size()-1 && a.get(right).compareTo(a.get(smallest)) < 0) {
			smallest = right;   
      }

		if (smallest != i) {
			swap(a, i, smallest);
			percolateDown(a, smallest);    
		} 
	} 

	// Swap two locations i and j
	private static <E> void swap(ArrayList<E> a, int i, int j) {
		E t = a.get(i); 
		a.set(i, a.get(j));
		a.set(j, t);   
	}

	// Return the index of the left child of node i
	private static int leftChild(int i) {
		return 2*i + 1;
	}

	// Return the index of the right child of node i
	private static int rightChild(int i) {
		return 2*i + 2;  
	}

	// Return the index of the parent of node i
	// (Parent of root will be -1)
	private static int parent(int i) {
		return (i-1)/2;   
	} 
} 