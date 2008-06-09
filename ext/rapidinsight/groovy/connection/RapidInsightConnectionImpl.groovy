package connection

import com.ifountain.core.connection.ConnectionParam;

public class RapidInsightConnectionImpl extends HttpConnectionImpl {

    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    private String username;
    private String password;

    public void connect() throws Exception {
    	if(!isConnected()){
	    	super.connect();
	    	def completeUrl = getBaseUrl() + "/User/login";
	    	def params = ["submission":"credentials", "login":username, "password":password];
	    	def response =  getHttpConnection().doGetRequest(completeUrl, params);
	    	if(response.indexOf("<Successful>") == -1){
	    		throw new Exception("Could not login using URL: " + completeUrl)
	    	}
    	}
    }

    public void disconnect() {
    	def completeUrl = getBaseUrl() + "/User/logout";
    	def params = [:];
    	def response =  getHttpConnection().doGetRequest(completeUrl, params);
    	if(response.indexOf("<Successful>") == -1){
    		throw new Exception("Could not logout");
    	}
    }

    public void _init(ConnectionParam param) throws Exception {
        super.init(param);
        this.username = checkParam(USERNAME);
        this.password = checkParam(PASSWORD);
    }

    public boolean isConnected() {
    	def completeUrl = getBaseUrl() + "/User/login";
    	def params = [:];
    	def response =  getHttpConnection().doGetRequest(completeUrl, params);
    	if(response.indexOf("<Successful>") == -1){
    		return false;
    	}
    	return true;
    }

}
