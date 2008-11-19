/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
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
	public static final String BROKER_USERNAME = "BrokerUsername";
	public static final String BROKER_PASSWORD = "BrokerPassword";
	public static final String NON_SECURE_BROKER_USERNAME = "BrokerNonsecure";
	public static final String NON_SECURE_BROKER_PASSWORD = "Nonsecure";

	private String broker;
	private String domain;
	private String username;
	private String password;
	private String brokerUsername;
	private String brokerPassword;
	protected SmRemoteDomainManager domainManager;
	

	protected void connect() throws Exception {
	    SmRemoteBroker smBroker = new SmRemoteBroker(broker);
	    smBroker.attach(brokerUsername, brokerPassword); 
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
        this.brokerUsername =  (String)params.getOtherParams().get(BROKER_USERNAME);
        this.brokerPassword =  (String)params.getOtherParams().get(BROKER_PASSWORD);
        if(brokerUsername == null || brokerUsername.trim().equals("") || brokerPassword == null)
        {
            brokerUsername = NON_SECURE_BROKER_USERNAME;
            brokerPassword = NON_SECURE_BROKER_PASSWORD;
        }
        this.domainManager = new SmRemoteDomainManager();
	}
    public boolean checkConnection()
       {
           if(domainManager != null)
           {
               try
               {
                   domainManager.noop();
               }
               catch(Exception exception)
               {
                   return false;
               }
               return domainManager.attached();
           } else
           {
               return false;
           }
       }

       public boolean isConnected()
       {
           boolean flag = checkConnection();
           if(!flag && domainManager != null)
               try
               {
                   domainManager.detach();
               }
               catch(RuntimeException runtimeexception) { }
           return flag;
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
