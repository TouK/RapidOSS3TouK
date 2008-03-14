package datasources; 

import com.ifountain.comp.utils.*;
import org.apache.commons.httpclient.*;

class HttpUtilsMock extends HttpUtils{
        public boolean willThrowException = false;

        public String doGetRequest(String urlStr, Map params) throws HttpException, HttpStatusException, IOException {
            if(willThrowException){
                throw new HttpException();
            }
            return "<GetRequest></GetRequest>";
        }
        
        public String doPostRequest(String urlStr, Map params) throws HttpException, HttpStatusException, IOException {
            if(willThrowException){
                throw new HttpException();
            }
            return "<PostRequest></PostRequest>";
        }
    }