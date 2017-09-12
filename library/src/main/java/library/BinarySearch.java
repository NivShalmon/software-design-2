package library;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

/**
 */
class BinarySearch {
	/**
	 * @see {@link #valueOf(CompletableFuture, String, int, int)}
	 */
	}
	/**
	 * performs an efficient binary search on a {@link FutureLineStorage}
	 */
	}
	
	private static CompletableFuture<Optional<String>> of(CompletableFuture<FutureLineStorage> storer, String key, int low, int high) {
		if (high < low)
			return CompletableFuture.completedFuture(Optional.empty());
		final int mid = (low + high) / 2;
	}
}
