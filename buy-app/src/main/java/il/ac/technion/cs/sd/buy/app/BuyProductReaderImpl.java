package il.ac.technion.cs.sd.buy.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;

import library.Dict;

public class BuyProductReaderImpl implements BuyProductReader {

	private Dict<String, Order> orderIdToOrder; // map orderId ----> Order
	private Dict<String, List<String>> userIdToOrderIdsList; // map userId ---->
																// Orders Ids
	private Dict<String, List<String>> productIdToOrderIdsList; // map productId
																// ----> Orders
																// Ids

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
		// TODO Dor Add & Think about that
		return null;
	}

	@Override
	public CompletableFuture<List<String>> getOrderIdsForUser(String s0) {
		return userIdToOrderIdsList.find(s0).thenApply(lst -> lst.isPresent() ? new ArrayList<String>() : lst.get());
	}

	@Override
	public CompletableFuture<Long> getTotalAmountSpentByUser(String s0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<List<String>> getUsersThatPurchased(String s0) {
		// TODO Auto-generated method stub
		return null;
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
