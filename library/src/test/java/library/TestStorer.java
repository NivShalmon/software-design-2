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

  @Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((lst == null) ? 0 : lst.hashCode());
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	TestStorer other = (TestStorer) obj;
	if (lst == null) {
		if (other.lst != null)
			return false;
	} else if (!lst.equals(other.lst))
		return false;
	return true;
}

TestStorer(){
	  
  }
  
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
