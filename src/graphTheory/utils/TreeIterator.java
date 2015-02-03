package graphTheory.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TreeIterator<T> implements Iterator<T>{
	
	protected TreeIterator(){
		
	}
	
	public static <T> TreeIterator<T> getTreeIterator(T e){
		return new SingleIterator<T>(e);
	}
	
	public static <T> TreeIterator<T> getTreeIterator(TreeIterator<T> first, TreeIterator<T> second){
		return new CompositeIterator(first, second);
	}
	
	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public T next() {
		return null;
	}

	@Override
	public void remove() {
	}
	
	public static void main(String[] args) {
		TreeIterator<Integer> t1 = TreeIterator.getTreeIterator(1);
		TreeIterator<Integer> t2 = TreeIterator.getTreeIterator(4);
		TreeIterator<Integer> t3 = TreeIterator.getTreeIterator(2);
		TreeIterator<Integer> t4 = TreeIterator.getTreeIterator(3);
		
		TreeIterator<Integer> t5 = TreeIterator.getTreeIterator(t1, t2);
		TreeIterator<Integer> t6 = TreeIterator.getTreeIterator(t5, t3);
		TreeIterator<Integer> t7 = TreeIterator.getTreeIterator(t6, t4);
		
		while(t7.hasNext())
			System.out.println(t7.next());
	}

}

class SingleIterator<T> extends TreeIterator<T>{
	
	private T element;
	private boolean hasNext;
	
	protected SingleIterator(T e) {
		element = e;
		hasNext = true;
	}
	
	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public T next() {
		if(hasNext){
			hasNext = false;
			return element;
		}
		else
			throw new NoSuchElementException();
	}

	@Override
	public void remove() {
	}
}

class CompositeIterator<T> extends TreeIterator<T>{
	private TreeIterator<T> first;
	private TreeIterator<T> second;
	
	protected CompositeIterator(TreeIterator<T> first, TreeIterator<T> second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public boolean hasNext() {
		return first.hasNext() || second.hasNext();
	}

	@Override
	public T next() {
		if(first.hasNext())
			return first.next();
		else
			return second.next();
	}

	@Override
	public void remove() {
	}
}
