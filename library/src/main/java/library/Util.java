package library;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

public class Util {
	static <T> CompletableFuture<?> doAfter(CompletableFuture<?> first, CompletableFuture<T> second,
			Function<T, ?> function) {
		return first.thenCombine(CompletableFuture.completedFuture(second),
				(f, s) -> s.thenApply(t -> function.apply(t)));
	}
	
	static CompletableFuture<?> storeToStorage(Map<String, String> map, CompletableFuture<FutureLineStorage> store,
			CompletableFuture<?> current) {
		for (String key : map.keySet().stream().sorted().collect(Collectors.toList())) {
			current = current.thenCompose(v->store.thenCompose(fls -> fls.appendLine(key)));
			current = current.thenCompose(v->store.thenCompose(fls -> fls.appendLine(map.get(key))));
		}
		return current;
	}
}
