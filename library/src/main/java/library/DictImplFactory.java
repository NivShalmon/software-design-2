package library;

import java.util.function.Function;

import com.google.inject.Inject;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

public abstract class DictImplFactory<K, V> {
	Function<K, String> keySerializer = k -> k.toString();
	Function<V, String> valueSerializer = v -> v.toString();
	Function<String, V> valueParser;

	public DictImplFactory<K, V> setKeySerializer(Function<K, String> keySerializer) {
		if(keySerializer != null)
			this.keySerializer = keySerializer;
		return this;
	}

	public DictImplFactory<K,V> setValueSerializer(Function<V, String> valueSerializer) {
		if (valueSerializer != null)
			this.valueSerializer = valueSerializer;
		return this;
	}

	public DictImplFactory<K, V> setValueParser(Function<String, V> valueParser) {
		if (valueParser != null)
			this.valueParser = valueParser;
		return this;
	}

	public abstract Dict<K, V> create();

	public static class DictImplFactoryImpl<K,V> extends DictImplFactory<K,V> {
		private FutureLineStorage s;

		@Inject
		public DictImplFactoryImpl(FutureLineStorage s) {
			this.s = s;
		}

		@Override
		public Dict<K, V> create() {
			if (valueParser == null)
				throw new IllegalStateException();
			return new DictImpl<K,V>(s, keySerializer, valueSerializer, valueParser);
		}

	}
}