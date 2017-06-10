package library;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

public class DoubleKeyDictImpl<K, T, V> implements DoubleKeyDict<K, T, V> {
	private final Map<K, Map<T, V>> mainKeyMap = new HashMap<>();
	private final Map<T, Map<K, V>> secondaryKeyMap = new HashMap<>();
	private final Dict<K, String> mainKeyDict;
	private final Dict<T, String> secondaryKeyDict;
	private final CompletableFuture<FutureLineStorage> values;
	private final Function<K, String> mainKeySerializer;
	private final Function<T, String> secondaryKeySerializer;
	private final Function<V, String> valueSerializer;
	private final Function<String, K> mainKeyParser;
	private final Function<String, T> secondaryKeyParser;
	private final Function<String, V> valueParser;

	@Inject
	public DoubleKeyDictImpl(DictImplFactory<K, String> mainKeyFactory, DictImplFactory<T, String> secondaryKeyFactory,
			FutureLineStorageFactory f, //
			@Assisted Function<K, String> mainKeySerializer, @Assisted Function<T, String> secondaryKeySerializer, //
			@Assisted Function<V, String> valueSerializer, @Assisted Function<String, K> mainKeyParser, //
			@Assisted Function<String, T> secondaryKeyParser, @Assisted Function<String, V> valueParser,
			@Assisted String name) {
		this.mainKeyDict = mainKeyFactory.create(mainKeySerializer, s -> s, s -> s, name + ".mainKey");
		this.secondaryKeyDict = secondaryKeyFactory.create(secondaryKeySerializer, s -> s, s -> s,
				name + ".secondaryKey");
		;
		this.values = f.open(name + ".values");
		this.mainKeySerializer = mainKeySerializer;
		this.secondaryKeySerializer = secondaryKeySerializer;
		this.valueSerializer = valueSerializer;
		this.mainKeyParser = mainKeyParser;
		this.secondaryKeyParser = secondaryKeyParser;
		this.valueParser = valueParser;
	}

	@Override
	public void add(K mainKey, T secondaryKey, V value) {
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
		for (K key : mainKeyMap.keySet()) {
			final Map<T, V> current = mainKeyMap.get(key);
			int endingLine = startingLine + current.size() * 2;
			mainKeyDict.add(key, startingLine + "" + endingLine);
			startingLine = endingLine;
			current.keySet().stream().sorted().forEachOrdered(k -> {
				values.thenAccept(s -> s.appendLine(secondaryKeySerializer.apply(k)));
				values.thenAccept(s -> s.appendLine(valueSerializer.apply(current.get(k))));
			});
		}
		for (T key : secondaryKeyMap.keySet()) {
			final Map<K, V> current = secondaryKeyMap.get(key);
			int endingLine = startingLine + current.size() * 2;
			secondaryKeyDict.add(key, startingLine + "," + endingLine);
			startingLine = endingLine;
			current.keySet().stream().sorted().forEachOrdered(k -> {
				values.thenAccept(s -> s.appendLine(mainKeySerializer.apply(k)));
				values.thenAccept(s -> s.appendLine(valueSerializer.apply(current.get(k))));
			});
		}
	}

	@Override
	public CompletableFuture<Map<T, V>> findByMainKey(K key) {
		return mainKeyDict.find(key).thenApply(o -> o.map(s -> {
			String[] lines = s.split(",");
			int start, end;
			try {
				start = Integer.parseInt(lines[0]);
				end = Integer.parseInt(lines[1]);
			} catch (NumberFormatException e) {
				return new HashMap<T, V>();
			}
			Map<T, V> m = new HashMap<>();
			for (int i = start; i < end; ++i) {
				final int line = i;
				CompletableFuture<T> sKey = values.thenCompose(ls -> ls.read(line)).thenApply(secondaryKeyParser);
				CompletableFuture<V> value = values.thenCompose(ls -> ls.read(line)).thenApply(valueParser);
				try {
					m.put(sKey.get(), value.get());
				} catch (InterruptedException | ExecutionException e) {
					return new HashMap<T, V>();
				}
			}
			return m;
		}).orElse(new HashMap<T, V>()));
	}

	@Override
	public CompletableFuture<Optional<V>> findByKeys(K mainKey, T secondaryKey) {
//		return mainKeyDict.find(mainKey).thenApply(o -> o.map(new Function<String, CompletableFuture<Optional<V>>>() {
//
//			@Override
//			public CompletableFuture<Optional<V>> apply(String s){
//				String[] lines = s.split(",");
//				int start,end;
//				try{
//					start = Integer.parseInt(lines[0]);
//					end = Integer.parseInt(lines[1]);
//				}catch(NumberFormatException e){
//					return CompletableFuture.completedFuture(Optional.empty());
//				}
//				return BinarySearch.valueOf(values, secondaryKeySerializer.apply(secondaryKey), start, end)//
//						.thenApply(o -> o.map(valueParser));
//			}
//		};)
		return null;
	}

	@Override
	public CompletableFuture<Map<K, V>> findBySecondaryKey(T key) {
		return secondaryKeyDict.find(key).thenApply(o -> o.map(s -> {
			String[] lines = s.split(",");
			int start, end;
			try {
				start = Integer.parseInt(lines[0]);
				end = Integer.parseInt(lines[1]);
			} catch (NumberFormatException e) {
				return new HashMap<K, V>();
			}
			Map<K, V> m = new HashMap<>();
			for (int i = start; i < end; ++i) {
				final int line = i;
				CompletableFuture<K> sKey = values.thenCompose(ls -> ls.read(line)).thenApply(mainKeyParser);
				CompletableFuture<V> value = values.thenCompose(ls -> ls.read(line)).thenApply(valueParser);
				try {
					m.put(sKey.get(), value.get());
				} catch (InterruptedException | ExecutionException e) {
					return new HashMap<K, V>();
				}
			}
			return m;
		}).orElse(new HashMap<K, V>()));
	}

}
