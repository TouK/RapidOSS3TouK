package connection;

import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.IConnection;
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException;
import com.ifountain.comp.utils.HttpUtils;

public class HttpConnectionImpl implements IConnection {

    public static final String BASE_URL = "BaseUrl";
    private String baseUrl;
    private ConnectionParam params;
    private HttpUtils httpUtils;

    public void connect() throws Exception {
    }

    public void disconnect() {
    }

    public ConnectionParam getParameters() {
        return params;
    }

    public void init(ConnectionParam param) throws Exception {
        this.params = param;
        this.baseUrl = checkParam(BASE_URL);
        setHttpConnection();
    }

    public boolean isConnected() {
        return true;
    }
    
	protected void setHttpConnection(){
		httpUtils = new HttpUtils(); 
	} 
	
	protected void setHttpConnection(HttpUtils httpUtils){
		this.httpUtils = httpUtils;
	}	
	
	public HttpUtils getHttpConnection(){
		return httpUtils;
	}

    protected String checkParam(String parameterName) throws UndefinedConnectionParameterException {
        if(!params.getOtherParams().containsKey(parameterName)){
            throw new UndefinedConnectionParameterException(parameterName);
        }
        return (String) params.getOtherParams().get(parameterName);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

}
