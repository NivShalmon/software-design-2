package library;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

@SuppressWarnings("unchecked")
public class DictTest {
	static Injector injector = Guice.createInjector(new TestLineStorageModule());
	static DictFactory<String, String> f;
	{
		f = injector.getInstance(DictFactory.class);
		f.setValueParser(s -> s);
		f.setName("test");
	}

	@Test()
	public void test0() throws InterruptedException, ExecutionException {
		Dict<String, String> testDict = f.create();
		testDict.add("a", "Dor");
		testDict.add("b", "Niv");
		testDict.store();
		//
		assertEquals("Niv", testDict.find("b").get().get());
	}

	@Test()
	public void test1() throws InterruptedException, ExecutionException {
		Dict<String, String> testDict = f.create();
		testDict.add("a", "Dor");
		testDict.store();
		//
		assertThat(testDict.find("asdf").get(), is(Optional.empty()));
	}

	@Test()
	public void test2() throws InterruptedException, ExecutionException {
		Dict<String, String> testDict = f.create();
		for (int i = 0; i < 3339; i++) {
			testDict.add(i + "", i + "");
		}
		testDict.store();
		//
		assertEquals(1456 + "", testDict.find("1456").get().get());
	}

	@Test()
	public void test3() throws InterruptedException, ExecutionException {
		Dict<String, String> testDict = f.create();
		testDict.add("b", "Niv");
		testDict.add("a", "Dor");
		testDict.store();
		//
		assertEquals("Niv", testDict.find("b").get().get());
	}
}
