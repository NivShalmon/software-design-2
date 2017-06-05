package il.ac.technion.cs.sd.buy.app;

public class Order {
	@Override
	public String toString() {
		return "Order [kind=" + kind + ", product_id=" + product_id + ", amount=" + amount + ", user_id=" + user_id
				+ "]";
	}

	private String kind;
	private String product_id;
	private String amount;
	private String user_id;

	public Order(String kind, String product_id, String amount, String user_id) {
		this.kind = kind;
		this.product_id = product_id;
		this.amount = amount;
		this.user_id = user_id;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
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
		return "cancel".equals(this.kind);
	}

	public boolean isModified() {
		return "modified".equals(this.kind);
	}

}
