package library;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

/**
 * The provided implementation of {@link DoubleKeyDict}, using
 * {@link FutureLineStorage}
 * 
 * @see {@link DoubleKeyDictFactory} and {@link LibraryModule} for more info on
 *      how to create an instance
 */
public class DoubleKeyDictImpl implements DoubleKeyDict {
	private final Dict mainKeyDict;
	private final Dict secondaryKeyDict;
	private final FutureLineStorage storer;
	private final Map<String, Map<String, String>> mainKeyMap = new HashMap<>();
	private final Map<String, Map<String, String>> secondaryKeyMap = new HashMap<>();

	private class IntegerWrapper {
		public int val;

		public IntegerWrapper() {
			this.val = 0;
		}

		public String toString() {
			return val + "";
		}
	}

	@Inject
	public DoubleKeyDictImpl(DictFactory dictFactory, FutureLineStorageFactory lineStorageFactory,
			@Assisted String name) {
		try {
			storer = lineStorageFactory.open(name + ".valuesMap").get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
		mainKeyDict = dictFactory.create(name + ".mainKeyMap");
		secondaryKeyDict = dictFactory.create(name + ".secondaryKeyMap");
	}

	@Override
	public void add(String mainKey, String secondaryKey, String value) {
		addToMap(mainKeyMap, mainKey, secondaryKey, value);
		addToMap(secondaryKeyMap, secondaryKey, mainKey, value);
	}

	public CompletableFuture<Void> store() {
		IntegerWrapper currentLine = new IntegerWrapper();
		try {
			storeDict(mainKeyDict, mainKeyMap, currentLine);
			storeDict(secondaryKeyDict, secondaryKeyMap, currentLine);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
		return CompletableFuture.completedFuture(null);
	}

	private void storeDict(final Dict dict, final Map<String, Map<String, String>> m, IntegerWrapper currentLine)
			throws InterruptedException, ExecutionException {
		for (String key : m.keySet()) {
			Map<String, String> current = m.get(key);
			DictImpl.storeToStorage(current, storer).get();
			int startingLine = currentLine.val;
			currentLine.val += current.size() * 2;
			dict.add(key, startingLine + "," + currentLine);
		}
		dict.store().get();
	}

	private void addToMap(final Map<String, Map<String, String>> m, String key1, String key2, String value) {
		if (!m.containsKey(key1))
			m.put(key1, new HashMap<>());
		m.get(key1).put(key2, value);
	}

	@Override
	public CompletableFuture<Map<String, String>> findByMainKey(String key) {
		return findByKey(mainKeyDict, key);
	}

	@Override
	public CompletableFuture<Optional<String>> findByKeys(String key1, String key2) {
		Optional<String> o;
		try {
			o = mainKeyDict.find(key1).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
		if (!o.isPresent())
			return CompletableFuture.completedFuture(Optional.empty());
		String[] lines = o.get().split(",");
		return BinarySearch.valueOf(storer, key2, Integer.parseInt(lines[0]), Integer.parseInt(lines[1]));
	}

	@Override
	public CompletableFuture<Map<String, String>> findBySecondaryKey(String key) {
		return findByKey(secondaryKeyDict, key);
	}

	private CompletableFuture<Map<String, String>> findByKey(Dict d, String key) {
		return d.find(key).thenApply(o -> o.map(str -> {
			Map<String, String> $ = new HashMap<>();
			String[] lines = o.get().split(",");
			int end = Integer.parseInt(lines[1]);
			try {
				for (int i = Integer.parseInt(lines[0]); i < end; i += 2) {
					$.put(storer.read(i).get(), storer.read(i + 1).get());
				}
			} catch (InterruptedException | ExecutionException e) {
				return new HashMap<String, String>();
			}
			return $;
		}).orElse(new HashMap<String, String>()));
	}
}
