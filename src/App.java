import javax.swing.SwingUtilities;

public class App {

	public static void main_cli (String[] args)
	{
		CLI my_CLI = new CLI();

//		SwingUtilities.invokeLater(new Runnable() {
//
//			public void run() {
//				MainWindow mw = new MainWindow();
//			}
//		});
	}

	public static void main (String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
//
			public void run() {
				MainWindow mw = new MainWindow();
			}
		});
	}

}
