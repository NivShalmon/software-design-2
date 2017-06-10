package library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class DoubleKeyDictTest {
	static Injector injector = Guice.createInjector(new TestLineStorageModule());
	static DoubleKeyDictImplFactory f = injector.getInstance(DoubleKeyDictImplFactory.class);
	
	@Test()
	public void test0() throws InterruptedException, ExecutionException {
		DoubleKeyDict testDict = f.create("test");
		testDict.add("a", "Dor", "Niv");
		testDict.add("b", "Niv", "10203040");
		testDict.add("b", "Dor", "1020304050");
		
		testDict.store();
		assertEquals(2, testDict.findByMainKey("b").get().size());
	}

	@Test()
	public void test1() throws InterruptedException, ExecutionException {
		DoubleKeyDict testDict = f.create("test");
		testDict.add("12345", "Dor", "a1");
		testDict.add("54321", "Niv", "a2");
		//
		testDict.store();
		//
		assertTrue(testDict.findByMainKey("asdf").get().isEmpty());
	}
//
//	@Test()
//	public void test2() throws InterruptedException {
//		List<Triple> input = new ArrayList<Triple>();
//		for (int i = 0; i < 3339; i++) {
//			input.add(new Triple(i + "", i + "", i + ""));
//		}
//		DoubleKeyDict testDict = injector.getInstance(DoubleKeyDict.class);
//		//
//		testDict.addAndStore(input);
//		//
//		assertEquals("1456", testDict.findByKeys("1456", "1456").get());
//	}
//
//	@Test()
//	public void test3() throws InterruptedException {
//		List<Triple> input = new ArrayList<Triple>();
//		input.add(new Triple("b", "Niv", "c"));
//		input.add(new Triple("a", "Dor", "c"));
//		DoubleKeyDict testDict = injector.getInstance(DoubleKeyDict.class);
//		testDict.addAndStore(input);
//		//
//		assertEquals(new Pair("Niv", "c"), testDict.findByMainKey("b").get(0));
//	}
//
//	@Test()
//	public void test4() throws InterruptedException {
//		List<Triple> input = new ArrayList<Triple>();
//		input.add(new Triple("b", "Niv", "c"));
//		input.add(new Triple("a", "Dor", "c"));
//		DoubleKeyDict testDict = injector.getInstance(DoubleKeyDict.class);
//		testDict.addAndStore(input);
//		//
//		assertEquals(new Pair("a", "c"), testDict.findBySecondaryKey("Dor").get(0));
//	}
//
//	@Test()
//	public void test5() throws InterruptedException {
//		List<Triple> input = new ArrayList<Triple>();
//		input.add(new Triple("b", "Niv", "c"));
//		input.add(new Triple("b", "Dor", "c"));
//		input.add(new Triple("c", "x", "y"));
//		DoubleKeyDict testDict = injector.getInstance(DoubleKeyDict.class);
//		testDict.addAndStore(input);
//		//
//		List<Pair> lst = new ArrayList<>();
//		lst.add(new Pair("Dor", "c"));
//		lst.add(new Pair("Niv", "c"));
//		lst.stream().sorted().collect(Collectors.toList());
//		List<Pair> res = testDict.findByMainKey("b").stream().sorted().collect(Collectors.toList());
//		//
//		for (int i = 0; i < res.size(); i++)
//			assertEquals(lst.get(i), res.get(i));
//	}
//
//	@Test()
//	public void test6() throws InterruptedException {
//		List<Triple> input = new ArrayList<Triple>();
//		input.add(new Triple("b", "x", "c"));
//		input.add(new Triple("b", "x", "c"));
//		input.add(new Triple("c", "x", "y"));
//		DoubleKeyDict testDict = injector.getInstance(DoubleKeyDict.class);
//		testDict.addAndStore(input);
//		//
//		List<Pair> lst = new ArrayList<>();
//		lst.add(new Pair("b", "c"));
//		lst.add(new Pair("b", "c"));
//		lst.add(new Pair("c", "y"));
//		lst.stream().sorted().collect(Collectors.toList());
//		List<Pair> res = testDict.findBySecondaryKey("x").stream().sorted().collect(Collectors.toList());
//		//
//		for (int i = 0; i < res.size(); i++)
//			assertEquals(lst.get(i), res.get(i));
//	}
}
