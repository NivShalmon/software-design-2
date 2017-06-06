package library;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

public class BagImpl<T> implements Bag<T> {
	private Function<T, String> serialzier;
	private FutureLineStorage storer;
	private Set<T> values = new HashSet<>();

	public BagImpl(FutureLineStorage storer, Function<T, String> serializer) {
		this.storer = storer;
		this.serialzier = serializer;
	}

	@Override
	public void add(T t) {
		values.add(t);
	}

	@Override
	public void addAll(Collection<? extends T> ts) {
		values.addAll(ts);
	}

	@Override
	public void store() {
		values.stream().sorted().forEachOrdered(t -> storer.appendLine(serialzier.apply(t)));
	}

	@Override
	public CompletableFuture<Boolean> isIn(T t) {
		return BinarySearch.isIn(storer, serialzier.apply(t), 0, storer.numberOfLines());
	}

}
