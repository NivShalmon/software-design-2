package library;

import java.util.concurrent.CompletableFuture;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

public class TestStorerFactory implements FutureLineStorageFactory {
  
  @Override public CompletableFuture<FutureLineStorage> open(String arg0) {
    return CompletableFuture.completedFuture(new TestStorer());
  }
  
}
