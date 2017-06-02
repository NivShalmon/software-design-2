package library;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Implements a basic dictionary using a {@link LineStorage} and binary search.
 * Allows adding values until a {@link Dict#store()} is performed, after
 * which data is stored persistently.
 */
public interface Dict {

	/**
	 * Performs the persistent write using the {@link LineStorage}, and prevents further writes
	 * to the {@link Dict}
	 */
	public void store();

	public default void store(Collection<Pair> ps) {
		addAll(ps);
		store();
	}

	/**
	 * adds a pair to the Dict. Should only be called before a store operation
	 * Does not save the data persistently.
	 * 
	 * @param p
	 *            the {@link Pair} to be stored
	 */
	public void add(Pair p);

	/**
	 * adds pairs to the Dict. Should only be called before a store operation
	 * Does not save the data persistently.
	 * 
	 * @param ps
	 *            the {@link Pair}s to be stored
	 */
	public void addAll(Collection<Pair> ps);

	/**
	 * @param key
	 *            the key to be searched in the dictionary
	 * @return the value that matches key or {@link Optional.empty} otherwise.
	 * @throws InterruptedException
	 */
	public CompletableFuture<Optional<String>> find(String key);
}
