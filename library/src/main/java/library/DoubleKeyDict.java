package library;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;


public interface DoubleKeyDict {

	/**
	 * adds a {@link Triple} to the {@link DoubleKeyDict}. Should only be called
	 * before a store operation Does not save the data persistently.
	 */
	public void add(String mainKey, String secondaryKey, String value);
	
	/**
	 * adds {@link Triple}s to the {@link DoubleKeyDict}. Should only be called
	 * before a store operation Does not save the data persistently
	 */

	/**
	 * Performs the persistent write using {@link FutureLineStorage}, and prevents
	 * further writes to the {@link DoubleKeyDict}
	 * @throws Exception if for some reason the write operation failed
	 */
	public void store();

	/**
	 * A search by the main key
	 * @param key
	 * @return mapping from all secondary keys which match the main
	 * key to the value the two keys represent.
	 */
	public CompletableFuture<Map<String,String>> findByMainKey(String key);

	/**
	 * a search by two keys
	 * @param mainKey
	 * @param secondaryKey
	 * @return the value saved by both keys, or {@link Optional#empty()} if
	 * no such combination of keys exists.
	 */
	public CompletableFuture<Optional<String>> findByKeys(String mainKey, String secondaryKey);

	/**
	 * A search by the secondary key
	 * @param key
	 * @return mapping from all main keys which match the secondary
	 * key to the value the two keys represent.
	 */
	public CompletableFuture<Map<String,String>> findBySecondaryKey(String key);
}
