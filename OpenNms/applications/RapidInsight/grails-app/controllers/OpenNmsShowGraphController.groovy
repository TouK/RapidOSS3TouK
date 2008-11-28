/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Nov 28, 2008
 * Time: 3:40:14 PM
 * To change this template use File | Settings | File Templates.
 */
import com.ifountain.rcmdb.domain.util.ControllerUtils;
import com.ifountain.rcmdb.domain.util.DomainClassUtils;

import datasource.HttpDatasource;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.httpclient.util.ParameterParser;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Date;
 

class OpenNmsShowGraphController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:show,params:params) }
    
     def show={
        //response.setHeader("Content-disposition", "attachment; filename=${photo.name}")
        //response.contentType = photo.fileType //'image/jpeg' will do too
        //response.outputStream << photo.file //'myphoto.jpg' will do too

        def openNmsGraphDs=HttpDatasource.get(name:"openNmsHttpDs");
        openNmsGraphDs.doGetRequest("j_acegi_security_check", ["j_username":"admin","j_password":"admin"]);

        def graph=OpenNmsGraph.get(id:params.id);
        def image=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB )
        if(graph!=null)
        {
            def url=graph.url;
            def queryParams=new ParameterParser().parse(URIUtil.getQuery(url),'&' as char);
            def params=[:]
            for(param in queryParams)
            {
            	params[param.name]=param.value;
            }
            long end=new Date().getTime();
            long start=end-(24*60*60*1000);

            params["start"]=String.valueOf(start);
            params["end"]=String.valueOf(end);
            image=openNmsGraphDs.adapter.getImage(url,params);
        }

        response.contentType="image/png";

        ImageIO.write(image, "png", response.outputStream);
        response.outputStream.flush()
        return;

    }
}