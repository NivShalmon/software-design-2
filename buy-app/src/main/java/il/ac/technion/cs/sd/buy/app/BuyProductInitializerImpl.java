package il.ac.technion.cs.sd.buy.app;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import library.Pair;
import library.Triple;

import org.w3c.dom.Node;

public class BuyProductInitializerImpl implements BuyProductInitializer {

	Map<String, String> products = new HashMap<>(); // key: name value: price
	Map<String, Order> reservations = new HashMap<>(); // key: order id:
														// kind, name,
														// product id

	@Override
	public CompletableFuture<Void> setupXml(String s0) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(s0)));
			NodeList allNodes = doc.getElementsByTagName("*"); // a list
																// containing
																// all the nodes
			for (int i = 0; i < allNodes.getLength(); i++) {
				Element element;
				Node n1;
				n1 = allNodes.item(i);
				element = (Element) n1;
				if (element.getNodeName().equals("Product")) {
					String id = element.getElementsByTagName("id").item(0).getTextContent();
					String price = element.getElementsByTagName("price").item(0).getTextContent();
					products.put(id, price);
					i += 2;
				} else if (!element.getNodeName().equals("Root")) {
					String kind = element.getNodeName();
					String user_id = null;
					String order_id = null;
					String product_id = null;
					String amount = null;
					if (kind.equals("CancelOrder")) {
						order_id = element.getElementsByTagName("order-id").item(0).getTextContent();
						if (reservations.containsKey(order_id))
							reservations.get(order_id).setKind("cancel");
					}
					if (kind.equals("ModifyOrder")) {
						order_id = element.getElementsByTagName("order-id").item(0).getTextContent();
						amount = element.getElementsByTagName("new-amount").item(0).getTextContent();
						if (reservations.containsKey(order_id)) {
							reservations.get(order_id).setKind("modified");
							reservations.get(order_id).setAmount(amount);
						}
					}
					if (kind.equals("Order")) {
						order_id = element.getElementsByTagName("order-id").item(0).getTextContent();
						user_id = element.getElementsByTagName("user-id").item(0).getTextContent();
						product_id = element.getElementsByTagName("product-id").item(0).getTextContent();
						amount = element.getElementsByTagName("amount").item(0).getTextContent();
						reservations.put(order_id, new Order(kind, product_id, amount, user_id));
						i += 4;
					}
				}
			}
			System.out.println(products);
			System.out.println(reservations);
		} catch (Exception ¢) {
			¢.printStackTrace();
		}

		return null;
	}

	@Override
	public CompletableFuture<Void> setupJson(String s0) {
		return null;
	}

}
