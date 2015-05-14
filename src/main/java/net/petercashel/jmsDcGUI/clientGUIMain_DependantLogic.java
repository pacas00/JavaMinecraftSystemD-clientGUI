package net.petercashel.jmsDcGUI;

import static net.petercashel.jmsDcGUI.Configuration.cfg;
import static net.petercashel.jmsDcGUI.Configuration.configDir;
import static net.petercashel.jmsDcGUI.Configuration.configInit;
import static net.petercashel.jmsDcGUI.Configuration.getDefault;
import static net.petercashel.jmsDcGUI.Configuration.getJSONObject;
import static net.petercashel.jmsDcGUI.clientGUIMain.*;
import java.io.File;
import net.petercashel.commonlib.threading.threadManager;
import net.petercashel.commonlib.util.OS_Util;
import net.petercashel.jmsDc.command.commandClient;
import net.petercashel.nettyCore.client.clientCore;
import net.petercashel.nettyCore.clientUDS.clientCoreUDS;
import net.petercashel.nettyCore.common.exceptions.ConnectionShuttingDown;
import net.petercashel.nettyCore.ssl.SSLContextProvider;

public class clientGUIMain_DependantLogic {

	public static void PostLoad() {
		// init client console and network;
				configInit();

				if (getDefault(getJSONObject(cfg, "clientSettings"), "clientCLIMode", true) == true && OS_Util.isWinNT()) {
					System.err.println();
					System.err.println("Unix Socket based CLI connections do not function on the Windows Platform.");
					System.err.println("Please correct your configuration by disabling clientCLIEnable");
					System.err.println(new File(configDir, "config.json").toPath());

					return;

				}
				clientCore.UseSSL = getDefault(getJSONObject(cfg, "clientSettings"), "clientSSLEnable", true);
				clientCore.DoAuth = getDefault(getJSONObject(cfg, "authSettings"), "authenticationEnable", true);
				clientCore.username = getDefault(getJSONObject(cfg, "authSettings"), "authenticationUsername", "");
				clientCore.token = getDefault(getJSONObject(cfg, "authSettings"), "authenticationToken", "");

				if (getDefault(getJSONObject(getJSONObject(cfg, "clientSettings"), "SSLSettings"), "SSL_UseExternal", true)) {
					SSLContextProvider.useExternalSSL = true;
					SSLContextProvider.pathToSSLCert = getDefault(
							getJSONObject(getJSONObject(cfg, "clientSettings"), "SSLSettings"), "SSL_ExternalPath", (new File(
									configDir, "SSLCERT.p12").toPath().toString()));
					SSLContextProvider.SSLCertSecret = getDefault(
							getJSONObject(getJSONObject(cfg, "clientSettings"), "SSLSettings"), "SSL_ExternalSecret", "secret");
				}

				host = getDefault(getJSONObject(cfg, "clientSettings"), "serverAddress", "127.0.0.1");
				port = getDefault(getJSONObject(cfg, "clientSettings"), "serverPort", 14444);
				CLIMode = getDefault(getJSONObject(cfg, "clientSettings"), "clientCLIMode", true);
				if (CLIMode) {
					chckbxCLIMode.setSelected(CLIMode);
					if (chckbxCLIMode.isSelected()) {
						chckbxNetMode.setSelected(false);
					} else {
						chckbxNetMode.setSelected(true);
					}
				}
				getDefault(getJSONObject(cfg, "clientSettings"), "clientCLIPath", "");

				commandClient.init();
				textField_ServerAddress.setText(host);
				textField_ServerPort.setText(String.valueOf(port));
				btnConnect.setEnabled(true);
				println("Ready to Go");
	}

	public static void EnterKeyHandler() {
		String line = textField.getText();
		lastLine = line;
		if (line.equalsIgnoreCase(".quit")) {
			clientGUIMain.shutdown();
			textField.setText("");
			frame.dispose();
		} else
		if (line.equalsIgnoreCase(".connect")) {
			clientGUIMain.reconnect();
			textField.setText("");
		} else
		if (!clientCore.connClosed || !clientCoreUDS.connClosed) {
			commandClient.sendCommand(line);
			textField.setText("");
		} else {
		commandClient.out.println("Connection is Closed.");
		commandClient.out.println("Please .connect or .quit");
		}
		
	}
	
	public static void shutdown() {
		run = false;
		try {
			if (!CLIMode) {
				clientCore.shutdown();
			} else {
				clientCoreUDS.shutdown();
			}
		}
		catch (NullPointerException e) {
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ConnectionShuttingDown e) {
		}
		threadManager.getInstance().shutdown();

	}

	public static void reconnect() {
		if (CLIMode && OS_Util.isWinNT()) {
			System.err.println();
			System.err.println("Unix Socket based CLI connections do not function on the Windows Platform.");
			System.err.println("Please correct your configuration by disabling clientCLIEnable");
			System.err.println(new File(configDir, "config.json").toPath());

			return;
		}
		
		
		if (!CLIMode) {
			threadManager.getInstance().addRunnable(new Runnable() {
				@Override
				public void run() {
					try {
						clientCore.initializeConnection(host, port);
					}
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						shutdown();
					}
				}
			});
		} else {
			threadManager.getInstance().addRunnable(new Runnable() {
				@Override
				public void run() {
					try {
						clientCoreUDS.initializeConnection(new File((getJSONObject(cfg, "clientSettings")
								.get("clientCLIPath")).getAsString()).toPath());
					}
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						shutdown();
					}
				}
			});
		}

	}

}
