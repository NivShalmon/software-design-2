package il.ac.technion.cs.sd.buy.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import library.DoubleKeyDict;

//
public class DoubleDictTestImpl<K, T, V> implements DoubleKeyDict<K, T, V> {

	Map<K, Map<T, V>> main;
	Map<T, Map<K, V>> secondary;
	boolean store;

	public DoubleDictTestImpl() {
		this.main = new HashMap<>();
		this.secondary = new HashMap<>();
		this.store = false;
	}

	@Override
	public void add(K mainKey, T secondaryKey, V value) {
		if (!this.store) {
			if (!main.containsKey(mainKey))
				main.put(mainKey, new HashMap<>());
			main.get(mainKey).put(secondaryKey, value);

			if (!secondary.containsKey(secondaryKey))
				secondary.put(secondaryKey, new HashMap<>());
			secondary.get(secondaryKey).put(mainKey, value);
		}
	}

	@Override
	public void store() {
		this.store = true;

	}

	@Override
	public CompletableFuture<Map<T, V>> findByMainKey(K key) {
		return CompletableFuture.completedFuture(main.get(key));
	}

	@Override
	public CompletableFuture<Optional<V>> findByKeys(K mainKey, T secondaryKey) {
		return CompletableFuture.completedFuture(Optional.of(main.get(mainKey).get(secondaryKey)));
	}

	@Override
	public CompletableFuture<Map<K, V>> findBySecondaryKey(T key) {
		return CompletableFuture.completedFuture(secondary.get(key));

	}

}
