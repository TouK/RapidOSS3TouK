package datasource

import com.ifountain.core.datasource.Action
import com.ifountain.core.connection.IConnection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 20, 2008
* Time: 3:37:43 PM
*/
class GetBytesAction implements Action{
    private String url;
    private Map params;
    def bytes;
    public GetBytesAction(String url, Map params){
        this.url = url;
        this.params = params;
    }

    public void execute(IConnection conn) {
        bytes = conn.getHttpConnection().getBytes(completeUrl, params);
    }

    def getBytes(){
        return bytes;
    }

}