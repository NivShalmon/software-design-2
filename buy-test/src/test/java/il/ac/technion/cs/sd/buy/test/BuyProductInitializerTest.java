package il.ac.technion.cs.sd.buy.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl;
import il.ac.technion.cs.sd.buy.app.Order;

public class BuyProductInitializerTest {

	private static List<String> decodeListStrings(String encoded) {
		return Arrays.asList((encoded.substring(1).substring(0, encoded.substring(1).length() - 1).split(",")));
	}

	@Test
	public void test0() {
		Injector injector = Guice.createInjector(new TestingModule());
		BuyProductInitializerImpl init = injector.getInstance(BuyProductInitializerImpl.class);
		Scanner scanner;
		try {
			scanner = new Scanner(new File("../buy-test/src/test/resources/il/ac/technion/cs/sd/buy/test/small.xml"));
			String text = scanner.useDelimiter("\\A").next();
			scanner.close();
			init.setupXml(text);
			assertEquals(decodeListStrings(init.getOrderIdToHistory().find("1").get().get()).size(), 3);
			assertEquals(Order.decodeOrder(init.getOrderIdToOrder().find("1").get().get()).getProduct_id(), "android");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test1() {
		Injector injector = Guice.createInjector(new TestingModule());
		BuyProductInitializerImpl init = injector.getInstance(BuyProductInitializerImpl.class);
		Scanner scanner;
		try {
			scanner = new Scanner(new File("../buy-test/src/test/resources/il/ac/technion/cs/sd/buy/test/small.json"));
			String text = scanner.useDelimiter("\\A").next();
			scanner.close();
			init.setupJson(text);
			assertEquals(decodeListStrings(init.getOrderIdToHistory().find("1").get().get()).size(), 1);
			assertEquals(Order.decodeOrder(init.getOrderIdToOrder().find("3").get().get()).getProduct_id(), "linux");
			assertEquals(Order.decodeOrder(init.getOrderIdToOrder().find("2").get().get()).getAmount(), "5");
			assertEquals(Order.decodeOrder(init.getOrderIdToOrder().find("1").get().get()).getProduct_id(), "mac");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2() {
		Injector injector = Guice.createInjector(new TestingModule());
		BuyProductInitializerImpl init = injector.getInstance(BuyProductInitializerImpl.class);
		Scanner scanner;
		try {
			scanner = new Scanner(
					new File("../buy-test/src/test/resources/il/ac/technion/cs/sd/buy/test/small_2.json"));
			String text = scanner.useDelimiter("\\A").next();
			scanner.close();
			init.setupJson(text);
			assertEquals(decodeListStrings(init.getOrderIdToHistory().find("foo1234").get().get()).size(), 3);
			assert (Order.decodeOrder(init.getOrderIdToOrder().find("foo1234").get().get()).isCancelled());
			assertEquals(decodeListStrings(init.getUserIdToOrderIds().find("foo1234").get().get()).get(0), "foo1234");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
