package library;

import java.util.function.Function;

import com.google.inject.Inject;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

public abstract class DictFactory<K, V> {
	Function<K, String> keySerializer = k -> k.toString();
	Function<V, String> valueSerializer = v -> v.toString();
	Function<String, V> valueParser;
	String name;

	public DictFactory<K, V> setKeySerializer(Function<K, String> keySerializer) {
		if(keySerializer != null)
			this.keySerializer = keySerializer;
		return this;
	}

	public DictFactory<K,V> setValueSerializer(Function<V, String> valueSerializer) {
		if (valueSerializer != null)
			this.valueSerializer = valueSerializer;
		return this;
	}

	public DictFactory<K, V> setValueParser(Function<String, V> valueParser) {
		if (valueParser != null)
			this.valueParser = valueParser;
		return this;
	}
	
	public DictFactory<K, V> setName(String name){
		if (name != null)
			this.name = name;
		return this;
	}

	public abstract Dict<K, V> create();

	public static class DictFactoryImpl<K,V> extends DictFactory<K,V> {
		private FutureLineStorageFactory s;

		@Inject
		public DictFactoryImpl(FutureLineStorageFactory s) {
			this.s = s;
		}

		@Override
		public Dict<K, V> create() {
			if (valueParser == null || name == null)
				throw new IllegalStateException();
			return new Dict<K,V>(s.open(name), keySerializer, valueSerializer, valueParser);
		}

	}
}