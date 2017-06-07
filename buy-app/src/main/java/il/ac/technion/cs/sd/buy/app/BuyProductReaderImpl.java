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

import library.Dict;

public class BuyProductReaderImpl implements BuyProductReader {

	@SuppressWarnings("unused")
	private Dict<String, String> productIdToPrice;
	private Dict<String, Order> orderIdToOrder;
	private Dict<String, List<String>> userIdToOrderIdsList;
	@SuppressWarnings("unused")
	private Dict<String, List<String>> productIdToOrderIdsList;
	private Dict<String, List<Integer>> orderIdToHistory;

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
		return userIdToOrderIdsList.find(s0).thenApply(lst -> lst.isPresent() ? new ArrayList<String>() : lst.get());
	}

	@Override
	public CompletableFuture<Long> getTotalAmountSpentByUser(String s0) {
		return null;
		// userIdToOrderIdsList.find(s0).thenApply(lst ->
		// !lst.isPresent() ? Long.parseLong("0") :lst.get().stream().mapToLong(
		// s -> orderIdToOrder.find(s).thenApply(o ->
		// o.get().isCancelled() ? Long.parseLong("0"):
		// Long.parseLong(o.get().getAmount())*
		// (productIdToProce.find(s).thenAccept(p -> !p.isPresent() ?
		// Long.parseLong("0"): Long.parseLong(p.get())).get()))).sum());
		//
	}

	// static <T> CompletableFuture<Optional<List<T>>>
	// sequence(CompletableFuture<Optional<List<T>>> com) {
	// return CompletableFuture.allOf(com.toArray(new
	// CompletableFuture[com.size()]))
	// .thenApply(v ->
	// com.stream().map(CompletableFuture::join).collect(Collectors.toList()));
	// }

	@Override
	public CompletableFuture<List<String>> getUsersThatPurchased(String s0) {
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
