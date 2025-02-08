import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import java.awt.*;

public class ShiftPanel extends JPanel {
	private JLabel firstNameLabel;
	private JTextField firstNameField;
	private JLabel lastNameLabel;
	private JTextField lastNameField;
	private JLabel datetimeLabel;
	private JTextField datetimeField;
	private JButton startShiftButton;
	private JButton endShiftButton;
	private DataModel model;

	public ShiftPanel(DataModel model) {
		this.model = model;
		setLayout(new BorderLayout(10, 10));

		// Input Panel
		JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

		// First Name
		firstNameLabel = new JLabel("First Name:", SwingConstants.RIGHT);
		firstNameField = new JTextField(15);
		inputPanel.add(firstNameLabel);
		inputPanel.add(firstNameField);

		// Last Name
		lastNameLabel = new JLabel("Last Name:", SwingConstants.RIGHT);
		lastNameField = new JTextField(15);
		inputPanel.add(lastNameLabel);
		inputPanel.add(lastNameField);

		// DateTime
		datetimeLabel = new JLabel("Shift Time:", SwingConstants.RIGHT);
		datetimeField = new JTextField(15);
		datetimeField.setEditable(false);
		datetimeField.setBackground(new Color(240, 240, 240));
		inputPanel.add(datetimeLabel);
		inputPanel.add(datetimeField);

		// Button Panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		startShiftButton = new JButton("Start Shift");
		endShiftButton = new JButton("End Shift");
		buttonPanel.add(startShiftButton);
		buttonPanel.add(endShiftButton);

		// Add panels to main panel
		add(inputPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		// Add action listeners
		startShiftButton.addActionListener(_ -> startShift());
		endShiftButton.addActionListener(_ -> endShift());
	}

	private void startShift() {
		String firstName = firstNameField.getText();
		String lastName = lastNameField.getText();
		if (!firstName.isEmpty() && !lastName.isEmpty()) {
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			datetimeField.setText(formatter.format(now));
			model.startShift(firstName, lastName);
		}
	}

	private void endShift() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		datetimeField.setText(formatter.format(now));
		clearFields();
		model.clearProductItems();
		model.endShift();
	}

	public void clearFields() {
		firstNameField.setText("");
		lastNameField.setText("");
	}
}