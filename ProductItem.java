public class ProductItem {
	private Product product;
	private int quantity;

	public ProductItem(Product product, int quantity) {
		this.product = product;
		this.quantity = quantity;
	}

	public void addQuantity(int quantity) {
		this.quantity += quantity;
	}

	public void removeQuantity(int quantity) {
		this.quantity -= quantity;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public Product getProduct() {
		return this.product;
	}
}
