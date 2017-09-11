package library;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;
import static library.Util.*;

/**
 * The provided implementation of {@link Dict}, using {@link FutureLineStorage}
 * 
 * @see {@link DictFactory} and {@link LibraryModule} for more info on how to
 *      create an instance
 */
public class DictImpl implements Dict {
	private final CompletableFuture<FutureLineStorage> storer;
	private final Map<String, String> pairs = new HashMap<>();
	private CompletableFuture<?> storingStatus;

	@Inject
	DictImpl(FutureLineStorageFactory factory, //
			@Assisted String name) {
		storingStatus = storer = factory.open(name);
	}

	public CompletableFuture<Void> store() {
		return (storingStatus = storeToStorage(pairs, storer, storingStatus))//
				.thenAccept(s -> {
					// empty block to turn this into CompletableFuture<Void>
				});
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
		return storingStatus.thenCompose(v->BinarySearch.valueOf(storer, key, 0));
	}
}
