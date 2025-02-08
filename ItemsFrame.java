import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.Dimension;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JComponent;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class ItemsFrame extends JFrame implements InvoiceObserver {
	private DataModel model;
	private ReceiptFrame receiptFrame;
	private static final int FRAME_WIDTH = 400;
	private static final int FRAME_HEIGHT = 800;

	// Panel 1
	private JPanel invoicePanel;
	private JTextArea productItemsTextArea;

	// Panel 2
	private JPanel totalsPanel;
	private JTextField cityStateField;
	private JTextField taxPercentageField;
	private JTextField discountPercentageField;
	private JCheckBox isDiscountedCheckbox;
	private JTextField totalAmountRawField;
	private JTextField totalAmountTaxField;
	private JTextField totalAmountDiscountedField;
	private JTextField grandTotalField;
	private JButton printButton;

	public ItemsFrame(DataModel model, ReceiptFrame receiptFrame) {
		this.model = model;
		this.receiptFrame = receiptFrame;

		// Register as observer
		model.addObserver(this);

		setLayout(new GridLayout(2, 1, 5, 5));

		// Panel 1 - Invoice View
		invoicePanel = new JPanel(new BorderLayout());
		invoicePanel.setBorder(BorderFactory.createTitledBorder("Current Invoice"));

		productItemsTextArea = new JTextArea(10, 40);
		productItemsTextArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(productItemsTextArea);
		invoicePanel.add(scrollPane, BorderLayout.CENTER);

		// Panel 2 - Totals and Receipt
		totalsPanel = new JPanel();
		totalsPanel.setBorder(BorderFactory.createTitledBorder("Totals and Receipt"));
		totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));

		// Create sub-panels for each row
		JPanel locationPanel = createLabelFieldPanel("Location:", cityStateField = createReadOnlyField());
		cityStateField.setText(model.getCity() + ", " + model.getState());

		JPanel taxPanel = createLabelFieldPanel("Tax Rate:", taxPercentageField = createReadOnlyField());
		taxPercentageField.setText(String.format("%.2f%%", model.getCityTaxPercentage() * 100));

		JPanel discountPanel = createLabelFieldPanel("Discount %:", discountPercentageField = new JTextField("10.00"));

		JPanel discountCheckPanel = createLabelFieldPanel("Apply Discount:", isDiscountedCheckbox = new JCheckBox());

		JPanel subtotalPanel = createLabelFieldPanel("Subtotal:", totalAmountRawField = createReadOnlyField());

		JPanel taxedTotalPanel = createLabelFieldPanel("Total with Tax:", totalAmountTaxField = createReadOnlyField());

		JPanel discountedTotalPanel = createLabelFieldPanel("Discount Amount:",
				totalAmountDiscountedField = createReadOnlyField());

		JPanel grandTotalPanel = createLabelFieldPanel("Grand Total:", grandTotalField = createReadOnlyField());

		// Print button in its own panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		printButton = new JButton("Print Receipt");
		buttonPanel.add(printButton);

		// Add all panels to totals panel
		totalsPanel.add(locationPanel);
		totalsPanel.add(Box.createVerticalStrut(5));
		totalsPanel.add(taxPanel);
		totalsPanel.add(Box.createVerticalStrut(5));
		totalsPanel.add(discountPanel);
		totalsPanel.add(Box.createVerticalStrut(5));
		totalsPanel.add(discountCheckPanel);
		totalsPanel.add(Box.createVerticalStrut(5));
		totalsPanel.add(subtotalPanel);
		totalsPanel.add(Box.createVerticalStrut(5));
		totalsPanel.add(taxedTotalPanel);
		totalsPanel.add(Box.createVerticalStrut(5));
		totalsPanel.add(discountedTotalPanel);
		totalsPanel.add(Box.createVerticalStrut(5));
		totalsPanel.add(grandTotalPanel);
		totalsPanel.add(Box.createVerticalStrut(10));
		totalsPanel.add(buttonPanel);

		// Add panels to frame
		add(invoicePanel);
		add(totalsPanel);

		// Add listeners
		isDiscountedCheckbox.addActionListener(_ -> updateTotals());
		discountPercentageField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateTotals();
			}
		});
		printButton.addActionListener(_ -> printReceipt());

		setTitle("Invoice Details");
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setVisible(true);
	}

	private JPanel createLabelFieldPanel(String labelText, JComponent field) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel(labelText);
		label.setPreferredSize(new Dimension(120, 20)); // Fixed width for labels
		panel.add(label);

		if (field instanceof JTextField) {
			((JTextField) field).setPreferredSize(new Dimension(150, 20)); // Fixed width for text fields
		}
		panel.add(field);

		return panel;
	}

	private JTextField createReadOnlyField() {
		JTextField field = new JTextField();
		field.setEditable(false);
		field.setBackground(new Color(240, 240, 240));
		return field;
	}

	public void updateInvoiceView() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-10s %-30s %-10s %-10s %-10s%n",
				"Code", "Name", "Qty", "Price", "Total"));
		sb.append("-".repeat(60)).append("\n");

		for (ProductItem item : model.getProductItems()) {
			Product product = item.getProduct();
			double total = product.getPrice() * item.getQuantity();
			sb.append(String.format("%-10s %-30s %-10d $%-9.2f $%-9.2f%n",
					product.getProductCodeNumber(),
					product.getName(),
					item.getQuantity(),
					product.getPrice(),
					total));
		}

		productItemsTextArea.setText(sb.toString());
		updateTotals();
	}

	private void updateTotals() {
		double subtotal = calculateSubtotal();
		double taxRate = model.getCityTaxPercentage();
		double taxAmount = subtotal * taxRate;
		double totalWithTax = subtotal + taxAmount;

		double discountPercent = 0.0;
		try {
			discountPercent = Double.parseDouble(discountPercentageField.getText()) / 100.0;
		} catch (NumberFormatException e) {
			discountPercent = 0.0;
		}

		double discountAmount = isDiscountedCheckbox.isSelected() ? totalWithTax * discountPercent : 0.0;
		double grandTotal = totalWithTax - discountAmount;

		totalAmountRawField.setText(String.format("$%.2f", subtotal));
		totalAmountTaxField.setText(String.format("$%.2f", totalWithTax));
		totalAmountDiscountedField.setText(String.format("$%.2f", discountAmount));
		grandTotalField.setText(String.format("$%.2f", grandTotal));
	}

	private double calculateSubtotal() {
		return model.getProductItems().stream()
				.mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
				.sum();
	}

	private void printReceipt() {
		receiptFrame.showReceipt(
				calculateSubtotal(),
				Double.parseDouble(discountPercentageField.getText()),
				isDiscountedCheckbox.isSelected());
	}

	@Override
	public void onInvoiceUpdated() {
		updateInvoiceView();
	}

	// Add window listener to clean up observer
	@Override
	public void dispose() {
		model.removeObserver(this);
		super.dispose();
	}
}
