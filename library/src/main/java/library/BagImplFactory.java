package library;

import java.util.function.Function;

import com.google.inject.Inject;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

public abstract class BagImplFactory<T> {
	Function<T, String> serializer = t -> t.toString();

	public BagImplFactory<T> setSerializer(Function<T, String> serializer) {
		if (serializer != null)
			this.serializer = serializer;
		return this;
	}

	public abstract Bag<T> create();

	public static class BagImplFactoryImpl<T> extends BagImplFactory<T> {
		private FutureLineStorage s;

		@Inject
		public BagImplFactoryImpl(FutureLineStorage s) {
			this.s = s;
		}

		@Override
		public Bag<T> create() {
			return new BagImpl<T>(s, serializer);
		}

	}
}
