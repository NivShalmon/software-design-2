package il.ac.technion.cs.sd.buy.app;

import java.io.StringReader;
import java.util.ArrayList;
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

import com.google.inject.Inject;

import library.Dict;
import library.DoubleKeyDict;

import org.w3c.dom.Node;

public class BuyProductInitializerImpl implements BuyProductInitializer {

	// temporary structures
	private Map<String, Order> tmpOrderIdToOrder;
	private Map<String, List<String>> tmpUserIdToOrderIds;
	private Map<String, List<String>> tmpProductIdToOrderIds;
	private Map<String, List<Integer>> tmpOrderIdToHistory;
	private Map<String, String> tmpProductIdToPrice;

	// actual structures

	private Dict<String, Order> orderIdToOrder;
	private Dict<String, List<String>> userIdToOrderIds;
	private Dict<String, List<String>> productIdToOrderIds;
	private Dict<String, List<Integer>> orderIdToHistory;
	private DoubleKeyDict<String, String, Long> UserProductAmount;

	@Inject
	public BuyProductInitializerImpl(Dict<String, Order> orderIdToOrder, Dict<String, List<String>> userIdToOrderIds,
			Dict<String, List<String>> productIdToOrderIds, Dict<String, List<Integer>> orderIdToHistory,
			DoubleKeyDict<String, String, Long> userProductAmount) {
		this();

		this.orderIdToOrder = orderIdToOrder;
		this.userIdToOrderIds = userIdToOrderIds;
		this.productIdToOrderIds = productIdToOrderIds;
		this.orderIdToHistory = orderIdToHistory;
		UserProductAmount = userProductAmount;

	}

	public BuyProductInitializerImpl() {
		tmpOrderIdToOrder = new HashMap<>();
		tmpUserIdToOrderIds = new HashMap<>();
		tmpProductIdToOrderIds = new HashMap<>();
		tmpOrderIdToHistory = new HashMap<>();
		tmpProductIdToPrice = new HashMap<>();
	}

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
					String status = element.getNodeName();
					String user_id = null;
					String order_id = null;
					String product_id = null;
					String amount = null;
					if (status.equals("CancelOrder")) {
						order_id = element.getElementsByTagName("order-id").item(0).getTextContent();
						CancellOrder(order_id);

					}
					if (status.equals("ModifyOrder")) {
						order_id = element.getElementsByTagName("order-id").item(0).getTextContent();
						amount = element.getElementsByTagName("new-amount").item(0).getTextContent();
						ModifyOrder(order_id, amount);

					}
					if (status.equals("Order")) {
						order_id = element.getElementsByTagName("order-id").item(0).getTextContent();
						user_id = element.getElementsByTagName("user-id").item(0).getTextContent();
						product_id = element.getElementsByTagName("product-id").item(0).getTextContent();
						amount = element.getElementsByTagName("amount").item(0).getTextContent();
						addOrder(order_id, user_id, product_id, amount, status);
						i += 4;
					}
				}
			}
			initialStructures();

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
				String status = ((JSONObject) arr.get(i)).getString("type");
				String user_id = null;
				String order_id = null;
				String product_id = null;
				String amount = null;
				if (status.equals("product")) {
					String id = ((JSONObject) arr.get(i)).getString("id");
					String price = ((JSONObject) arr.get(i)).getString("price");
					tmpProductIdToPrice.put(id, price);
				}
				if (status.equals("cancel-order")) {
					order_id = ((JSONObject) arr.get(i)).getString("order-id");
					CancellOrder(order_id);
				}
				if (status.equals("modify-order")) {
					order_id = ((JSONObject) arr.get(i)).getString("order-id");
					amount = ((JSONObject) arr.get(i)).getString("amount");
					ModifyOrder(order_id, amount);

				}
				if (status.equals("order")) {
					order_id = ((JSONObject) arr.get(i)).getString("order-id");
					user_id = ((JSONObject) arr.get(i)).getString("user-id");
					product_id = ((JSONObject) arr.get(i)).getString("product-id");
					amount = ((JSONObject) arr.get(i)).getString("amount");
					addOrder(order_id, user_id, product_id, amount, status);
				}
			}

			initialStructures();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void addOrder(String order_id, String user_id, String product_id, String amount, String status) {
		tmpOrderIdToOrder.put(order_id, new Order(status, product_id, amount, user_id));
		if (tmpUserIdToOrderIds.get(user_id) == null)
			tmpUserIdToOrderIds.put(user_id, new ArrayList<>());
		tmpUserIdToOrderIds.get(user_id).add(order_id);
		tmpOrderIdToHistory.put(order_id, new ArrayList<>());
		tmpOrderIdToHistory.get(order_id).add(Integer.parseInt(amount));
	}

	private void CancellOrder(String order_id) {
		if (tmpOrderIdToOrder.containsKey(order_id) && !tmpOrderIdToOrder.get(order_id).isCancelled()) {
			tmpOrderIdToOrder.get(order_id).setStatus("cancel");
			tmpOrderIdToHistory.get(order_id).add(-1);
		}
	}

	private void ModifyOrder(String order_id, String amount) {
		if (tmpOrderIdToOrder.containsKey(order_id)) {
			tmpOrderIdToOrder.get(order_id).setStatus("modified");
			tmpOrderIdToOrder.get(order_id).setAmount(amount);
			tmpOrderIdToHistory.get(order_id).add(Integer.parseInt(amount));

		}
	}

	private void initialStructures() {

		System.out.println("ProductIdToPrice:");
		System.out.println(tmpProductIdToPrice);
		System.out.println("OrderIdToOrder:");
		System.out.println(tmpOrderIdToOrder);
		System.out.println("History:");
		System.out.println(tmpOrderIdToHistory);

		System.out.println("___________");

		orderIdToOrder.addAll(tmpOrderIdToOrder);
		userIdToOrderIds.addAll(tmpUserIdToOrderIds);
		productIdToOrderIds.addAll(tmpProductIdToOrderIds);
		orderIdToHistory.addAll(tmpOrderIdToHistory);

		// TODO : dor UserProductAmount.;

		orderIdToOrder.store();
		userIdToOrderIds.store();
		productIdToOrderIds.store();
		orderIdToHistory.store();

	}

	// Getters for testing the initializer only !
	public Dict<String, Order> getOrderIdToOrder() {
		return orderIdToOrder;
	}

	public Dict<String, List<String>> getUserIdToOrderIds() {
		return userIdToOrderIds;
	}

	public Dict<String, List<String>> getProductIdToOrderIds() {
		return productIdToOrderIds;
	}

	public Dict<String, List<Integer>> getOrderIdToHistory() {
		return orderIdToHistory;
	}

	public DoubleKeyDict<String, String, Long> getUserProductAmount() {
		return UserProductAmount;
	}

}
