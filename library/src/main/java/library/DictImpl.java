package library;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.google.inject.Inject;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

public class DictImpl<K, V> implements Dict<K,V>{
	private	final CompletableFuture<FutureLineStorage> storer; 
	private Map<K, V> pairs = new HashMap<>();
	private Function<K, String> keySerializer;
	private Function<V, String> valueSerializer;
	private Function<String, V> valueParser;

	@Inject
	DictImpl(CompletableFuture<FutureLineStorage> storer, Function<K, String> keySerializer, Function<V, String> valueSerializer,
			Function<String, V> valueParser) {
		this.storer = storer;
		this.keySerializer = keySerializer;
		this.valueSerializer = valueSerializer;
		this.valueParser = valueParser;
	}

	public void store(){
		pairs.keySet().stream().sorted().forEachOrdered(key -> {
			storer.thenAccept(s-> s.appendLine(keySerializer.apply(key)));
			storer.thenAccept(s->s.appendLine(valueSerializer.apply(pairs.get(key))));
		});
	}

	public void add(K key, V value) {
		pairs.put(key, value);
	}

	public void addAll(Map<K, V> ps) {
		pairs.putAll(ps);
	}

	public CompletableFuture<Optional<V>> find(K key) {
		return BinarySearch.valueOf(storer, keySerializer.apply(key), 0, storer.thenCompose(s->s.numberOfLines()))//
				.thenApply(o -> o.map(valueParser));
	}
}
