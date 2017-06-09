package library;

import java.util.function.Function;

import com.google.inject.Inject;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

/**
 * A factory for creating {@link DictImpl}
 */
public abstract class DictImplFactory<K, V> {
	Function<K, String> keySerializer = k -> k.toString();
	Function<V, String> valueSerializer = v -> v.toString();
	Function<String, V> valueParser;
	String name;

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
	
	public DictImplFactory<K, V> setName(String name){
		if (name != null)
			this.name = name;
		return this;
	}

	public abstract Dict<K, V> create();

	public static class DictImplFactoryImpl<K,V> extends DictImplFactory<K,V> {
		private FutureLineStorageFactory s;

		@Inject
		public DictImplFactoryImpl(FutureLineStorageFactory s) {
			this.s = s;
		}

		@Override
		public Dict<K, V> create() {
			if (valueParser == null || name == null)
				throw new IllegalStateException();
			return new DictImpl<K,V>(s.open(name), keySerializer, valueSerializer, valueParser);
		}

	}
}