package library;

import java.util.Collection;

public interface Bag<T> {
	
	public void add(T s);
	public void addAll(Collection<? super T> ss);
	public void store();
	public default void store(Collection<? super T> ss){
		addAll(ss);
		store();
	}
	public boolean isIn(T s);
}
