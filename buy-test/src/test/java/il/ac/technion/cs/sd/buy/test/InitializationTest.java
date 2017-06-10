package il.ac.technion.cs.sd.buy.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl;

public class InitializationTest {

	@Test
	public void test() {
		Injector injector = Guice.createInjector(new TestingModule());
		BuyProductInitializerImpl init = injector.getInstance(BuyProductInitializerImpl.class);
		Scanner scanner;
		try {
			scanner = new Scanner(new File("../buy-test/src/test/resources/il/ac/technion/cs/sd/buy/test/small.xml"));
			String text = scanner.useDelimiter("\\A").next();
			scanner.close();
			init.setupXml(text);
			assertEquals(init.getOrderIdToHistory().find("1").get().get().size(), 3);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
