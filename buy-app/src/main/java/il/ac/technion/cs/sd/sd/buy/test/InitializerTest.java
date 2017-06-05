package il.ac.technion.cs.sd.sd.buy.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.Test;

import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl;

public class InitializerTest {

	@Test
	public void testXML0() {
		Scanner scanner;
		try {
			scanner = new Scanner(new File("../buy-test/src/test/resources/il/ac/technion/cs/sd/buy/test/small.xml"));
			String text = scanner.useDelimiter("\\A").next();
			scanner.close();
			BuyProductInitializer init = new BuyProductInitializerImpl();
			init.setupXml(text);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testJson0() {
		Scanner scanner;
		try {
			scanner = new Scanner(new File("../buy-test/src/test/resources/il/ac/technion/cs/sd/buy/test/small_2.json"));
			String text = scanner.useDelimiter("\\A").next();
			scanner.close();
			BuyProductInitializer init = new BuyProductInitializerImpl();
			init.setupJson(text);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
