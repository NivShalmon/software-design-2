package library;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

/**
 * Implements a basic dictionary using a {@link FutureLineStorage} and binary search.
 * Allows adding values until a {@link Dict#store()} is performed, after
 * which data is stored persistently and additional adds will not affect the storage.
 * Since only {@link FutureLineStorage} can be used to store data persistently, nothing
 * stops you from calling store twice. However, that would corrupt the Dict, so don't
 * do that.
 */
public interface Dict {

	/**
	 * Performs the persistent write using the {@link LineStorage}, and prevents further writes
	 * to the {@link Dict}
	 * Don't call this more than once, since that might corrupt the Dict. 
	 * @return a {@link CompletableFuture} that shows whether the store
	 * was completed.
	 * User must make sure to wait on this before calling find() from
	 * a different instance of the same Dict (i.e same name)
	 */
	public CompletableFuture<Void> store();

	/**
	 * adds a pair to the Dict. Should only be called before a store operation
	 * Does not save the data persistently.
	 */
	public void add(String key,String value);
	
	/**
	 * adds pairs to the Dict. Should only be called before a store operation
	 * Does not save the data persistently.
	 */
	public void addAll(Map<String,String> m);

	/**
	 * @param key
	 *            the key to be searched in the dictionary
	 * @return the value that matches key or {@link Optional.empty} otherwise.
	 */
	public CompletableFuture<Optional<String>> find(String key);
}