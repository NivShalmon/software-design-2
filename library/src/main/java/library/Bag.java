package library;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface Bag<T> {
	
	public void add(T t);
	public void addAll(Collection<? extends T> ts);
	public void store();
	public CompletableFuture<Boolean> isIn(T t);
}
