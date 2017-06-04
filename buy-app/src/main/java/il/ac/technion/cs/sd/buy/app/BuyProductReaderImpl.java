package il.ac.technion.cs.sd.buy.app;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;

public class BuyProductReaderImpl implements BuyProductReader {

	@Override
	public CompletableFuture<Boolean> isValidOrderId(String orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> isCanceledOrder(String orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> isModifiedOrder(String orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<OptionalInt> getNumberOfProductOrdered(String orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<List<Integer>> getHistoryOfOrder(String orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<List<String>> getOrderIdsForUser(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Long> getTotalAmountSpentByUser(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<List<String>> getUsersThatPurchased(String productId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<List<String>> getOrderIdsThatPurchased(String productId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<OptionalLong> getTotalNumberOfItemsPurchased(String productId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<OptionalDouble> getAverageNumberOfItemsPurchased(String productId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<OptionalDouble> getCancelRatioForUser(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<OptionalDouble> getModifyRatioForUser(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Map<String, Long>> getAllItemsPurchased(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Map<String, Long>> getItemsPurchasedByUsers(String productId) {
		// TODO Auto-generated method stub
		return null;
	}

}
