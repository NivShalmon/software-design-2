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

public class DoubleKeyDictImpl implements DoubleKeyDict {
	private final Map<String, Map<String, String>> mainKeyMap = new HashMap<>();
	private final Map<String, Map<String, String>> secondaryKeyMap = new HashMap<>();
	private final Dict mainKeyDict;
	private final Dict secondaryKeyDict;
	private final CompletableFuture<FutureLineStorage> values;

	@Inject
	public DoubleKeyDictImpl(DictImplFactory dictFactory, FutureLineStorageFactory lineStorageFactory, //
			@Assisted String name) {
		this.mainKeyDict = dictFactory.create(name + ".mainKey");
		this.secondaryKeyDict = dictFactory.create(name + ".secondaryKey");
		this.values = lineStorageFactory.open(name + ".values");
	}

	@Override
	public void add(String mainKey, String secondaryKey, String value) {
		addToMap(mainKey, secondaryKey, value, mainKeyMap);
		addToMap(secondaryKey, mainKey, value, secondaryKeyMap);
	}

	private <A, B, C> void addToMap(A key1, B key2, C value, Map<A, Map<B, C>> map) {
		if (!map.containsKey(key1))
			map.put(key1, new HashMap<>());
		map.get(key1).put(key2, value);
	}

	@Override
	public void store() {
		int startingLine = 0;
		for (String key : mainKeyMap.keySet()) {
			final Map<String, String> current = mainKeyMap.get(key);
			int endingLine = startingLine + current.size() * 2;
			mainKeyDict.add(key, startingLine + "" + endingLine);
			startingLine = endingLine;
			current.keySet().stream().sorted().forEachOrdered(k -> {
				values.thenAccept(s -> s.appendLine(k));
				values.thenAccept(s -> s.appendLine(current.get(k)));
			});
		}
		for (String key : secondaryKeyMap.keySet()) {
			final Map<String, String> current = secondaryKeyMap.get(key);
			int endingLine = startingLine + current.size() * 2;
			secondaryKeyDict.add(key, startingLine + "," + endingLine);
			startingLine = endingLine;
			current.keySet().stream().sorted().forEachOrdered(k -> {
				values.thenAccept(s -> s.appendLine(k));
				values.thenAccept(s -> s.appendLine(current.get(k)));
			});
		}
	}

	@Override
	public CompletableFuture<Map<String, String>> findByMainKey(String key) {
		return mainKeyDict.find(key).thenApply(o -> o.map(s -> {
			String[] lines = s.split(",");
			int start, end;
			try {
				start = Integer.parseInt(lines[0]);
				end = Integer.parseInt(lines[1]);
			} catch (NumberFormatException e) {
				return new HashMap<String, String>();
			}
			Map<String, String> m = new HashMap<>();
			for (int i = start; i < end; ++i) {
				final int line = i;
				CompletableFuture<String> sKey = values.thenCompose(ls -> ls.read(line));
				CompletableFuture<String> value = values.thenCompose(ls -> ls.read(line));
				try {
					m.put(sKey.get(), value.get());
				} catch (InterruptedException | ExecutionException e) {
					return new HashMap<String, String>();
				}
			}
			return m;
		}).orElse(new HashMap<String, String>()));
	}

	@Override
	public CompletableFuture<Optional<String>> findByKeys(String mainKey, String secondaryKey) {
		return mainKeyDict.find(mainKey).thenCompose(o -> o.map(s -> {
			String[] lines = s.split(",");
			int start, end;
			start = Integer.parseInt(lines[0]);
			end = Integer.parseInt(lines[1]);
			return BinarySearch.valueOf(values, secondaryKey, start, end);
		}).orElse(CompletableFuture.completedFuture(Optional.empty())));
	}

	@Override
	public CompletableFuture<Map<String, String>> findBySecondaryKey(String key) {
		return secondaryKeyDict.find(key).thenApply(o -> o.map(s -> {
			String[] lines = s.split(",");
			int start, end;
			try {
				start = Integer.parseInt(lines[0]);
				end = Integer.parseInt(lines[1]);
			} catch (NumberFormatException e) {
				return new HashMap<String, String>();
			}
			Map<String, String> m = new HashMap<>();
			for (int i = start; i < end; ++i) {
				final int line = i;
				CompletableFuture<String> sKey = values.thenCompose(ls -> ls.read(line));
				CompletableFuture<String> value = values.thenCompose(ls -> ls.read(line));
				try {
					m.put(sKey.get(), value.get());
				} catch (InterruptedException | ExecutionException e) {
					return new HashMap<String, String>();
				}
			}
			return m;
		}).orElse(new HashMap<String, String>()));
	}

}
