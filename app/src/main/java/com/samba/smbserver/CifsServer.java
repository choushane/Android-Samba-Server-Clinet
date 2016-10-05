/**
 * 
 */
package com.samba.smbserver;

import java.io.InputStreamReader;

import org.alfresco.jlan.app.JLANCifsServer;
import org.alfresco.jlan.app.XMLServerConfiguration;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.NetworkServer;
import org.alfresco.jlan.smb.server.CIFSConfigSection;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

/**
 * @author Patrick
 *
 */
public class CifsServer extends JLANCifsServer {

	private String args;
	private XMLServerConfiguration srvConfig;

	/**
	 * @param srvConfig 
	 * 
	 */
	public CifsServer(XMLServerConfiguration srvConfig) {
		super();
		this.srvConfig = srvConfig;
	}

	public void run() throws Exception {
        //  Loop until shutdown
		CIFSConfigSection cifsConfig = (CIFSConfigSection) srvConfig.getConfigSection( CIFSConfigSection.SectionName);
        
		//	Create the NetBIOS name server if NetBIOS SMB is enabled
		
		if  (cifsConfig.hasNetBIOSSMB())
			srvConfig.addServer( createNetBIOSServer(srvConfig));

		//	Create the SMB server
		
		srvConfig.addServer( createSMBServer(srvConfig));
		
//		Start the configured servers
		
		for ( int i = 0; i < srvConfig.numberOfServers(); i++) {
			//	Get the current server
			NetworkServer server = srvConfig.getServer(i);
			String serverName = server.getProtocolName();
			//	Start the server
			srvConfig.getServer(i).startServer();
		}
	}

	public void stop() {
		for ( int i = 0; i < srvConfig.numberOfServers(); i++) {
			//	Get the current server
			NetworkServer server = srvConfig.getServer(i);
			String serverName = server.getProtocolName();
			//	Start the server
			srvConfig.getServer(i).shutdownServer(true);
		}
	}

}
