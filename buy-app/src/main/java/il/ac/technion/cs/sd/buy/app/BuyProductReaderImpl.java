package il.ac.technion.cs.sd.buy.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import library.Dict;

public class BuyProductReaderImpl implements BuyProductReader {

	// private Dict<String, String> productIdToPrice;
	private Dict<String, Order> orderIdToOrder;
	private Dict<String, List<String>> userIdToOrderIds;
	private Dict<String, List<String>> productIdToOrderIds;
	private Dict<String, List<Integer>> orderIdToHistory;

	@Inject
	public BuyProductReaderImpl(Dict<String, String> productIdToPrice, Dict<String, Order> orderIdToOrder,
			Dict<String, List<String>> userIdToOrderIds, Dict<String, List<String>> productIdToOrderIds,
			Dict<String, List<Integer>> orderIdToHistory) {
		super();
		// this.productIdToPrice = productIdToPrice;
		this.orderIdToOrder = orderIdToOrder;
		this.userIdToOrderIds = userIdToOrderIds;
		this.productIdToOrderIds = productIdToOrderIds;
		this.orderIdToHistory = orderIdToHistory;
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
			return orderIdToOrder.find(s0).thenApply(o -> o.isPresent() && o.get().isCancelled());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(false);
	}

	@Override
	public CompletableFuture<Boolean> isModifiedOrder(String s0) {
		try {
			return orderIdToOrder.find(s0).thenApply(o -> o.isPresent() && o.get().isModified());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(false);
	}

	@Override
	public CompletableFuture<OptionalInt> getNumberOfProductOrdered(String s0) {
		return orderIdToOrder.find(s0)
				.thenApply(o -> !o.isPresent() ? OptionalInt.empty()
						: o.get().isCancelled() ? OptionalInt.of(-1 * Integer.parseInt(o.get().getAmount()))
								: OptionalInt.of(Integer.parseInt(o.get().getAmount())));
	}

	@Override
	public CompletableFuture<List<Integer>> getHistoryOfOrder(String s0) {
		return orderIdToHistory.find(s0).thenApply(o -> !o.isPresent() ? new ArrayList<Integer>() : o.get());
	}

	@Override
	public CompletableFuture<List<String>> getOrderIdsForUser(String s0) {
		return userIdToOrderIds.find(s0).thenApply(lst -> lst.isPresent() ? new ArrayList<String>() : lst.get());
	}

	@Override
	public CompletableFuture<Long> getTotalAmountSpentByUser(String s0) {

		return userIdToOrderIds.find(s0).thenCompose(orderIds -> {
			List<CompletableFuture<Optional<Order>>> tmpOrders = orderIds.orElse(new ArrayList<>()).stream()
					.map(i -> orderIdToOrder.find(i)).collect(Collectors.toList());
			CompletableFuture<List<Order>> orders = sequence(tmpOrders).thenApply(
					l -> l.stream().filter(o -> o.isPresent()).map(o -> o.get()).collect(Collectors.toList()));
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
			List<CompletableFuture<Optional<Order>>> tmpOrders = orderIds.orElse(new ArrayList<>()).stream()
					.map(i -> orderIdToOrder.find(i)).collect(Collectors.toList());
			CompletableFuture<List<Order>> orders = sequence(tmpOrders).thenApply(
					l -> l.stream().filter(o -> o.isPresent()).map(o -> o.get()).collect(Collectors.toList()));
			return orders.thenApply(lst -> lst.stream().filter(order -> !order.isCancelled())
					.map(order -> order.getUser_id()).distinct().collect(Collectors.toList()));
		});
	}

	@Override
	public CompletableFuture<List<String>> getOrderIdsThatPurchased(String s0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<OptionalLong> getTotalNumberOfItemsPurchased(String s0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<OptionalDouble> getAverageNumberOfItemsPurchased(String s0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<OptionalDouble> getCancelRatioForUser(String s0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<OptionalDouble> getModifyRatioForUser(String s0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Map<String, Long>> getAllItemsPurchased(String s0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Map<String, Long>> getItemsPurchasedByUsers(String s0) {
		// TODO Auto-generated method stub
		return null;
	}

}
