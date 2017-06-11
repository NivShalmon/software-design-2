package il.ac.technion.cs.sd.buy.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import library.Dict;
import library.DoubleKeyDict;

public class BuyProductReaderImpl implements BuyProductReader {

	private Dict orderIdToOrder;
	private Dict userIdToOrderIds;
	private Dict productIdToOrderIds;
	private Dict orderIdToHistory;
	private DoubleKeyDict userProductAmount;

	@Inject
	public BuyProductReaderImpl(@Named("orderIdToOrder") Dict orderIdToOrder, //
			@Named("userIdToOrderIds") Dict userIdToOrderIds, //
			@Named("productIdToOrderIds") Dict productIdToOrderIds, //
			@Named("orderIdToHistory") Dict orderIdToHistory, //
			@Named("userProductAmount") DoubleKeyDict userProductAmount) {
		this.orderIdToOrder = orderIdToOrder;
		this.userIdToOrderIds = userIdToOrderIds;
		this.productIdToOrderIds = productIdToOrderIds;
		this.orderIdToHistory = orderIdToHistory;
		this.userProductAmount = userProductAmount;
	}

	@Override
	public CompletableFuture<Boolean> isValidOrderId(String s0) {
		try {
			return orderIdToOrder.find(s0).thenApply(o -> o.isPresent());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(false);
	}

	@Override
	public CompletableFuture<Boolean> isCanceledOrder(String s0) {
		try {
			return orderIdToOrder.find(s0).thenApply(o -> o.isPresent() && Order.decodeOrder(o.get()).isCancelled());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(false);
	}

	@Override
	public CompletableFuture<Boolean> isModifiedOrder(String s0) {
		try {
			return orderIdToOrder.find(s0).thenApply(o -> o.isPresent()
					&& (Order.decodeOrder(o.get()).isModified() || Order.decodeOrder(o.get()).isCancelled()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(false);
	}

	@Override
	public CompletableFuture<OptionalInt> getNumberOfProductOrdered(String s0) {
		return orderIdToOrder.find(s0)
				.thenApply(o -> !o.isPresent() ? OptionalInt.empty()
						: Order.decodeOrder(o.get()).isCancelled()
								? OptionalInt.of(-1 * Integer.parseInt(Order.decodeOrder(o.get()).getAmount()))
								: OptionalInt.of(Integer.parseInt(Order.decodeOrder(o.get()).getAmount())));
	}

	@Override
	public CompletableFuture<List<Integer>> getHistoryOfOrder(String s0) {
		return orderIdToHistory.find(s0)
				.thenApply(o -> !o.isPresent() ? new ArrayList<Integer>() : decodeListIntegers(o.get()));
	}

	@Override
	public CompletableFuture<List<String>> getOrderIdsForUser(String s0) {
		return userIdToOrderIds.find(s0).thenApply(lst -> !lst.isPresent() ? new ArrayList<String>()
				: decodeListStrings(lst.get()).stream().sorted().collect(Collectors.toList()));
	}

	@Override
	public CompletableFuture<Long> getTotalAmountSpentByUser(String s0) {

		return userIdToOrderIds.find(s0).thenCompose(orderIds -> {
			CompletableFuture<List<Order>> orders = getOrders(decodeListStringsOptional(orderIds));
			return orders.thenApply(ordersLst -> ordersLst.stream().filter(order -> !order.isCancelled())
					.mapToLong(order -> Long.parseLong(order.getAmount()) * Long.parseLong(order.getPrice())).sum());
		});

	}

	static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> com) {
		return CompletableFuture.allOf(com.toArray(new CompletableFuture[com.size()]))
				.thenApply(v -> com.stream().map(CompletableFuture::join).collect(Collectors.toList()));
	}

	// list of users that purchased this product
	@Override
	public CompletableFuture<List<String>> getUsersThatPurchased(String s0) {
		return productIdToOrderIds.find(s0).thenCompose(orderIds -> {
			CompletableFuture<List<Order>> orders = getOrders(decodeListStringsOptional(orderIds));
			return orders.thenApply(lst -> lst.stream().filter(order -> !order.isCancelled())
					.map(order -> order.getUser_id()).distinct().sorted().collect(Collectors.toList()));
		});
	}

	public CompletableFuture<Integer> getUsersThatPurchasedAndNotCanecelled(String s0) {
		return productIdToOrderIds.find(s0).thenCompose(orderIds -> {
			CompletableFuture<List<Order>> orders = getOrders(decodeListStringsOptional(orderIds));
			return orders.thenApply(
					lst -> lst.stream().filter(order -> !order.isCancelled()).collect(Collectors.toList()).size());
		});
	}

	@Override
	public CompletableFuture<List<String>> getOrderIdsThatPurchased(String s0) {
		return productIdToOrderIds.find(s0).thenApply(orderIds -> !orderIds.isPresent() ? new ArrayList<String>()
				: decodeListStrings(orderIds.get()).stream().sorted().collect(Collectors.toList()));
	}

	// items that were purchased by a given product id
	@Override
	public CompletableFuture<OptionalLong> getTotalNumberOfItemsPurchased(String s0) {

		return productIdToOrderIds.find(s0).thenCompose(orderIds -> {
			CompletableFuture<List<Order>> orders = getOrders(decodeListStringsOptional(orderIds));
			return orders
					.thenApply(ordersLst -> OptionalLong.of(ordersLst.stream().filter(order -> !order.isCancelled())
							.mapToLong(order -> Long.parseLong(order.getAmount())).sum()));
		});

	}

	@Override
	public CompletableFuture<OptionalDouble> getAverageNumberOfItemsPurchased(String s0) {
		CompletableFuture<OptionalLong> total = getTotalNumberOfItemsPurchased(s0);
		CompletableFuture<Integer> size = getUsersThatPurchasedAndNotCanecelled(s0);
		return total.thenCombine(size, (t, s) -> !t.isPresent() ? OptionalDouble.empty()
				: OptionalDouble.of(s == 0 ? 0 : (double) t.getAsLong() / s));
	}

	@Override
	public CompletableFuture<OptionalDouble> getCancelRatioForUser(String s0) {

		return userIdToOrderIds.find(s0).thenCompose(orderIds -> {
			CompletableFuture<List<Order>> orders = getOrders(decodeListStringsOptional(orderIds));
			CompletableFuture<Integer> total = orders.thenApply(lst -> lst.size());
			CompletableFuture<Integer> cancelled = orders
					.thenApply(o -> o.stream().filter(i -> i.isCancelled()).collect(Collectors.toList()).size());
			return total.thenCombine(cancelled, (t, c) -> OptionalDouble.of(t == 0 || c == 0 ? 0 : (double) c / t));
		});

	}

	private CompletableFuture<List<Order>> getOrders(Optional<List<String>> orderIds) {
		List<CompletableFuture<Optional<Order>>> tmpOrders = orderIds.orElse(new ArrayList<>()).stream()
				.map(i -> Order.decodeOrder(orderIdToOrder.find(i))).collect(Collectors.toList());
		return sequence(tmpOrders)
				.thenApply(l -> l.stream().filter(o -> o.isPresent()).map(o -> o.get()).collect(Collectors.toList()));
	}

	@Override
	public CompletableFuture<OptionalDouble> getModifyRatioForUser(String s0) {
		return userIdToOrderIds.find(s0).thenCompose(orderIds -> {
			CompletableFuture<List<Order>> orders = getOrders(decodeListStringsOptional(orderIds));
			CompletableFuture<Integer> total = orders.thenApply(lst -> lst.size());
			CompletableFuture<Integer> modified = orders
					.thenApply(o -> o.stream().filter(i -> i.isModified()).collect(Collectors.toList()).size());
			return total.thenCombine(modified, (t, c) -> OptionalDouble.of(t == 0 || c == 0 ? 0 : (double) c / t));
		});
	}

	@Override
	public CompletableFuture<Map<String, Long>> getAllItemsPurchased(String s0) {
		return convertMap(userProductAmount.findByMainKey(s0));
	}

	@Override
	public CompletableFuture<Map<String, Long>> getItemsPurchasedByUsers(String s0) {
		return convertMap(userProductAmount.findBySecondaryKey(s0));
	}

	private static CompletableFuture<Map<String, Long>> convertMap(CompletableFuture<Map<String, String>> x) {
		return x.thenApply(m -> {
			if (m == null || m.isEmpty())
				return new HashMap<>();
			Map<String, Long> ret = new HashMap<>();
			for (String k : m.keySet()) {
				ret.put(k, Long.parseLong(m.get(k)));
			}
			return ret;
		});
	}

	private static List<String> decodeListStrings(String encoded) {
		return Arrays.asList((encoded.substring(1).substring(0, encoded.substring(1).length() - 1).split(",")));
	}

	private static Optional<List<String>> decodeListStringsOptional(Optional<String> encoded) {
		return !encoded.isPresent() ? Optional.of(new ArrayList<>())
				: Optional.of(Arrays.asList(
						(encoded.get().substring(1).substring(0, encoded.get().substring(1).length() - 1).split(","))));
	}

	private static List<Integer> decodeListIntegers(String encoded) {
		return Arrays.asList((encoded.substring(1).substring(0, encoded.substring(1).length() - 1).split(","))).stream()
				.map(s -> Integer.parseInt(s)).collect(Collectors.toList());
	}

}
