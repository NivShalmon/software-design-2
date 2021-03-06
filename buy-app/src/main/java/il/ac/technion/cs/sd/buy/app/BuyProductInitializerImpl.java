package il.ac.technion.cs.sd.buy.app;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.google.inject.name.Named;

import library.Dict;
import library.DoubleKeyDict;

import org.w3c.dom.Node;

public class BuyProductInitializerImpl implements BuyProductInitializer {

	// temporary structures
	private Map<String, Order> tmpOrderIdToOrder = new HashMap<>();
	private Map<String, List<Integer>> tmpOrderIdToHistory = new HashMap<>();
	private Map<String, String> tmpProductIdToPrice = new HashMap<>();

	private Map<String, List<String>> tmpUserIdToOrderIds = new HashMap<>();
	private Map<String, List<String>> tmpProductIdToOrderIds = new HashMap<>();

	// actual structures

	private Dict orderIdToOrder;
	private Dict userIdToOrderIds;
	private Dict productIdToOrderIds;
	private Dict orderIdToHistory;
	private DoubleKeyDict UserProductAmount;

	@Inject
	public BuyProductInitializerImpl(@Named("orderIdToOrder") Dict orderIdToOrder, //
			@Named("userIdToOrderIds") Dict userIdToOrderIds, //
			@Named("productIdToOrderIds") Dict productIdToOrderIds, //
			@Named("orderIdToHistory") Dict orderIdToHistory, //
			@Named("userProductAmount") DoubleKeyDict userProductAmount) {
		this.orderIdToOrder = orderIdToOrder;
		this.userIdToOrderIds = userIdToOrderIds;
		this.productIdToOrderIds = productIdToOrderIds;
		this.orderIdToHistory = orderIdToHistory;
		UserProductAmount = userProductAmount;
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

		return CompletableFuture.completedFuture(null);
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
		return CompletableFuture.completedFuture(null);
	}

	private void addOrder(String order_id, String user_id, String product_id, String amount, String status) {
		tmpOrderIdToOrder.put(order_id, new Order(status, product_id, amount, user_id));

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
			if (tmpOrderIdToHistory.get(order_id).get(tmpOrderIdToHistory.get(order_id).size() - 1).equals(Integer.parseInt("-1"))) {
				tmpOrderIdToHistory.get(order_id).set(tmpOrderIdToHistory.get(order_id).size() - 1,Integer.parseInt(amount));
			}
			else{
				tmpOrderIdToHistory.get(order_id).add(Integer.parseInt(amount));
			}

		}
	}

	private void initialStructures() {

		try {
			// clear all the old orders
			Set<String> oids = tmpOrderIdToOrder.keySet();
			Set<String> temp = new HashSet<>();
			temp.addAll(oids);
			for (String oid : temp) {
				if (tmpProductIdToPrice.containsKey(tmpOrderIdToOrder.get(oid).getProduct_id())) {
					tmpOrderIdToOrder.get(oid)
							.setPrice(tmpProductIdToPrice.get(tmpOrderIdToOrder.get(oid).getProduct_id()));
				} else {
					tmpOrderIdToOrder.remove(oid);
					tmpOrderIdToHistory.remove(oid);
				}

			}

			oids = tmpOrderIdToOrder.keySet();
			for (String oid : oids) {
				String user = tmpOrderIdToOrder.get(oid).getUser_id();
				String pid = tmpOrderIdToOrder.get(oid).getProduct_id();
				if (!tmpUserIdToOrderIds.containsKey(user))
					tmpUserIdToOrderIds.put(user, new ArrayList<>());

				if (!tmpProductIdToOrderIds.containsKey(pid))
					tmpProductIdToOrderIds.put(pid, new ArrayList<>());

				tmpUserIdToOrderIds.get(user).add(oid);
				tmpProductIdToOrderIds.get(pid).add(oid);

			}

			// Add to the actual structures
			for (String oid : tmpOrderIdToOrder.keySet()) {
				orderIdToOrder.add(oid, tmpOrderIdToOrder.get(oid).toString().replaceAll("\\s+", ""));
			}
			orderIdToOrder.store().get();

			for (String user : tmpUserIdToOrderIds.keySet()) {
				userIdToOrderIds.add(user, tmpUserIdToOrderIds.get(user).toString().replaceAll("\\s+", ""));
			}
			userIdToOrderIds.store().get();

			for (String pid : tmpProductIdToOrderIds.keySet()) {
				productIdToOrderIds.add(pid, tmpProductIdToOrderIds.get(pid).toString().replaceAll("\\s+", ""));
			}
			productIdToOrderIds.store().get();

			for (String oid : tmpOrderIdToHistory.keySet()) {
				orderIdToHistory.add(oid, tmpOrderIdToHistory.get(oid).toString().replaceAll("\\s+", ""));
			}
			orderIdToHistory.store().get();

			Map<String, Map<String, String>> tmpMap = new HashMap<>();

			for (String oid : oids) {
				String user = tmpOrderIdToOrder.get(oid).getUser_id();
				String pid = tmpOrderIdToOrder.get(oid).getProduct_id();
				String amount = tmpOrderIdToOrder.get(oid).getAmount();
				if (!tmpOrderIdToOrder.get(oid).isCancelled()) {
					if (!tmpMap.containsKey(user)) {
						tmpMap.put(user, new HashMap<>());
						tmpMap.get(user).put(pid, amount);
					} else {
						if (!tmpMap.get(user).containsKey(pid)) {
							tmpMap.get(user).put(pid, amount);
						} else {
							tmpMap.get(user).put(pid,
									(Long.parseLong(tmpMap.get(user).get(pid)) + Long.parseLong(amount) + ""));
						}
					}
				}
			}

			// 1,000,000 at the worst case
			for (String user : tmpMap.keySet()) {
				for (String pid : tmpMap.get(user).keySet()) {
					UserProductAmount.add(user, pid, tmpMap.get(user).get(pid));
				}
			}

			UserProductAmount.store().get();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	// Getters for testing the initializer only !
	public Dict getOrderIdToOrder() {
		return orderIdToOrder;
	}

	public Dict getUserIdToOrderIds() {
		return userIdToOrderIds;
	}

	public Dict getProductIdToOrderIds() {
		return productIdToOrderIds;
	}

	public Dict getOrderIdToHistory() {
		return orderIdToHistory;
	}

	public DoubleKeyDict getUserProductAmount() {
		return UserProductAmount;
	}

}
