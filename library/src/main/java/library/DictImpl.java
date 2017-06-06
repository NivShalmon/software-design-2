package library;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.google.inject.Inject;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

/**
 * Implements a basic dictionary using a {@link LineStorage} and binary search.
 * Allows adding values until a {@link Dict#store()} is performed, after which
 * data is stored persistently.
 */
public class DictImpl<K, V> implements Dict<K, V> {
	private final FutureLineStorage storer;
	private boolean initialized = false;
	private Map<K, V> pairs = new HashMap<>();
	private Function<K, String> keySerializer;
	private Function<V, String> valueSerializer;
	private Function<String, V> valueParser;

	@Inject
	DictImpl(FutureLineStorage storer, Function<K, String> keySerializer, Function<V, String> valueSerializer,
			Function<String, V> valueParser) {
		this.storer = storer;
		this.keySerializer = keySerializer;
		this.valueSerializer = valueSerializer;
		this.valueParser = valueParser;
	}

	/**
	 * Performs the persistent write using the {@link LineStorage}, and prevents
	 * further writes to the {@link Dict}
	 */
	public void store() {
		assert !initialized;
		initialized = true;
		pairs.keySet().stream().sorted().forEachOrdered(key -> {
			storer.appendLine(keySerializer.apply(key));
			storer.appendLine(valueSerializer.apply(pairs.get(key)));
		});
	}

	/**
	 * adds a pair to the Dict. Should only be called before a store operation
	 * Does not save the data persistently.
	 * 
	 * @param p
	 *            the {@link Pair} to be stored
	 */
	@Override
	public void add(K key, V value) {
		assert !initialized;
		pairs.put(key, value);
	}

	/**
	 * adds pairs to the Dict. Should only be called before a store operation
	 * Does not save the data persistently.
	 * 
	 * @param ps
	 *            the {@link Pair}s to be stored
	 */
	@Override
	public void addAll(Map<K, V> ps) {
		assert !initialized;
		pairs.putAll(ps);
	}

	/**
	 * @param key
	 *            the key to be searched in the dictionary
	 * @return the value that matches key or {@link Optional.empty} otherwise.
	 * @throws InterruptedException
	 */
	@Override
	public CompletableFuture<Optional<V>> find(K key) {
		return BinarySearch.valueOf(storer, keySerializer.apply(key), 0, storer.numberOfLines())//
				.thenApply(o -> o.map(valueParser));
	}
}
