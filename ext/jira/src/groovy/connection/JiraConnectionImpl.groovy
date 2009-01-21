package connection;

import com.ifountain.core.connection.BaseConnection
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException

import org.ifountain.www.jira.rpc.soap.jirasoapservice_v2.*;
import com.atlassian.jira.rpc.soap.beans.*;

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

    protected void disconnect() {
    	jiraSoapService.logout(token);
    }

    public void init(ConnectionParam param) throws Exception {
        this.params = param;
        this.username = checkParam(USERNAME);
        this.password = checkParam(PASSWORD);
    }

    public boolean checkConnection() {
    	if (token!=""){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    private String checkParam(String parameterName) throws UndefinedConnectionParameterException {
        if(!params.getOtherParams().containsKey(parameterName)){
            throw new UndefinedConnectionParameterException(parameterName);
        }
        return (String) params.getOtherParams().get(parameterName);
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
}
