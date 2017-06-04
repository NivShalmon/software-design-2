package il.ac.technion.cs.sd.sd.buy.test;

import static org.junit.Assert.*;

import org.junit.Test;

import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl;

public class InitializerTest {

	@Test
	public void test() {
		BuyProductInitializer init = new BuyProductInitializerImpl();
		init.setupXml("<Root>" + "<Product>" + "<id>iphone</id>" + "<price>1000</price>" + "</Product>" + "<Order>"
				+ "<user-id>1</user-id>" + "<order-id>1</order-id>" + "<product-id>android</product-id>"
				+ "<amount>5</amount>" + "</Order>" + "<ModifyOrder>" + "<order-id>1</order-id>"
				+ "<new-amount>10</new-amount>" + "</ModifyOrder>" + "<CancelOrder>" + "<order-id>1</order-id>"
				+ "</CancelOrder>" + "<Product>" + "<id>android</id>" + "<price>500</price>" + "</Product>"
				+ "</Root>");

	}

}
