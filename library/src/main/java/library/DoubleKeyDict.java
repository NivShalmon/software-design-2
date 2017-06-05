package library;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


public interface DoubleKeyDict<K extends Comparable<K>,T extends Comparable<T>,V> {

	/**
	 * adds a {@link Triple} to the {@link DoubleKeyDict}. Should only be called
	 * before a store operation Does not save the data persistently.
	 * 
	 * @param t
	 *            the {@link Triple} to be stored
	 */
	public void add(Triple<K,T,V> t);

	public default void add(K key1, T key2, V value){
		add(new Triple<K,T,V>(key1,key2,value));
	}
	
	/**
	 * adds {@link Triple}s to the {@link DoubleKeyDict}. Should only be called
	 * before a store operation Does not save the data persistently.
	 * 
	 * @param ts
	 *            the {@link Triple}s to be stored
	 */
	public void addAll(Collection<Triple<K,T,V>> ts);

	/**
	 * Performs an {@link DoubleKeyDict#addAll(Collection)} operation, followed
	 * by a {@link DoubleKeyDict#store()}.
	 * 
	 * @param triples
	 *            the {@link Triple}s to be added
	 */
	public default void addAndStore(Collection<Triple<K,T,V>> ts) {
		addAll(ts);
		store();
	}

	/**
	 * Performs the persistent write using the {@link LineStorage}, and prevents
	 * further writes to the {@link DoubleKeyDict}
	 */
	public void store();

	public CompletableFuture<List<Pair<T,V>>> findByMainKey(K key);

	public CompletableFuture<Optional<V>> findByKeys(K key1, T key2);

	public CompletableFuture<List<Pair<K,V>>> findBySecondaryKey(T key);
}
