public class Product {
	private String productCodeNumber;
	private String name;
	private double price;
	private String description;

	public Product(String productCodeNumber, String name, double price, String description) {
		this.productCodeNumber = productCodeNumber;
		this.name = name;
		this.price = price;
		this.description = description;
	}

	public String getProductCodeNumber() {
		return productCodeNumber;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public String getDescription() {
		return description;
	}
}
