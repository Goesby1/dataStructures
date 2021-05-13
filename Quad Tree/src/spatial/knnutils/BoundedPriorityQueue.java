package spatial.knnutils;

import java.util.Iterator;

import spatial.exceptions.UnimplementedMethodException;


/**
 * <p>{@link BoundedPriorityQueue} is a priority queue whose number of elements
 * is bounded. Insertions are such that if the queue's provided capacity is surpassed,
 * its length is not expanded, but rather the maximum priority element is ejected
 * (which could be the element just attempted to be enqueued).</p>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 * @author  <a href = "https://github.com/jasonfillipou/">Jason Filippou</a>
 *
 * @see PriorityQueue
 * @see PriorityQueueNode
 */
public class BoundedPriorityQueue<T> implements PriorityQueue<T> {

	/* *********************************************************************** */
	/* *************  PLACE YOUR PRIVATE FIELDS AND METHODS HERE: ************ */
	/* *********************************************************************** */
	private java.util.PriorityQueue<PriorityQueueNode<T>> que;
	private int sz; 
	private int insertionOrder;


	/* *********************************************************************** */
	/* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
	/* *********************************************************************** */

	/**
	 * Constructor that specifies the size of our queue.
	 * @param size The static size of the {@link BoundedPriorityQueue}. Has to be a positive integer.
	 * @throws IllegalArgumentException if size is not a strictly positive integer.
	 */
	public BoundedPriorityQueue(int size) throws IllegalArgumentException{
		//throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
		if (size < 1) {
			throw new IllegalArgumentException();
		}
		que = new java.util.PriorityQueue<PriorityQueueNode<T>>();
		this.sz = size;
		this.insertionOrder = 0;
	}

	/**
	 * <p>Enqueueing elements for BoundedPriorityQueues works a little bit differently from general case
	 * PriorityQueues. If the queue is not at capacity, the element is inserted at its
	 * appropriate location in the sequence. On the other hand, if the object is at capacity, the element is
	 * inserted in its appropriate spot in the sequence (if such a spot exists, based on its priority) and
	 * the maximum priority element is ejected from the structure.</p>
	 * 
	 * @param element The element to insert in the queue.
	 * @param priority The priority of the element to insert in the queue.
	 */
	@Override
	public void enqueue(T element, double priority) {
		//throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
		PriorityQueueNode<T> nd = new PriorityQueueNode<>(element, priority, insertionOrder);
		java.util.PriorityQueue<PriorityQueueNode<T>> nw = new  java.util.PriorityQueue<>(this.sz);
		PriorityQueueNode<T> temp;
		

	

		if (this.size() < this.sz) {
			this.insertionOrder++;
			this.que.add(nd);
		} else if (this.size() >= this.sz) {
			temp = (PriorityQueueNode<T>) this.last();
			this.insertionOrder++;
			if (temp.compareTo(nd) > 0) {
				while (this.size() > 1 ) {
					nw.add(this.que.poll());
				}
				this.que = nw;
				this.que.add(nd);
			}
		}

	}

	@Override
	public T dequeue() {
		//throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
		return this.que.poll().getData();

		//return this.que.poll().getData();
	}

	@Override
	public T first() {
		//throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
			if (this.que.peek() != null) {
				return this.que.peek().getData();
			}
		return null;
	}
	
	/**
	 * Returns the last element in the queue. Useful for cases where we want to 
	 * compare the priorities of a given quantity with the maximum priority of 
	 * our stored quantities. In a minheap-based implementation of any {@link PriorityQueue},
	 * this operation would scan O(n) nodes and O(nlogn) links. In an array-based implementation,
	 * it takes constant time.
	 * @return The maximum priority element in our queue, or null if the queue is empty.
	 */
	public T last() {
		//throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
		java.util.PriorityQueue<PriorityQueueNode<T>> nw = new  java.util.PriorityQueue<>(this.sz);
		PriorityQueueNode<T> nd;
		while (this.size() > 1 ) {
			nw.add(this.que.poll());
		}
		nd = this.que.poll();

		this.que = nw; 
		this.que.add(nd);
		return (T) nd;

	}

	/**
	 * Inspects whether a given element is in the queue. O(N) complexity.
	 * @param element The element to search for.
	 * @return {@code true} iff {@code element} is in {@code this}, {@code false} otherwise.
	 */
	public boolean contains(T element)
	{
		//throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
		java.util.PriorityQueue<PriorityQueueNode<T>> nw = new  java.util.PriorityQueue<>(this.sz);
		PriorityQueueNode<T> nd;
		boolean tr = false;
		while (this.size() > 0 ) {
			nd = this.que.poll();
			nw.add(nd);
			if (nd.getData().equals(element)) {
				tr = true;
			}

		}
		
		this.que = nw;
		return tr;
	}

	@Override
	public int size() {
		//throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
		return this.que.size();
	}

	@Override
	public boolean isEmpty() {
		//throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
		return this.que.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		//throw new UnimplementedMethodException(); // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
		java.util.PriorityQueue<PriorityQueueNode<T>> nw = new  java.util.PriorityQueue<>(this.sz);
		for (PriorityQueueNode<T> i: this.que) {

			nw.add(i);
		}
		
		return new it(nw);

	}

	public class it implements Iterator<T> {

		java.util.PriorityQueue<PriorityQueueNode<T>> nw;
		T elm;

		public it(java.util.PriorityQueue<PriorityQueueNode<T>> org) {
			nw = org;
			
		}

		public boolean hasNext() {
			return nw.size() > 0;
		}

		public T next() {

			if (hasNext()) {
				elm = nw.poll().getData();
			}

			return elm;
		}


	} 


}
