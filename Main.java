public class Main {
	public static void main(String[] args) {
		DataModel model = new DataModel();
		DashboardFrame dashboardFrame = new DashboardFrame(model);
		ReceiptFrame receiptFrame = new ReceiptFrame(model);
		ItemsFrame itemsFrame = new ItemsFrame(model, receiptFrame);
		itemsFrame.setLocation(dashboardFrame.getX() + dashboardFrame.getWidth(), dashboardFrame.getY());
	}
}
