/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
package com.ifountain.smarts.test.util;

public class SmartsConnectionParams {

	private String broker, domain, username, password;
    private String brokerUsername = "BrokerNonsecure";
    private String brokerPassword = "Nonsecure";

    public String getBrokerUsername() {
        return brokerUsername;
    }

    public void setBrokerUsername(String brokerUsername) {
        this.brokerUsername = brokerUsername;
    }

    public String getBrokerPassword() {
        return brokerPassword;
    }

    public void setBrokerPassword(String brokerPassword) {
        this.brokerPassword = brokerPassword;
    }

    public String getBroker() {
		return broker;
	}
	public void setBroker(String broker) {
		this.broker = broker;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public SmartsConnectionParams(String broker, String domain, String username, String password) {
		super();
		this.broker = broker;
		this.domain = domain;
		this.username = username;
		this.password = password;
	}
}
