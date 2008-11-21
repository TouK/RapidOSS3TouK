package connection

import com.ifountain.core.connection.ConnectionParam

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 21, 2008
* Time: 9:37:04 AM
*/
class HypericConnectionImpl extends HttpConnectionImpl {
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    private String username;
    private String password;

    public void connect() throws Exception {
        super.connect();
        if(!isConnected()){
            throw new Exception("Could not connect to " + getBaseUrl())
        }
    }

    public void disconnect() {
        def completeUrl = getBaseUrl() + "/Logout.do";
        def params = [:];
        def response = getHttpConnection().doGetRequest(completeUrl, params);
        if (response.indexOf("LoginForm") < 0) {
            throw new Exception("Could not logout");
        }
    }

    public void _init(ConnectionParam param) throws Exception {
        super.init(param);
        this.username = checkParam(USERNAME);
        this.password = checkParam(PASSWORD);
    }

    public boolean isConnected() {
        def completeUrl = getBaseUrl() + "/j_security_check.do";
        def params = ["j_username": username, "j_password": password];
        def response = getHttpConnection().doGetRequest(completeUrl, params);
        if (response.indexOf("LoginForm") > -1) {
            return false;
        }
        return true;
    }
}