package library;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;


public interface DoubleKeyDict<K,T,V> {

	/**
	 * adds a {@link Triple} to the {@link DoubleKeyDict}. Should only be called
	 * before a store operation Does not save the data persistently.
	 */
	public void add(K mainKey, T secondaryKey, V value);
	
	/**
	 * adds {@link Triple}s to the {@link DoubleKeyDict}. Should only be called
	 * before a store operation Does not save the data persistently
	 */

	/**
	 * Performs the persistent write using {@link FutureLineStorage}, and prevents
	 * further writes to the {@link DoubleKeyDict}
	 */
	public void store();

	/**
	 * A search by the main key
	 * @param key
	 * @return mapping from all secondary keys which match the main
	 * key to the value the two keys represent.
	 */
	public CompletableFuture<Map<T,V>> findByMainKey(K key);

	/**
	 * a search by two keys
	 * @param mainKey
	 * @param secondaryKey
	 * @return the value saved by both keys, or {@link Optional#empty()} if
	 * no such combination of keys exists.
	 */
	public CompletableFuture<Optional<V>> findByKeys(K mainKey, T secondaryKey);

	/**
	 * A search by the secondary key
	 * @param key
	 * @return mapping from all main keys which match the secondary
	 * key to the value the two keys represent.
	 */
	public CompletableFuture<Map<K,V>> findBySecondaryKey(T key);
}
