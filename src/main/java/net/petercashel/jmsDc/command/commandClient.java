/*******************************************************************************
 *    Copyright 2015 Peter Cashel (pacas00@petercashel.net)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/

package net.petercashel.jmsDc.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;
import net.petercashel.commonlib.threading.threadManager;
import net.petercashel.jmsDcGUI.clientGUIMain;
import net.petercashel.nettyCore.client.clientCore;
import net.petercashel.nettyCore.clientUDS.clientCoreUDS;
import net.petercashel.nettyCore.common.PacketRegistry;
import net.petercashel.nettyCore.common.packets.CMDInPacket;
import net.petercashel.nettyCore.common.packets.IOInPacket;
import net.petercashel.nettyCore.common.packets.IOOutPacket;
import net.petercashel.nettyCore.server.serverCore;

public class commandClient {

	public static InputStream in = null;
	public static PrintStream out = null;
	public static PrintStream err = null;

	public static void init() {
		err = System.err;
		out = System.out;
		in = System.in;
		
		
	}

	public static void sendCommand(final String s) {
		if (s.startsWith(".")) {
			threadManager.getInstance().addRunnable(new Runnable() {
				@Override
				public void run() {
					try {
						String str = s;
						str = s.substring(s.indexOf(".") + 1, s.length());
						if (!clientGUIMain.CLIMode) {
							(PacketRegistry.pack(new CMDInPacket(str))).sendPacket(clientCore.getChannel());
						} else {
							(PacketRegistry.pack(new CMDInPacket(str))).sendPacket(clientCoreUDS.getChannel());
						}

					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			threadManager.getInstance().addRunnable(new Runnable() {
				@Override
				public void run() {
					try {
						int i = s.getBytes().length;
						byte[] b = s.getBytes();
						if (!clientGUIMain.CLIMode) {
							(PacketRegistry.pack(new IOInPacket(i, b))).sendPacket(clientCore.getChannel());
						} else {
							(PacketRegistry.pack(new IOInPacket(i, b))).sendPacket(clientCoreUDS.getChannel());
						}
						i = 0;
						b = null;
					}
					catch (Exception e) {
					}
				}
			});
		}
	}
}
