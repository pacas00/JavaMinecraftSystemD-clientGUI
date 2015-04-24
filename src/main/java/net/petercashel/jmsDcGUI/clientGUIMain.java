package net.petercashel.jmsDcGUI;


import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultCaret;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import net.petercashel.commonlib.threading.threadManager;
import java.awt.GridLayout;
import javax.swing.SwingConstants;

public class clientGUIMain {

	public static JFrame frame;
	private static JTextPane editorPane;
	private static final Font MONOSPACED = new Font("Monospaced", 0, 13);
    public static boolean run = true;
	public static String host = "127.0.0.1";
	public static int port = 14444;
	public static Boolean CLIMode;
	 static JTextField textField;
	 static JScrollPane scrollPane;
	 static JButton btnConnect;
	private JLabel lblServer;
	static JTextField textField_ServerAddress;
	private JLabel lblPort;
	 static JTextField textField_ServerPort;
	static String lastLine = "";
	public static PrintStream out = null;
	public static PrintStream err = null;
	static JCheckBox chckbxCLIMode;
	private JPanel panel;
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
		    public void run() {
				clientGUIMain.shutdown();
			}			
		});
		
		threadManager.getInstance().addRunnable((new Runnable() {
			public void run() {
				try {
					@SuppressWarnings("unused")
					clientGUIMain window = new clientGUIMain();					
					
					err = new PrintStreamWrapper(System.err, true);
					out = new PrintStreamWrapper(System.out, true);
					
					System.setErr(err);
					System.setOut(out);
					
					clientGUIMain.frame.setVisible(true);
					
					threadManager.getInstance().addRunnable((new Runnable() {
						public void run() {
							try {
								DepLoader.main(args);
							}
							catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}));
					
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}));
	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public clientGUIMain() {
		initialize();
		
	}
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				shutdown();
			}
		});
		frame.setBounds(100, 100, 800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("JMSDd Client");
		
		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(0, 6, 5, 0));
		
		lblServer = new JLabel("Server:");
		lblServer.setHorizontalAlignment(SwingConstants.TRAILING);
		panel.add(lblServer);
		//		lblServer.setMinimumSize(new Dimension(160,40));
		//		lblServer.setSize(new Dimension(160,40));
				
				textField_ServerAddress = new JTextField();
				panel.add(textField_ServerAddress);
				textField_ServerAddress.setColumns(10);
				//		textField_ServerAddress.setMinimumSize(new Dimension(120,40));
				//		textField_ServerAddress.setMaximumSize(new Dimension(120,40));
						textField_ServerAddress.setText(host);
						
						lblPort = new JLabel("Port:");
						lblPort.setHorizontalAlignment(SwingConstants.TRAILING);
						panel.add(lblPort);
						//		lblPort.setMinimumSize(new Dimension(80,40));
						//		lblPort.setSize(new Dimension(80,40));
								
								textField_ServerPort = new JTextField();
								textField_ServerPort.setText("14444");
								panel.add(textField_ServerPort);
								textField_ServerPort.setColumns(10);
								
								chckbxCLIMode = new JCheckBox("Unix Socket Mode");
								chckbxCLIMode.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										CLIMode = chckbxCLIMode.isSelected();
										if (chckbxCLIMode.isSelected()) {
											chckbxCLIMode.setText("Unix Socket Mode");
										} else {
											chckbxCLIMode.setText("Network Client Mode");
										}
									}
								});
								panel.add(chckbxCLIMode);
								
								
								btnConnect = new JButton("Connect");
								panel.add(btnConnect);
								btnConnect.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										host = textField_ServerAddress.getText();
										port = Integer.parseInt(textField_ServerPort.getText());
										reconnect();
									}
								});
								btnConnect.setEnabled(false);
								btnConnect.setEnabled(false);
		
		textField = new JTextField();
		textField.setFont(new Font("Monospaced", Font.PLAIN, 14));
		textField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					textField.setText(lastLine);
				}
				
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					clientGUIMain_DependantLogic.EnterKeyHandler();
					
				}
			}
		});
		frame.getContentPane().add(textField, BorderLayout.SOUTH);
		textField.setColumns(10);
		
		scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		editorPane = new JTextPane();
        editorPane.setBackground(Color.DARK_GRAY);
        editorPane.setBackground(new Color(70, 130, 180, 255));
        editorPane.setForeground(new Color(0, 0, 0, 255));
        editorPane.setEditable(false);
        editorPane.setFont(MONOSPACED);
        
        DefaultCaret caret = (DefaultCaret) editorPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scrollPane.setViewportView(editorPane);
		
	}
	
	public static void print(String string) {
		String s = editorPane.getText();
        editorPane.setText(s + string);
        editorPane.setFont(MONOSPACED);
	}
	
	public static void println(String string) {
		String s = editorPane.getText();
        editorPane.setText(s + string + "\n");
        editorPane.setFont(MONOSPACED);
	}
	
	public static void clientMain() {

		reconnect();

		try {
			Thread.sleep(2000);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// init other

		//commandClient.ConsoleHandover();

	}

	public static void shutdown() {
		clientGUIMain_DependantLogic.shutdown();

	}

	public static void reconnect() {
		clientGUIMain_DependantLogic.reconnect();

	}
}
