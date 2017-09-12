package library;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

/**
 * A helper class of the Library, not meant for use by users inculded the
 * implementation of the binary search used by the library
 */
class BinarySearch {

	/**
	 * a version of valueOf where the high parameter is also a completable
	 * future, to allow using {@link FutureLineStorage#numberOfLines()} as the
	 * high value
	 * 
	 * @see {@link #valueOf(CompletableFuture, String, int, int)}
	 */
	static CompletableFuture<Optional<String>> valueOf(FutureLineStorage storer, String key, int low,
			CompletableFuture<Integer> high) {
		try {
			return valueOf(storer, key, low, high.get());
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * performs an efficient binary search on a {@link FutureLineStorage}
	 * 
	 * @param storer
	 *            the {@link FutureLineStorage}, written in a format of key in
	 *            one line, followed by value in the next, sorted by key
	 * @param key
	 *            the key to be searched
	 * @param low
	 *            the line number of the first key, 0 for whole file search
	 * @param high
	 *            the number of lines used, numberOfLines() for entire file
	 *            search
	 * @return an Optional with the value saved for key, or Optional.empty() if
	 *         the key doesn't exist
	 */
	static CompletableFuture<Optional<String>> valueOf(FutureLineStorage storer, String key, int low, int high) {
		try {
			return of(storer, key, low / 2, high / 2 - 1);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private static CompletableFuture<Optional<String>> of(FutureLineStorage storer, String key, int low, int high)
			throws InterruptedException, ExecutionException {
		if (high < low)
			return CompletableFuture.completedFuture(Optional.empty());
		final int mid = (low + high) / 2;
		String current = storer.read(2 * mid).get();
		int comparison = current.compareTo(key);
		if (comparison == 0)
			return CompletableFuture.completedFuture(Optional.of(storer.read(2 * mid + 1).get()));
		if (comparison < 0)
			return of(storer, key, mid + 1, high);
		return of(storer, key, low, mid - 1);
	}
}
