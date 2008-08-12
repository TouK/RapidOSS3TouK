/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
 * noted in a separate copyright notice. All rights reserved.
 * This file is part of RapidCMDB.
 * 
 * RapidCMDB is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */
package com.ifountain.smarts.connection;

import com.ifountain.core.connection.BaseConnection;
import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException;
import com.smarts.remote.SmRemoteBroker;
import com.smarts.remote.SmRemoteDomainManager;

public class SmartsConnectionImpl extends BaseConnection{

	public static final String BROKER = "Broker";
	public static final String DOMAIN = "Domain";
	public static final String USERNAME = "Username";
	public static final String PASSWORD = "Password";

    public static long isConnectedCount = 0;

	private String broker;
	private String domain;
	private String username;
	private String password;
	private SmRemoteDomainManager domainManager;
	

	protected void connect() throws Exception {
	    SmRemoteBroker smBroker = new SmRemoteBroker(broker);
	    smBroker.attach("BrokerNonsecure", "Nonsecure"); 
        domainManager.attach(smBroker, domain, username, password);
        smBroker.detach();
	}

	protected void disconnect() {
	    if(domainManager != null)
        {
            domainManager.detach();
        }
	}
	public void init(ConnectionParam param) throws Exception {
		this.params = param;
		this.broker = checkParam(BROKER);
        this.domain = checkParam(DOMAIN);
        this.username = checkParam(USERNAME);
        this.password = checkParam(PASSWORD);
        this.domainManager = new SmRemoteDomainManager();
	}

	public boolean isConnected() {
        isConnectedCount ++;
        if(domainManager != null)
        {
            try
            {
                domainManager.noop();
            }
            catch (Exception e)
            {
                try {
                    domainManager.detach();
                }
                catch (RuntimeException exceptionWillBeIgnored) {
                }
                return false;
            }
            return domainManager.attached();
        }
        else
        {
            return false;
        }
	}
	
	private String checkParam(String parameterName) throws UndefinedConnectionParameterException {
        if(!params.getOtherParams().containsKey(parameterName)){
            throw new UndefinedConnectionParameterException(parameterName);
        }
        return (String) params.getOtherParams().get(parameterName);
    }

	public SmRemoteDomainManager getDomainManager() {
		return domainManager;
	}
}
