/**
 * 
 */
package com.samba.smbserver;

import java.util.LinkedList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.alfresco.config.ConfigElement;
import org.alfresco.jlan.debug.DebugInterface;

import android.content.ContextWrapper;
import android.util.Log;

/**
 * @author Patrick
 *
 */
public class DebugHandler extends Handler {

	private static DebugHandler instance;
	private LinkedList<MessageListener> listeners;
	
	public static DebugHandler getInstance(){
		if(instance==null)
			instance = new DebugHandler();
		return instance;
	}
	
	/**
	 * 
	 */
	protected DebugHandler() {
		this.listeners = new LinkedList<MessageListener>();
		this.setLevel(Level.ALL);
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#close()
	 */
	@Override
	public void close() {
		
	}

	public void attach(MessageListener listener){
		listeners.add(listener);
	}
	
	public MessageListener dettach(MessageListener listener){
		listeners.remove(listener);
		return listener;
	}
	
	/* (non-Javadoc)
	 * @see java.util.logging.Handler#flush()
	 */
	@Override
	public void flush() {

	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	@Override
	public void publish(LogRecord record) {
		// TODO Auto-generated method stub
		String msg = record.getMessage();
		this.print(msg);
	}

	private void print(String tag, String msg){
		Log.d(tag, msg);
		for(MessageListener listener : this.listeners){
			listener.message(msg);
		}
	}
	
	private void print(String msg){
		this.print(AndroidSMBConstants.TAG, msg);
	}

}
