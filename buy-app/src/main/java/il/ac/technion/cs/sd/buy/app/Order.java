package il.ac.technion.cs.sd.buy.app;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Order {
	@Override
	public String toString() {
		return status + ":" + product_id + ":" + amount + ":" + user_id + ":" + price + ":" + was_modified;
	}

	private String status;
	private String product_id;
	private String amount;
	private String user_id;
	private String price;

	private boolean was_modified;

	public static Order decodeOrder(String decoded) {
		String[] arr = decoded.split(":");
		return new Order(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
	}

	public static CompletableFuture<Optional<Order>> decodeOrder(CompletableFuture<Optional<String>> find) {
		return find.thenApply(decoded -> {
			String[] arr = decoded.get().split(":");
			return Optional.of(new Order(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]));
		});
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Order(String status, String product_id, String amount, String user_id) {
		this.status = status;
		this.product_id = product_id;
		this.amount = amount;
		this.user_id = user_id;
		this.was_modified = false;
	}

	public Order(String status, String product_id, String amount, String user_id, String price, String was_modified) {
		this.status = status;
		this.product_id = product_id;
		this.amount = amount;
		this.user_id = user_id;
		this.price = price;
		this.was_modified = Boolean.parseBoolean(was_modified);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String kind) {
		this.status = kind;
		if (kind.equals("modified"))
			this.was_modified = true;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public boolean isCancelled() {
		return "cancel".equals(this.status);
	}

	public boolean isModified() {
		return "modified".equals(this.status) || this.was_modified;
	}

}
