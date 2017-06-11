import com.google.inject.Singleton;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by rds15 on 08/06/2017.
 */
@Singleton
class FutureLineStorageFactoryFake implements FutureLineStorageFactory {
	public static class FutureLineStorageFake implements FutureLineStorage {
		private final List<String> list = new ArrayList<>();

		@Override
		public CompletableFuture<Void> appendLine(String s) {
			list.add(s);
			return CompletableFuture.completedFuture(null);
		}

		@Override
		public CompletableFuture<String> read(int lineNumber) {
			return CompletableFuture.completedFuture(list.get(lineNumber)).thenApply(s -> {
				try {
					Thread.sleep(s.length());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return s;
			});
		}

		@Override
		public CompletableFuture<Integer> numberOfLines() {
			return CompletableFuture.completedFuture(list.size()).thenApply(i -> {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return i;
			});
		}

		public CompletableFuture<List<String>> getFileContent() {
			return CompletableFuture.completedFuture(this.list);
		}

	}

	private final Map<String, FutureLineStorageFake> files = new HashMap<>();

	@Override
	public CompletableFuture<FutureLineStorage> open(String fileName) {
		if (!files.containsKey(fileName))
			files.put(fileName, new FutureLineStorageFake());
		return CompletableFuture.completedFuture(files.get(fileName)).thenApply(i -> {
			try {
				Thread.sleep(files.size() * 100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			return i;
		});
	}

	public void clean() {
		this.files.clear();
	}
}
