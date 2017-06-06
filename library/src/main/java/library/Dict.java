package library;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Implements a basic dictionary using a {@link LineStorage} and binary search.
 * Allows adding values until a {@link Dict#store()} is performed, after
 * which data is stored persistently.
 */
public interface Dict<K,V> {

	/**
	 * Performs the persistent write using the {@link LineStorage}, and prevents further writes
	 * to the {@link Dict}
	 */
	public void store();

	/**
	 * adds a pair to the Dict. Should only be called before a store operation
	 * Does not save the data persistently.
	 */
	public void add(K key,V value);
	
	/**
	 * adds pairs to the Dict. Should only be called before a store operation
	 * Does not save the data persistently.
	 */
	public void addAll(Map<K,V> m);

	/**
	 * @param key
	 *            the key to be searched in the dictionary
	 * @return the value that matches key or {@link Optional.empty} otherwise.
	 * @throws InterruptedException
	 */
	public CompletableFuture<Optional<V>> find(K key);
}
