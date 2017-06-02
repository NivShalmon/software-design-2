package library;

public interface Bag {
	
	public void add(String s);
	public void addAll(String ss);
	public void store();
	public default void store(String ss){
		addAll(ss);
		store();
	}
	public boolean isIn(String s);
}
