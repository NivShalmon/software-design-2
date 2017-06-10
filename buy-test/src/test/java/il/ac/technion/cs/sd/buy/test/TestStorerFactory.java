package il.ac.technion.cs.sd.buy.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

public class TestStorerFactory implements FutureLineStorageFactory {

	Map<String, TestStorer> store = new HashMap<>();

	@Override
	public CompletableFuture<FutureLineStorage> open(String name) {
		if (!store.containsKey(name))
			store.put(name, new TestStorer());
		return CompletableFuture.completedFuture(store.get(name));
	}

}
