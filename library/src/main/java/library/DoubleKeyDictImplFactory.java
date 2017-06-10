package library;

import java.util.function.Function;

public interface DoubleKeyDictImplFactory<K, T, V> {

	public DoubleKeyDict<K, T, V> create(Function<K, String> mainKeySerializer,
			Function<T, String> secondaryKeySerializer, //
			Function<V, String> valueSerializer, Function<String, K> mainKeyParser, //
			Function<String, T> secondaryKeyParser, Function<String, V> valueParser, String name);
}
