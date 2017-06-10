package il.ac.technion.cs.sd.buy.test;

import java.io.File;
import java.util.Scanner;

import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductReader;

public class LibraryIntegrationTest {
	static Injector injector = Guice.createInjector(new BuyProductModule(), new TestLineStorageModule());
	
	@Test
	public void test0() throws Exception {
		BuyProductInitializer init = injector.getInstance(BuyProductInitializer.class);
		Scanner scanner;
		scanner = new Scanner(new File("../buy-test/src/test/resources/il/ac/technion/cs/sd/buy/test/small.xml"));
		String text = scanner.useDelimiter("\\A").next();
		scanner.close();
		init.setupXml(text);
		BuyProductReader reader = injector.getInstance(BuyProductReader.class);
		assertTrue(reader.isValidOrderId("1").get());
		assertFalse(reader.isValidOrderId("1233").get());
	}
}
