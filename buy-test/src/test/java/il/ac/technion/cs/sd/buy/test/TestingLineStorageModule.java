package il.ac.technion.cs.sd.buy.test;

import com.google.inject.AbstractModule;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

class TestingLineStorageModule extends AbstractModule {
  private final FutureLineStorageFactoryImpl t = new FutureLineStorageFactoryImpl();

  @Override
  public void configure() {
    bind(FutureLineStorageFactory.class).toInstance(t);
  }

  private interface ThrowingSupplier<T> {
    T get() throws Exception;
  }
  private static <T> CompletableFuture<T> swallowException(ThrowingSupplier<T> ts) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return ts.get();
      } catch (Exception e) {
        throw new AssertionError(e);
      }
    });
  }

  private static class FutureLineStorageFactoryImpl implements FutureLineStorageFactory {
    private final Map<String, FutureLineStorage> map = new HashMap<>();
    @Override
    public CompletableFuture<FutureLineStorage> open(String fileName) {
      return swallowException(() -> {
		synchronized(FutureLineStorageFactoryImpl.this) {
          if (!map.containsKey(fileName))
            map.put(fileName, new FutureLineStorageImpl());
          Thread.sleep(map.size() * 100);
          return map.get(fileName);
		}
      });
    }

  }

  private static class FutureLineStorageImpl implements FutureLineStorage {
    private final List<String> lines = new ArrayList<>();
    @Override
    public CompletableFuture<Void> appendLine(String s) {
      return CompletableFuture.supplyAsync(() -> {
          lines.add(s);
          return null;
      });
    }

    @Override
    public CompletableFuture<String> read(int i) {
      return swallowException(() -> {
        String $ = lines.get(i);
        Thread.sleep($.length());
        return $;
      });
    }

    @Override
    public CompletableFuture<Integer> numberOfLines() {
      return swallowException(() -> {
        Thread.sleep(100);
        return lines.size();
      });
    }
  }
}
