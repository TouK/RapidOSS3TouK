package datasource

import com.ifountain.core.datasource.Action
import com.ifountain.core.connection.IConnection
import java.awt.image.BufferedImage;

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 20, 2008
* Time: 2:47:34 PM
*/
class GetImageAction implements Action{
    private String url;
    private Map params;
    private BufferedImage image;
    public GetImageAction(String url, Map params){
       this.url = url;
       this.params = params;
    }
    public void execute(IConnection conn) {
       String completeUrl = conn.getBaseUrl() + url;
       image =  conn.getHttpConnection().getImage(completeUrl, params);
    }

    public BufferedImage getImage(){
        return image;
    }

}