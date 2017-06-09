package il.ac.technion.cs.sd.buy.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import library.Dict;

//Local implementation of the library: for testing
public class DictTestImpl<K, V> implements Dict<K, V> {

	Map<K, V> m;
	boolean isStored;

	public DictTestImpl() {
		this.m = new HashMap<>();
		this.isStored = false;
	}

	@Override
	public void store() {
		this.isStored = true;

	}

	@Override
	public void add(K key, V value) {
		if (!isStored) {
			m.put(key, value);
		}

	}

	@Override
	public void addAll(Map<K, V> m) {
		if (!isStored) {
			m.putAll(m);
		}

	}

	@Override
	public CompletableFuture<Optional<V>> find(K key) {
		return CompletableFuture.completedFuture(Optional.of(m.get(key)));
	}

}
