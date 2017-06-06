package library;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

class BinarySearch {
	/**
	 * a vesrion of value of where the high parameter is also
	 * a completable future, to allow using {@link FutureLineStorage#numberOfLines()}
	 * as the high value 
	 * @see {@link #valueOf(FutureLineStorage, String, int, int)}
	 */
	static CompletableFuture<Optional<String>> valueOf(FutureLineStorage storer, String key, int low, CompletableFuture<Integer> high){
		return high.thenCompose(h -> valueOf(storer,key,low,h));
	}
	
	/**
	 * performs an efficient binary search on a {@link FutureLineStorage}
	 * @param storer the {@link FutureLineStorage}, written in a format of key in one line,
	 * followed by value in the next, sorted by key
	 * @param key the key to be searched
	 * @param low the line number of the first key, 0 for whole file search
	 * @param high the number of lines used, numberOfLines() for entire file search
	 * @return an Optional with the value saved for key, or Optional.empty() if the
	 * key doesn't exist
	 */
	static CompletableFuture<Optional<String>> valueOf(FutureLineStorage storer, String key, int low, int high) {
		if (high < low)
			return CompletableFuture.completedFuture(Optional.empty());
		final int mid = (low + high) / 2;
		return storer.read(mid).thenCompose(new Function<String, CompletableFuture<Optional<String>>>() {
			@Override
			public CompletableFuture<Optional<String>> apply(String current) {
				int comparison = current.compareTo(key);
				if (comparison == 0)
					return storer.read(mid + 1).thenApply(s -> Optional.of(s));
				if (comparison < 0)
					return valueOf(storer,key,mid+2,high);
				return valueOf(storer,key,low,mid-2);
			}
		});
	}
	/**
	 * performs an efficient binary search on a {@link FutureLineStorage}
	 * @param storer the {@link FutureLineStorage}
	 * @param key the key to be searched
	 * @param low the line number to begin searching from
	 * @param high the line number to stop searching at
	 * @return true iff key is stored in the storer
	 */
	static CompletableFuture<Boolean> isIn(FutureLineStorage storer, String key, int low, int high){
		return CompletableFuture.completedFuture(false);
	}
}
