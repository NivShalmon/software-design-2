package library;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

/**
 * The implementation of the Dict. Can be created using assisted injection. see
 * {@link TestLineStorageModule} for example on how to use assisted injection to
 * add the binding to a moudle.
 */
public class DictImpl implements Dict {
	private final CompletableFuture<FutureLineStorage> storer;
	private Map<String, String> pairs = new HashMap<>();

	@Inject
	DictImpl(FutureLineStorageFactory factory, //
			@Assisted String name) {
		this.storer = factory.open(name);
	}

	public void store() {
		pairs.keySet().stream().sorted().forEachOrdered(key -> {
			storer.thenAccept(s -> s.appendLine(key));
			storer.thenAccept(s -> s.appendLine(pairs.get(key)));
		});
	}

	@Override
	public void add(String key, String value) {
		pairs.put(key, value);
	}

	@Override
	public void addAll(Map< String,  String> ps) {
		pairs.putAll(ps);
	}

	@Override
	public CompletableFuture<Optional<String>> find(String key) {
		return BinarySearch.valueOf(storer,key, 0, storer.thenCompose(s -> s.numberOfLines()));
	}
}
