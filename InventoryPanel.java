import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class InventoryPanel extends JPanel {
	private JTextField infoField;
	private JButton loadInventoryButton;
	private JButton showProductsButton;
	private DataModel model;

	public InventoryPanel(DataModel model) {
		this.model = model;
		setLayout(new BorderLayout(10, 10));

		// Button Panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		loadInventoryButton = new JButton("Load Inventory");
		showProductsButton = new JButton("Show Products");
		buttonPanel.add(loadInventoryButton);
		buttonPanel.add(showProductsButton);

		// Status Field
		infoField = new JTextField(40);
		infoField.setEditable(false);
		infoField.setBackground(new Color(240, 240, 240));

		// Add components
		add(buttonPanel, BorderLayout.NORTH);
		add(infoField, BorderLayout.CENTER);

		// Add action listeners
		loadInventoryButton.addActionListener(_ -> loadInventory());
		showProductsButton.addActionListener(_ -> showProducts());
	}

	private void loadInventory() {
		if (!model.isInventoryLoaded()) {
			model.loadInventory();
			infoField.setText("Inventory loaded successfully");
		} else {
			infoField.setText("Inventory is already loaded");
		}
	}

	private void showProducts() {
		if (!model.isInventoryLoaded()) {
			JOptionPane.showMessageDialog(this,
					"Please load the inventory first.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JFrame productFrame = new JFrame("Product List");
		productFrame.setLayout(new BorderLayout());

		JTextArea productArea = new JTextArea(20, 50);
		productArea.setEditable(false);

		List<Product> products = model.getProducts();
		String formattedProducts = formatProducts(products);
		productArea.setText(formattedProducts);

		JScrollPane scrollPane = new JScrollPane(productArea);
		productFrame.add(scrollPane, BorderLayout.CENTER);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(_ -> productFrame.dispose());
		productFrame.add(closeButton, BorderLayout.SOUTH);

		productFrame.pack();
		productFrame.setLocationRelativeTo(null);
		productFrame.setVisible(true);
	}

	private String formatProducts(List<Product> products) {
		StringBuilder formatted = new StringBuilder();
		for (Product product : products) {
			formatted
					.append(String.format("%s, %s, %s%n", product.getProductCodeNumber(), product.getName(),
							product.getDescription()));
		}
		return formatted.toString();
	}

	public JTextField getInfoField() {
		return infoField;
	}
}