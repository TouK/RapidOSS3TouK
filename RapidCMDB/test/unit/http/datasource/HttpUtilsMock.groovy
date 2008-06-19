package http.datasource

import com.ifountain.comp.utils.HttpStatusException
import com.ifountain.comp.utils.HttpUtils
import org.apache.commons.httpclient.HttpException;

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