package library;

public class Triple implements Comparable<Triple> {
  private String key1;
  private String key2;
  private String value;

  public String getKey1() {
    return key1;
  }
  public void setKey1(String key1) {
    this.key1 = key1;
  }
  public String getKey2() {
    return key2;
  }
  public void setKey2(String key2) {
    this.key2 = key2;
  }
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }
  @Override public int compareTo(Triple o) {
    int cmp = this.getKey1().compareTo(o.getKey1());
    return cmp != 0 ? cmp : this.getKey2().compareTo(o.getKey2());
  }
  @Override public boolean equals(Object obj) {
    if (!(obj instanceof Triple))
      return false;
    Triple t = (Triple) obj;
    return t.getKey1().equals(getKey1()) && t.getKey2().equals(getKey2()) && t.getValue().equals(getValue());
  }
  public Triple(String key1, String key2, String value) {
    super();
    this.key1 = key1;
    this.key2 = key2;
    this.value = value;
  }
  @Override public String toString() {
    return "(" + getKey1() + "," + getKey2() + "," + getValue() + ")";
  }
}
