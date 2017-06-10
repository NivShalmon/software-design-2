package il.ac.technion.cs.sd.buy.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.OptionalInt;
import java.util.Scanner;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl;
import il.ac.technion.cs.sd.buy.app.BuyProductReader;
import il.ac.technion.cs.sd.buy.app.BuyProductReaderImpl;
import il.ac.technion.cs.sd.buy.app.Order;
import library.Dict;
import library.DoubleKeyDict;

public class BuyProductReaderInitializationTest {

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
//			assert(!reader.getTotalNumberOfItemsPurchased("android").get().isPresent());
//			assert(!reader.getTotalNumberOfItemsPurchased("iphone").get().isPresent());
			

			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
