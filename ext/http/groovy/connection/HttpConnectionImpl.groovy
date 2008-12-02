package connection

import com.ifountain.comp.utils.HttpUtils
import com.ifountain.core.connection.BaseConnection
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException

public class HttpConnectionImpl extends BaseConnection{

    public static final String BASE_URL = "BaseUrl";
    private String baseUrl;
    private HttpUtils httpUtils;

    protected void connect() throws Exception {
    }

    protected void disconnect() {
    }

   
    public void init(ConnectionParam param) throws Exception {
        super.init (param);
        this.baseUrl = checkParam(BASE_URL);
        setHttpConnection();
    }

    public boolean checkConnection() {
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
