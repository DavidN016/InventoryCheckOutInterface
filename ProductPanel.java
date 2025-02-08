import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ProductPanel extends JPanel {
	private JLabel productCodeLabel;
	private JTextField productCodeField;
	private JLabel quantityLabel;
	private JTextField quantityField;
	private JButton addButton;
	private JButton removeButton;
	private JButton showButton;
	private DataModel model;

	public ProductPanel(DataModel model) {
		this.model = model;
		setLayout(new BorderLayout(10, 10));

		// Input Panel
		JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));

		// Product Code
		productCodeLabel = new JLabel("Product Code:", SwingConstants.RIGHT);
		productCodeField = new JTextField(15);
		inputPanel.add(productCodeLabel);
		inputPanel.add(productCodeField);

		// Quantity
		quantityLabel = new JLabel("Quantity:", SwingConstants.RIGHT);
		quantityField = new JTextField(15);
		inputPanel.add(quantityLabel);
		inputPanel.add(quantityField);

		// Button Panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		addButton = new JButton("Add Item");
		removeButton = new JButton("Remove Item");
		showButton = new JButton("Show Products");
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		buttonPanel.add(showButton);

		// Add panels to main panel
		add(inputPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		// Add action listeners
		addButton.addActionListener(_ -> addProduct());
		removeButton.addActionListener(_ -> removeProduct());
		showButton.addActionListener(_ -> showProducts());
	}

	private void addProduct() {
		String productCode = productCodeField.getText().trim();
		String quantity = quantityField.getText().trim();

		if (!model.isValidProduct(productCode)) {
			JOptionPane.showMessageDialog(this,
					"The product code entered does not exist.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!model.isValidQuantity(quantity)) {
			JOptionPane.showMessageDialog(this,
					"Please enter a valid quantity.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!model.addProductToInvoice(productCode, Integer.parseInt(quantity))) {
			JOptionPane.showMessageDialog(this,
					"Product already exists in the invoice.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
		clearFields();
	}

	private void removeProduct() {
		String productCode = productCodeField.getText().trim();
		String quantityStr = quantityField.getText().trim();
	
		if (productCode.isEmpty() || quantityStr.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Product code cannot be emty. Please enter both product code and quantity",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	
		int quantity;
		try {
			quantity = Integer.parseInt(quantityStr);
			if (quantity <= 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
					"Quantity entered must be a postive number",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	
		if (!model.removeProductFromInvoice(productCode, quantity)) {
			JOptionPane.showMessageDialog(this,
					"The product code does not exist in the invoice, or the quantity exceeds the available amount.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
					"Removed " + quantity + " units of product code " + productCode + " successfully.",
					"Success",
					JOptionPane.INFORMATION_MESSAGE);
		}
	
		clearFields();
	}

	private void showProducts() {
		if (!model.isInventoryLoaded()) {
			JOptionPane.showMessageDialog(this,
					"Please load the inventory first.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		String searchCode = productCodeField.getText().trim();

		JFrame productList = new JFrame("Product List");
		productList.setLayout(new BorderLayout());

		JTextArea productArea = new JTextArea(20, 50);
		productArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(productArea);

		// If search contains wildcard, show filtered results
		if (searchCode.contains("*")) {
			String prefix = searchCode.replace("*", "");
			productArea.setText(model.getFilteredProducts(prefix));
		} else {
			productArea.setText(model.getAllProducts());
		}

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(_ -> productList.dispose());

		productList.add(scrollPane, BorderLayout.CENTER);
		productList.add(closeButton, BorderLayout.SOUTH);
		productList.pack();
		productList.setLocationRelativeTo(this);
		productList.setVisible(true);
	}

	private void clearFields() {
		productCodeField.setText("");
		quantityField.setText("");
	}
}