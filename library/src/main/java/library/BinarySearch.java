package library;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

/**
 * A helper class of the Library, not meant for use by users
 * inculded the implementation of the binary search used by
 * the library
 */
class BinarySearch {
	
	/**
	 * same as {@link #valueOf(CompletableFuture, String, int, int)}, except
	 * high is the number of lines in the {@link FutureLineStorage}  
	 * @see {@link #valueOf(CompletableFuture, String, int, int)}
	 */
	static CompletableFuture<Optional<String>> valueOf(CompletableFuture<FutureLineStorage> storer, String key, int low){
		return storer.thenCompose(fls->fls.numberOfLines()).thenCompose(high->valueOf(storer,key,low,high));
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
	static CompletableFuture<Optional<String>> valueOf(CompletableFuture<FutureLineStorage> storer, String key, int low, int high) {
		return storer.thenCompose(fls -> of(fls,key,low/2,high/2-1));
	}
	
	private static CompletableFuture<Optional<String>> of(FutureLineStorage storer, String key, int low, int high) {
		if (high < low)
			return CompletableFuture.completedFuture(Optional.empty());
		final int mid = (low + high) / 2;
		return storer.read(2 * mid).thenCompose(new Function<String, CompletableFuture<Optional<String>>>() {
			@Override
			public CompletableFuture<Optional<String>> apply(String current) {
				int comparison = current.compareTo(key);
				if (comparison == 0)
					return storer.read(2 * mid + 1).thenApply(s -> Optional.of(s));
				if (comparison < 0)
					return of(storer,key,mid+1,high);
				return of(storer,key,low,mid-1);
			}
		});
	}
}
