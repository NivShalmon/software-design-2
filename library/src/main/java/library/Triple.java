package library;

public class Triple<K extends Comparable<K>,T extends Comparable<T>,V> implements Comparable<Triple<K,T,V>> {
  private K key1;
  private T key2;
  private V value;

  public Triple(K key1, T key2, V value) {
	this.key1 = key1;
	this.key2 = key2;
	this.value = value;
}
public K getKey1() {
    return key1;
  }
  public void setKey1(K key1) {
    this.key1 = key1;
  }
  public T getKey2() {
    return key2;
  }
  public void setKey2(T key2) {
    this.key2 = key2;
  }
  public V getValue() {
    return value;
  }
  public void setValue(V value) {
    this.value = value;
  }
  @Override public int compareTo(Triple<K,T,V> o) {
    int cmp = this.getKey1().compareTo(o.getKey1());
    return cmp != 0 ? cmp : this.getKey2().compareTo(o.getKey2());
  }
  @Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((key1 == null) ? 0 : key1.hashCode());
	result = prime * result + ((key2 == null) ? 0 : key2.hashCode());
	result = prime * result + ((value == null) ? 0 : value.hashCode());
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
	Triple<?, ?, ?> other = (Triple<?, ?, ?>) obj;
	if (key1 == null) {
		if (other.key1 != null)
			return false;
	} else if (!key1.equals(other.key1))
		return false;
	if (key2 == null) {
		if (other.key2 != null)
			return false;
	} else if (!key2.equals(other.key2))
		return false;
	if (value == null) {
		if (other.value != null)
			return false;
	} else if (!value.equals(other.value))
		return false;
	return true;
}
  @Override public String toString() {
    return "(" + getKey1() + "," + getKey2() + "," + getValue() + ")";
  }
}
