import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

public class DashboardFrame extends JFrame {
	private ShiftPanel shiftPanel;
	private InventoryPanel inventoryPanel;
	private ProductPanel productPanel;

	private static final int FRAME_WIDTH = 300;
	private static final int FRAME_HEIGHT = 600;

	public DashboardFrame(DataModel model) {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		// Initialize panels
		shiftPanel = new ShiftPanel(model);
		inventoryPanel = new InventoryPanel(model);
		productPanel = new ProductPanel(model);

		// Add some vertical spacing between panels
		add(Box.createVerticalStrut(10));
		add(shiftPanel);
		add(Box.createVerticalStrut(10));
		add(inventoryPanel);
		add(Box.createVerticalStrut(10));
		add(productPanel);
		add(Box.createVerticalStrut(10));

		setTitle("Store POS");
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
}
