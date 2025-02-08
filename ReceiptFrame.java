import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ReceiptFrame extends JFrame {
	private DataModel model;
	private JTextArea receiptArea;

	public ReceiptFrame(DataModel model) {
		this.model = model;
		setLayout(new BorderLayout());

		receiptArea = new JTextArea(30, 40);
		receiptArea.setEditable(false);
		receiptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

		add(new JScrollPane(receiptArea), BorderLayout.CENTER);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(_ -> setVisible(false));
		add(closeButton, BorderLayout.SOUTH);

		setTitle("Receipt");
		pack();
	}

	public void showReceipt(double subtotal, double discountPercent, boolean isDiscounted) {
		StringBuilder receipt = new StringBuilder();

		// Header
		receipt.append(String.format("%s%n", model.getStoreName()));
		receipt.append(String.format("%s, %s %s%n", model.getCity(), model.getState(), model.getZipCode()));
		receipt.append(String.format("Phone: %s%n", model.getPhoneNumber()));
		receipt.append("-".repeat(40)).append("\n");

		// Date/Time
		receipt.append(String.format("Date: %s%n", java.time.LocalDateTime.now().format(
				java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
		receipt.append("-".repeat(40)).append("\n");

		// Items
		receipt.append(String.format("%-8s %-20s %5s %8s%n", "Code", "Item", "Qty", "Total"));
		receipt.append("-".repeat(40)).append("\n");

		for (ProductItem item : model.getProductItems()) {
			Product product = item.getProduct();
			receipt.append(String.format("%-8s %-20s %5d $%7.2f%n",
					product.getProductCodeNumber(),
					product.getName(),
					item.getQuantity(),
					product.getPrice() * item.getQuantity()));
		}

		receipt.append("-".repeat(40)).append("\n");

		// Totals
		double taxAmount = subtotal * model.getCityTaxPercentage();
		double totalWithTax = subtotal + taxAmount;
		double discountAmount = isDiscounted ? totalWithTax * (discountPercent / 100.0) : 0.0;
		double grandTotal = totalWithTax - discountAmount;

		receipt.append(String.format("Subtotal:%32.2f%n", subtotal));
		receipt.append(String.format("Tax (%d%%):%31.2f%n",
				(int) (model.getCityTaxPercentage() * 100), taxAmount));
		if (isDiscounted) {
			receipt.append(String.format("Discount (%d%%):%28.2f%n",
					(int) discountPercent, discountAmount));
		}
		receipt.append(String.format("Grand Total:%30.2f%n", grandTotal));

		receipt.append("-".repeat(40)).append("\n");
		receipt.append("Your cashier serving you today is: " +
				"[Cashier Name]\n"); // TODO: Get from ShiftPanel
		receipt.append("Thank you for shopping with us!\n");

		receiptArea.setText(receipt.toString());
		setVisible(true);
	}
}
