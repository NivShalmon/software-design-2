package library;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface Bag<T> {
	
	public void add(T s);
	public void addAll(Collection<? super T> ss);
	public void store();
	public CompletableFuture<Boolean> isIn(T s);
}
