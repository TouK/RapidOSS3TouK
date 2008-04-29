package connection

import com.ifountain.comp.utils.HttpUtils
import com.ifountain.core.connection.BaseConnection
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException;


public class HttpConnectionImpl extends BaseConnection{

    public static final String BASE_URL = "BaseUrl";
    private String baseUrl;
    private ConnectionParam params;
    private HttpUtils httpUtils;

    public void _connect() throws Exception {
    }

    public void _disconnect() {
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
