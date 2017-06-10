package il.ac.technion.cs.sd.buy.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.OptionalInt;
import java.util.Scanner;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl;
import il.ac.technion.cs.sd.buy.app.BuyProductReader;
import il.ac.technion.cs.sd.buy.app.BuyProductReaderImpl;

public class BuyProductReaderInitializationTest {

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
			BuyProductReader reader = new BuyProductReaderImpl(init.getOrderIdToOrder(), init.getUserIdToOrderIds(),
					init.getProductIdToOrderIds(), init.getOrderIdToHistory(), init.getUserProductAmount());

			assert (!reader.isValidOrderId("jhv").get());
			assert (reader.isValidOrderId("1").get());
			assert (reader.isCanceledOrder("1").get());
			assert (reader.isModifiedOrder("1").get());
			assert (!reader.getNumberOfProductOrdered("165").get().isPresent());
			assert (reader.getNumberOfProductOrdered("1").get().isPresent());
			assertEquals(reader.getNumberOfProductOrdered("1").get().getAsInt(), -10);
			assertEquals(reader.getHistoryOfOrder("1").get().get(2).intValue(), -1);
			assertEquals(reader.getOrderIdsForUser("1").get().get(0), "1");
			assertEquals(reader.getTotalAmountSpentByUser("1").get().longValue(), 0);
			assertEquals(reader.getUsersThatPurchased("android").get().size(), 0);
			assertEquals(reader.getOrderIdsThatPurchased("android").get().size(), 1);
			assertEquals(reader.getOrderIdsThatPurchased("iphone").get().size(), 0);
			assertEquals(reader.getTotalNumberOfItemsPurchased("android").get().getAsLong(), 0);
			assertEquals(reader.getTotalNumberOfItemsPurchased("iphone").get().getAsLong(), 0);
			assertEquals(reader.getCancelRatioForUser("1").get().getAsDouble(), 1, 0.001);
			assertEquals(reader.getModifyRatioForUser("1").get().getAsDouble(), 0, 0.001);
			assertEquals(reader.getAllItemsPurchased("1").get(), new HashMap<>());
			assertEquals(reader.getItemsPurchasedByUsers("1").get(), new HashMap<>());

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
			BuyProductReader reader = new BuyProductReaderImpl(init.getOrderIdToOrder(), init.getUserIdToOrderIds(),
					init.getProductIdToOrderIds(), init.getOrderIdToHistory(), init.getUserProductAmount());

			assert (!reader.isValidOrderId("jhv").get());
			assert (reader.isValidOrderId("1").get());
			assert (reader.isValidOrderId("2").get());
			assert (reader.isValidOrderId("3").get());
			assert (!reader.isValidOrderId("4").get());

			assert (!reader.isCanceledOrder("1").get());
			assert (!reader.isModifiedOrder("1").get());
			assert (!reader.isCanceledOrder("2").get());
			assert (!reader.isModifiedOrder("2").get());
			assert (!reader.isCanceledOrder("3").get());
			assert (!reader.isModifiedOrder("3").get());

			assert (!reader.getNumberOfProductOrdered("165").get().isPresent());
			assert (reader.getNumberOfProductOrdered("1").get().isPresent());

			assertEquals(reader.getNumberOfProductOrdered("1").get().getAsInt(), 2);
			assertEquals(reader.getHistoryOfOrder("1").get().size(), 1);

			assertEquals(reader.getOrderIdsForUser("1").get().get(0), "1");

			assertEquals(reader.getTotalAmountSpentByUser("1").get().longValue(), 20600);
			assertEquals(reader.getTotalAmountSpentByUser("15").get().longValue(), 0);

			assertEquals(reader.getUsersThatPurchased("android").get().size(), 0);
			assertEquals(reader.getUsersThatPurchased("mac").get().size(), 1);
			assertEquals(reader.getUsersThatPurchased("linux").get().size(), 1);
			assertEquals(reader.getUsersThatPurchased("windows").get().size(), 1);

			assertEquals(reader.getOrderIdsThatPurchased("mac").get().get(0), 1+"");
			assertEquals(reader.getOrderIdsThatPurchased("windows").get().get(0), 2+"");
			assertEquals(reader.getOrderIdsThatPurchased("linux").get().get(0), 3+"");

			assertEquals(reader.getTotalNumberOfItemsPurchased("mac").get().getAsLong(), 2);
			assertEquals(reader.getTotalNumberOfItemsPurchased("linux").get().getAsLong(), 100);
			assertEquals(reader.getTotalNumberOfItemsPurchased("windows").get().getAsLong(), 5);
			
			assertEquals(reader.getTotalNumberOfItemsPurchased("iphone").get().getAsLong(), 0);
			assertEquals(reader.getCancelRatioForUser("1").get().getAsDouble(), 1, 0.001);
			assertEquals(reader.getModifyRatioForUser("1").get().getAsDouble(), 0, 0.001);
			assertEquals(reader.getAllItemsPurchased("1").get(), new HashMap<>());
			assertEquals(reader.getItemsPurchasedByUsers("1").get(), new HashMap<>());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
