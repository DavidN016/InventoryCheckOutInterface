import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.HashSet;

public class DataModel {
	private List<Product> products;
	private List<ProductItem> productItems;
	private String storeName;
	private String phoneNumber;
	private String city;
	private String state;
	private String zipCode;
	private double cityTaxPercentage;
	private boolean inventoryLoaded;
	private Set<InvoiceObserver> observers;


	public DataModel() {
		this.productItems = new ArrayList<>();
		this.products = new ArrayList<>();
		this.inventoryLoaded = false;
		this.observers = new HashSet<>();
		loadStoreInfo();
		System.out.println("Products loaded: " + products.size());
		for (Product product : products) {
			System.out.println(product.getProductCodeNumber() + " " + product.getName() + " " + product.getPrice() + " "
					+ product.getDescription());
		}
	}

	public boolean isInventoryLoaded() {
		return inventoryLoaded;
	}

	public String getStoreName() {
		return storeName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public double getCityTaxPercentage() {
		return cityTaxPercentage;
	}

	public List<Product> getProducts() {
		return products;
	}

	public List<ProductItem> getProductItems() {
		return productItems;
	}

	public void clearProductItems() {
		productItems.clear();
		notifyObservers();
	}

	private void loadStoreInfo() {
		try {
			String jsonContent = readFileContent("/data.json");
			parseStoreInfo(jsonContent);
		} catch (IOException e) {
			System.err.println("Error loading store info: " + e.getMessage());
		}
	}

	private void parseStoreInfo(String jsonContent) {
		// Extract store info section
		int storeInfoStart = jsonContent.indexOf("\"store_info\"");
		if (storeInfoStart == -1)
			return;

		// Find the object boundaries
		int objectStart = jsonContent.indexOf("{", storeInfoStart);
		int objectEnd = jsonContent.indexOf("}", objectStart);
		String storeObject = jsonContent.substring(objectStart, objectEnd + 1);

		// Parse individual fields
		this.storeName = extractValue(storeObject, "store_name");
		this.phoneNumber = extractValue(storeObject, "phone_number");
		this.city = extractValue(storeObject, "city");
		this.state = extractValue(storeObject, "state");
		this.zipCode = extractValue(storeObject, "zip_code");

		// Parse tax percentage with special handling for numeric value
		String taxStr = extractValue(storeObject, "city_tax_percentage").trim();
		this.cityTaxPercentage = taxStr.isEmpty() ? 0.0 : Double.parseDouble(taxStr);

		System.out.println("Store info loaded: " + storeName + " " + phoneNumber + " " + city + " " + state + " "
				+ zipCode + " " + cityTaxPercentage);
	}

	public void loadInventory() {
		products.clear();
		try {
			String jsonContent = readFileContent("/data.json");
			parseProducts(jsonContent);
			System.out.println("Products loaded: " + products.size());
			inventoryLoaded = true;
		} catch (IOException e) {
			System.err.println("Error loading products: " + e.getMessage());
		}
	}

	private String readFileContent(String filePath) throws IOException {
		StringBuilder content = new StringBuilder();
		try (InputStream inputStream = getClass().getResourceAsStream(filePath);
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
		}
		return content.toString();
	}

	private void parseProducts(String jsonContent) {
		int productInfoStart = jsonContent.indexOf("\"product_info\"");
		if (productInfoStart == -1)
			return;

		int arrayStart = jsonContent.indexOf("[", productInfoStart);
		int arrayEnd = jsonContent.lastIndexOf("]");

		String productsArray = jsonContent.substring(arrayStart + 1, arrayEnd);
		String[] productObjects = productsArray.split("\\},\\s*\\{");

		for (String productObj : productObjects) {
			// Clean up the first and last product
			productObj = productObj.replaceAll("^\\s*\\{", "").replaceAll("\\}\\s*$", "");

			// Parse product properties
			String code = extractValue(productObj, "product_code");
			String name = extractValue(productObj, "product_name");
			String priceStr = extractValue(productObj, "price");
			double price = Double.parseDouble(priceStr);
			String description = extractValue(productObj, "description");

			Product product = new Product(code, name, price, description);
			products.add(product);
		}
	}

	private String extractValue(String jsonObject, String key) {
		String keyPattern = "\"" + key + "\":\\s*\"?([^,\"]+)\"?";
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(keyPattern);
		java.util.regex.Matcher matcher = pattern.matcher(jsonObject);

		if (matcher.find()) {
			// Clean up any trailing non-numeric characters except decimal point
			String value = matcher.group(1).trim();
			if (key.equals("city_tax_percentage")) {
				return value.replaceAll("[^0-9.].*$", "");
			}
			return value;
		}
		return "";
	}

	public void startShift(String firstName, String lastName) {
		// TODO: Implement shift start logic
		// Could store the shift start time and employee info
		System.out.println("Starting shift for " + firstName + " " + lastName);
	}

	public void endShift() {
		// TODO: Implement shift end logic
		// Could store the shift end time and calculate duration
		System.out.println("Ending shift");
	}

	public String getFilteredProducts(String prefix) {
		StringBuilder result = new StringBuilder();
		for (Product product : products) {
			if (product.getProductCodeNumber().startsWith(prefix)) {
				result.append(String.format("%s - %s - $%.2f - %s%n",
						product.getProductCodeNumber(),
						product.getName(),
						product.getPrice(),
						product.getDescription()));
			}
		}
		return result.toString();
	}

	public String getAllProducts() {
		StringBuilder result = new StringBuilder();
		for (Product product : products) {
			result.append(String.format("%s - %s - $%.2f - %s%n",
					product.getProductCodeNumber(),
					product.getName(),
					product.getPrice(),
					product.getDescription()));
		}
		return result.toString();
	}

	public boolean isValidProduct(String productCode) {
		for (Product product : products) {
			if (product.getProductCodeNumber().equals(productCode)) {
				return true;
			}
		}
		return false;
	}

	public boolean isValidQuantity(String quantity) {
		try {
			int qty = Integer.parseInt(quantity);
			return qty > 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public boolean addProductToInvoice(String productCode, int quantity) {
		for (Product product : products) {
			if (product.getProductCodeNumber().equals(productCode)) {
				productItems.add(new ProductItem(product, quantity));
				notifyObservers();
				return true;
			}
		}
		return false;
	}

	public boolean removeProductFromInvoice(String productCode, int quantity) {
		for (ProductItem item : productItems) {
			if (item.getProduct().getProductCodeNumber().equals(productCode)) {
				if (quantity > item.getQuantity()) {
					return false; 
				}

				item.removeQuantity(quantity);

				if (item.getQuantity() == 0) {
					productItems.remove(item);
				}

				notifyObservers();
				return true;
			}
		}
		return false;
	}

	public void addObserver(InvoiceObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(InvoiceObserver observer) {
		observers.remove(observer);
	}

	private void notifyObservers() {
		for (InvoiceObserver observer : observers) {
			observer.onInvoiceUpdated();
		}
	}

}
