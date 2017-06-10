package il.ac.technion.cs.sd.buy.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import library.DoubleKeyDict;

//
public class DoubleDictTestImpl implements DoubleKeyDict {

	Map<String, Map<String, String>> main;
	Map<String, Map<String, String>> secondary;
	boolean store;

	public DoubleDictTestImpl() {
		this.main = new HashMap<>();
		this.secondary = new HashMap<>();
		this.store = false;
	}

	@Override
	public void add(String mainKey, String secondaryKey, String value) {
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
	public CompletableFuture<Void> store() {
		this.store = true;
		return CompletableFuture.completedFuture(1).thenAccept(s->{});
	}

	@Override
	public CompletableFuture<Map<String, String>> findByMainKey(String key) {
		return CompletableFuture.completedFuture(main.get(key));
	}

	@Override
	public CompletableFuture<Optional<String>> findByKeys(String mainKey, String secondaryKey) {
		return CompletableFuture.completedFuture(Optional.of(main.get(mainKey).get(secondaryKey)));
	}

	@Override
	public CompletableFuture<Map<String, String>> findBySecondaryKey(String key) {
		return CompletableFuture.completedFuture(secondary.get(key));

	}

}
