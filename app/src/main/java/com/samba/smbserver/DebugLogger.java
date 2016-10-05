/**
 * 
 */
package com.samba.smbserver;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.alfresco.config.ConfigElement;
import org.alfresco.jlan.debug.DebugInterface;

/**
 * @author Patrick
 *
 */
public class DebugLogger implements DebugInterface {

	/* (non-Javadoc)
	 * @see org.alfresco.jlan.debug.DebugInterface#close()
	 */
	public void close() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.alfresco.jlan.debug.DebugInterface#debugPrint(java.lang.String)
	 */
	public void debugPrint(String str) {
		DebugHandler.getInstance().publish(new LogRecord(Level.INFO,str));
	}

	/* (non-Javadoc)
	 * @see org.alfresco.jlan.debug.DebugInterface#debugPrintln(java.lang.String)
	 */
	public void debugPrintln(String str) {
		DebugHandler.getInstance().publish(new LogRecord(Level.INFO,str));
	}

	/* (non-Javadoc)
	 * @see org.alfresco.jlan.debug.DebugInterface#initialize(org.alfresco.config.ConfigElement)
	 */
	public void initialize(ConfigElement params) throws Exception {
		// TODO Auto-generated method stub

	}

}
