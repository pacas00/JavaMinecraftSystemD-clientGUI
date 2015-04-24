package net.petercashel.jmsDcGUI;

import java.io.PrintStream;
import javax.swing.SwingUtilities;

public class PrintStreamWrapper extends PrintStream {

	PrintStream outputStream;
	String prefix = "";

	public PrintStreamWrapper(PrintStream out) {
		this(out, false);
		outputStream = out;
	}

	public PrintStreamWrapper(PrintStream out, boolean autoFlush) {
		super(out, autoFlush);
		outputStream = out;
	}

	public void println(final String x) {
		synchronized (this) {
			SwingUtilities.invokeLater(new Runnable ()
	        {
				@Override
				public void run() {
					clientGUIMain.println(x);
				}
	        });
			outputStream.println(x);
			outputStream.flush();
		}
	}

	public void println() {
		synchronized (this) {
			SwingUtilities.invokeLater(new Runnable ()
	        {
				@Override
				public void run() {
					clientGUIMain.println("");
				}
	        });
			outputStream.println();
			outputStream.flush();
		}
	}

	public void print(final String s) {
		synchronized (this) {
			SwingUtilities.invokeLater(new Runnable ()
	        {
				@Override
				public void run() {
					clientGUIMain.print(s);
				}
	        });
			clientGUIMain.print(s);
			outputStream.print(s);
			outputStream.flush();
		}
	}
}
