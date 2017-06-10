package il.ac.technion.cs.sd.buy.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import library.Dict;

//Local implementation of the library: for testing
public class DictTestImpl implements Dict {

	Map<String, String> m;
	boolean isStored;

	public DictTestImpl() {
		this.m = new HashMap<>();
		this.isStored = false;
	}

	@Override
	public CompletableFuture<Void> store() {
		this.isStored = true;
		return CompletableFuture.completedFuture(1).thenAccept(s -> {});
	}

	@Override
	public void add(String key, String value) {
		if (!isStored) {
			m.put(key, value);
		}

	}

	@Override
	public void addAll(Map<String, String> m) {
		if (!isStored) {
			m.putAll(m);
		}

	}

	@Override
	public CompletableFuture<Optional<String>> find(String key) {
		if (!m.containsKey(key))
			return CompletableFuture.completedFuture(Optional.empty());
		return CompletableFuture.completedFuture(Optional.of(m.get(key)));

	}

}
