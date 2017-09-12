package library;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

/**
 * The provided implementation of {@link Dict}, using {@link FutureLineStorage}
 * 
 * @see {@link DictFactory} and {@link LibraryModule} for more info on how to
 *      create an instance
 */
public class DictImpl implements Dict {
	private final FutureLineStorage storer;
	private final Map<String, String> pairs = new HashMap<>();

	@Inject
	DictImpl(FutureLineStorageFactory factory, //
			@Assisted String name) {
		try {
			storer = factory.open(name).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public CompletableFuture<Void> store() {
		return storeToStorage(pairs, storer).thenAccept(s -> {
		});
	}

	static CompletableFuture<Void> storeToStorage(Map<String, String> map, FutureLineStorage store) {
		try {
			for (String key : map.keySet().stream().sorted().collect(Collectors.toList())) {
				store.appendLine(key).get();
				store.appendLine(map.get(key)).get();
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public void add(String key, String value) {
		pairs.put(key, value);
	}

	@Override
	public void addAll(Map<String, String> ps) {
		pairs.putAll(ps);
	}

	@Override
	public CompletableFuture<Optional<String>> find(String key) {
		return BinarySearch.valueOf(storer, key, 0, storer.numberOfLines());
	}
}
