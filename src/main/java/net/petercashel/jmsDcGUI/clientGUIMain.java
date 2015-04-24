package net.petercashel.jmsDcGUI;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
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
import javax.swing.JSeparator;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import net.petercashel.commonlib.threading.threadManager;
import java.awt.FlowLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;

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

	private JSeparator separator;
	private JSeparator separator_1;
	static JCheckBox chckbxCLIMode;
	private JSeparator separator_2;
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
					clientGUIMain window = new clientGUIMain();					
					
					err = new PrintStreamWrapper(System.err, true);
					out = new PrintStreamWrapper(System.out, true);
					
					System.setErr(err);
					System.setOut(out);
					
					window.frame.setVisible(true);
					
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
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFont(MONOSPACED);
		toolBar.setFloatable(false);
		toolBar.setMinimumSize(new Dimension(10, 80));
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		panel = new JPanel();
		toolBar.add(panel);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("8px"),
				ColumnSpec.decode("48px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("108px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("1px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("48px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("64px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("1px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("192px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("1px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("128px"),},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("23px"),}));
		
		lblServer = new JLabel("Server:");
		panel.add(lblServer, "2, 2, left, center");
		lblServer.setMinimumSize(new Dimension(160,40));
		lblServer.setSize(new Dimension(160,40));
		
		textField_ServerAddress = new JTextField();
		panel.add(textField_ServerAddress, "4, 2, left, center");
		textField_ServerAddress.setColumns(10);
		textField_ServerAddress.setMinimumSize(new Dimension(120,40));
		textField_ServerAddress.setMaximumSize(new Dimension(120,40));
		textField_ServerAddress.setText(host);
		
		separator = new JSeparator();
		panel.add(separator, "6, 2, left, center");
		
		lblPort = new JLabel("Port:");
		panel.add(lblPort, "8, 2, left, center");
		lblPort.setMinimumSize(new Dimension(80,40));
		lblPort.setSize(new Dimension(80,40));
		
		textField_ServerPort = new JTextField();
		panel.add(textField_ServerPort, "10, 2, left, center");
		textField_ServerPort.setColumns(10);
		textField_ServerPort.setMinimumSize(new Dimension(60,40));
		textField_ServerPort.setMaximumSize(new Dimension(60,40));
		
		separator_1 = new JSeparator();
		panel.add(separator_1, "12, 2, left, center");
		
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
		panel.add(chckbxCLIMode, "14, 2, left, top");
		
		separator_2 = new JSeparator();
		panel.add(separator_2, "16, 2, left, center");
		
		
		btnConnect = new JButton("Connect");
		panel.add(btnConnect, "18, 2, left, top");
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
