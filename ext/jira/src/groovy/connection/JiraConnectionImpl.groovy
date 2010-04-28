package connection;

import com.ifountain.core.connection.BaseConnection
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException

import org.ifountain.www.jira.rpc.soap.jirasoapservice_v2.*;
import com.atlassian.jira.rpc.soap.beans.*;
import com.atlassian.jira.rpc.exception.*;
import org.apache.log4j.Logger;

public class JiraConnectionImpl extends BaseConnection{

    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    private String username ;
    private String password ;
    private String token;
    private JiraSoapService jiraSoapService;
    
    protected void connect() {
    	JiraSoapServiceService jiraSoapServiceGetter = new JiraSoapServiceServiceLocator();
		jiraSoapService = jiraSoapServiceGetter.getJirasoapserviceV2();
    	token = jiraSoapService.login(username, password);
    }

    public boolean isConnectionException(Throwable t)
    {
        return false;
    }

    protected void disconnect() {
    	if (token=="" || token==null) return;
    	jiraSoapService.logout(token);
    }

    public void init(ConnectionParam param) throws Exception {
        this.params = param;
        this.username = checkParam(USERNAME);
        this.password = checkParam(PASSWORD);
    }

    public boolean checkConnection() {
    	if (token=="" || token==null) return false;
    	try{
    		jiraSoapService.getServerInfo(token);
    		return true;
    	}
        catch (RemoteAuthenticationException e){
            Logger errorLogger=Logger.getRootLogger();
            if(errorLogger.isDebugEnabled())
            {
                errorLogger.debug("[JiraConnectionImpl]: Disconnect detected during checkConnection. Reason:"+e.toString());
            }

            return false;
    	}
    	
    }

    public String getToken(){
    	return this.token;
    }
    
    public JiraSoapService getJiraSoapService(){
    	return this.jiraSoapService;
    }
    
    public String getUsername(){
    	return this.username;
    }
    
    public String getPassword(){
    	return this.password;
    }
}
