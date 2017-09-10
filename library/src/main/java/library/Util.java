package library;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

public class Util {
	static <T> CompletableFuture<?> doAfter(CompletableFuture<?> first, CompletableFuture<T> second,
			Function<T, ?> function) {
		return first.thenCombine(second,
				(f, t) -> function.apply(t));
	}
	
	static CompletableFuture<?> storeToStorage(Map<String, String> map, CompletableFuture<FutureLineStorage> store,
			CompletableFuture<?> current) {
		for (String key : map.keySet().stream().sorted().collect(Collectors.toList())) {
			current = doAfter(current,store,s->s.appendLine(key));
			current = doAfter(current,store,s->s.appendLine(map.get(key)));
		}
		return current;
	}
}
