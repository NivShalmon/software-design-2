package library;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import com.google.inject.Inject;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

/**
 * Implements a basic dictionary using a {@link LineStorage} and binary search.
 * Allows adding values until a {@link Dict#store()} is performed, after which
 * data is stored persistently.
 */
public class Dict<K, V> {
	private	final CompletableFuture<FutureLineStorage> s; 
	private FutureLineStorage storer;
	private Map<K, V> pairs = new HashMap<>();
	private Function<K, String> keySerializer;
	private Function<V, String> valueSerializer;
	private Function<String, V> valueParser;

	@Inject
	Dict(CompletableFuture<FutureLineStorage> s, Function<K, String> keySerializer, Function<V, String> valueSerializer,
			Function<String, V> valueParser) {
		this.s = s;
		this.keySerializer = keySerializer;
		this.valueSerializer = valueSerializer;
		this.valueParser = valueParser;
	}

	/**
	 * Performs the persistent write using the {@link LineStorage}, and prevents further writes
	 * to the {@link Dict}
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void store() throws InterruptedException, ExecutionException {
		storer = s.get();
		pairs.keySet().stream().sorted().forEachOrdered(key -> {
			storer.appendLine(keySerializer.apply(key));
			storer.appendLine(valueSerializer.apply(pairs.get(key)));
		});
	}

	/**
	 * adds a pair to the Dict. Should only be called before a store operation
	 * Does not save the data persistently.
	 */
	public void add(K key, V value) {
		pairs.put(key, value);
	}

	/**
	 * adds pairs to the Dict. Should only be called before a store operation
	 * Does not save the data persistently.
	 */
	public void addAll(Map<K, V> ps) {
		pairs.putAll(ps);
	}

	/**
	 * @param key
	 *            the key to be searched in the dictionary
	 * @return the value that matches key or {@link Optional.empty} otherwise.
	 * @throws InterruptedException
	 */
	public CompletableFuture<Optional<V>> find(K key) {
		return BinarySearch.valueOf(storer, keySerializer.apply(key), 0, storer.numberOfLines())//
				.thenApply(o -> o.map(valueParser));
	}
}
