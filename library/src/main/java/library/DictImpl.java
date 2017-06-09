package library;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

/**
 * The implementation of the Dict.
 * Can be created using assisted injection.
 * see {@link TestLineStorageModule} for example on how to use
 * assisted injection to add the binding to a moudle.
 */
public class DictImpl<K, V> implements Dict<K, V> {
	private final CompletableFuture<FutureLineStorage> storer;
	private Map<K, V> pairs = new HashMap<>();
	private Function<K, String> keySerializer;
	private Function<V, String> valueSerializer;
	private Function<String, V> valueParser;

	@Inject
	DictImpl(FutureLineStorageFactory factory, //
			@Assisted Function<K, String> keySerializer, //
			@Assisted Function<V, String> valueSerializer, //
			@Assisted Function<String, V> valueParser,//
			@Assisted String name) {
		this.storer = factory.open(name);
		this.keySerializer = keySerializer;
		this.valueSerializer = valueSerializer;
		this.valueParser = valueParser;
	}

	public void store() {
		pairs.keySet().stream().sorted().forEachOrdered(key -> {
			storer.thenAccept(s -> s.appendLine(keySerializer.apply(key)));
			storer.thenAccept(s -> s.appendLine(valueSerializer.apply(pairs.get(key))));
		});
	}

	public void add(K key, V value) {
		pairs.put(key, value);
	}

	public void addAll(Map<K, V> ps) {
		pairs.putAll(ps);
	}

	public CompletableFuture<Optional<V>> find(K key) {
		return BinarySearch.valueOf(storer, keySerializer.apply(key), 0, storer.thenCompose(s -> s.numberOfLines()))//
				.thenApply(o -> o.map(valueParser));
	}
}
