package library;

import java.util.function.Function;

/**
 * A factory for creating {@link DictImpl} using assisted injection. See
 * {@link https://github.com/google/guice/wiki/AssistedInject} for more info
 */
public interface DictImplFactory<K, V> {

	public Dict<K, V> create(Function<K, String> keySerializer, Function<V, String> valueSerializer, //
			Function<String, V> valueParser, String name);
}