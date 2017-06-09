package il.ac.technion.cs.sd.buy.app;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import library.Dict;
import library.DoubleKeyDict;

import org.w3c.dom.Node;

@SuppressWarnings("unused")
public class BuyProductInitializerImpl implements BuyProductInitializer {

	// temporary structures
	private Map<String, Order> tmpOrderIdToOrder;

	private Map<String, List<String>> tmoUserIdToOrderIds;
	private Map<String, List<String>> tmoProductIdToOrderIds;
	private Map<String, List<Integer>> tmpOrderIdToHistory;
	private Map<String, String> tmpProductIdToPrice;

	// actual structures

	private Dict<String, Order> orderIdToOrder;
	private Dict<String, List<String>> userIdToOrderIds;
	private Dict<String, List<String>> productIdToOrderIds;
	private Dict<String, List<Integer>> orderIdToHistory;
	private DoubleKeyDict<String, String, Long> UserProductAmount;

	@Override
	public CompletableFuture<Void> setupXml(String s0) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(s0)));
			NodeList allNodes = doc.getElementsByTagName("*");

			for (int i = 0; i < allNodes.getLength(); i++) {
				Element element;
				Node n1;
				n1 = allNodes.item(i);
				element = (Element) n1;
				if (element.getNodeName().equals("Product")) {
					String id = element.getElementsByTagName("id").item(0).getTextContent();
					String price = element.getElementsByTagName("price").item(0).getTextContent();
					tmpProductIdToPrice.put(id, price);
					i += 2;
				} else if (!element.getNodeName().equals("Root")) {
					String kind = element.getNodeName();
					String user_id = null;
					String order_id = null;
					String product_id = null;
					String amount = null;
					if (kind.equals("CancelOrder")) {
						order_id = element.getElementsByTagName("order-id").item(0).getTextContent();
						if (tmpOrderIdToOrder.containsKey(order_id))
							tmpOrderIdToOrder.get(order_id).setStatus("cancel");
					}
					if (kind.equals("ModifyOrder")) {
						order_id = element.getElementsByTagName("order-id").item(0).getTextContent();
						amount = element.getElementsByTagName("new-amount").item(0).getTextContent();
						if (tmpOrderIdToOrder.containsKey(order_id)) {
							tmpOrderIdToOrder.get(order_id).setStatus("modified");
							tmpOrderIdToOrder.get(order_id).setAmount(amount);
						}
					}
					if (kind.equals("Order")) {
						order_id = element.getElementsByTagName("order-id").item(0).getTextContent();
						user_id = element.getElementsByTagName("user-id").item(0).getTextContent();
						product_id = element.getElementsByTagName("product-id").item(0).getTextContent();
						amount = element.getElementsByTagName("amount").item(0).getTextContent();
						tmpOrderIdToOrder.put(order_id, new Order(kind, product_id, amount, user_id));
						i += 4;
					}
				}
			}
			System.out.println(tmpProductIdToPrice);
			System.out.println(tmpOrderIdToOrder);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public CompletableFuture<Void> setupJson(String s0) {
		try {
			final JSONObject obj = new JSONObject(s0.replace("[", "{ \"arr\":[").replaceAll("]", "]}"));
			final JSONArray arr = obj.getJSONArray("arr");
			for (int i = 0; i < arr.length(); i++) {
				String kind = ((JSONObject) arr.get(i)).getString("type");
				String user_id = null;
				String order_id = null;
				String product_id = null;
				String amount = null;
				if (kind.equals("product")) {
					String id = ((JSONObject) arr.get(i)).getString("id");
					String price = ((JSONObject) arr.get(i)).getString("price");
					tmpProductIdToPrice.put(id, price);
				}
				if (kind.equals("cancel-order")) {
					order_id = ((JSONObject) arr.get(i)).getString("order-id");
					if (tmpOrderIdToOrder.containsKey(order_id))
						tmpOrderIdToOrder.get(order_id).setStatus("cancel");
					continue;
				}
				if (kind.equals("modify-order")) {
					order_id = ((JSONObject) arr.get(i)).getString("order-id");
					amount = ((JSONObject) arr.get(i)).getString("amount");
					if (tmpOrderIdToOrder.containsKey(order_id)) {
						tmpOrderIdToOrder.get(order_id).setStatus("modified");
						tmpOrderIdToOrder.get(order_id).setAmount(amount);
					}
				}
				if (kind.equals("order")) {
					order_id = ((JSONObject) arr.get(i)).getString("order-id");
					user_id = ((JSONObject) arr.get(i)).getString("user-id");
					product_id = ((JSONObject) arr.get(i)).getString("product-id");
					amount = ((JSONObject) arr.get(i)).getString("amount");
					tmpOrderIdToOrder.put(order_id, new Order(kind, product_id, amount, user_id));
				}
			}
			System.out.println(tmpProductIdToPrice);
			System.out.println(tmpOrderIdToOrder);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
