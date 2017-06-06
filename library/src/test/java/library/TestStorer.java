package library;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

/** A storer to be used for storing where a functional storer is needed. Uses a
 * list of strings to simulate the file. Should only be used for testing as it
 * is not persistent. Also emulates the timing of LineStorage if initialized to
 * do so */
public class TestStorer implements FutureLineStorage {
  private final List<String> lst = new ArrayList<>();

  @Override public CompletableFuture<Void> appendLine(String line) {
   lst.add(line);
   return null;
  }
  
  @Override public CompletableFuture<String> read(int lineNumber) {
    String $ = lst.get(lineNumber);
    return CompletableFuture.completedFuture($);
  }
  
  @Override public CompletableFuture<Integer> numberOfLines() {
    return CompletableFuture.completedFuture(lst.size());
  }
}
