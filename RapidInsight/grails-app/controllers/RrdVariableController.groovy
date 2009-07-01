import javax.imageio.ImageIO
import java.io.*;
import java.io.ByteArrayInputStream;
import com.ifountain.rcmdb.rrd.*;
import com.ifountain.rcmdb.domain.util.ControllerUtils;

/**
 * User: ifountain
 * Date: Jul 1, 2009
 * Time: 2:18:27 PM
 */

class RrdVariableController {

    def index = {redirect(action: show, params: params)}

    def graph = {
    	//take parameters:
    	Map config = [:];
    	Map rrdVariable = [:];
    	if(params.name != null){
    		config[RrdUtils.RRD_VARIABLE] = params.name;
    	}
    	if(params.template != null){
      		config[RrdUtils.GRAPH_TEMPLATE] = params.template;
     	}
    	if(params.title != null){
    		config[Grapher.TITLE] = params.title;
    	}
    	if(params.color != null){
    		config[Grapher.COLOR] = params.color;
    	}
    	if(params.thickness){
    		config[Grapher.THICKNESS] = params.thickness;
    	}
    	if(params.type != null){
    		config[Grapher.TYPE] = params.type;
    	}
    	if(params.rpn != null){
    		config[Grapher.RPN] = params.rpn;
    	}
    	if(params.startTime != null){
    		config[DbUtils.START_TIME] = Long.parseLong(params.startTime);
    	}
    	if(params.endTime != null){
    		config[Grapher.END_TIME] = Long.parseLong(params.endTime);
    	}
    	if(params.verticalLabel != null){
    		config[Grapher.VERTICAL_LABEL] = params.verticalLabel;
    	}
    	if(params.description != null){
    		config[Grapher.DESCRIPTION] = params.description;
    	}

        config["destination"] = 'web' /*web olcakmis:D*/

    	try{
		    byte[] rowData = RrdUtils.graph(config);
		    if (rowData != null) {
	            InputStream inn = new ByteArrayInputStream(rowData);
	            def image =  ImageIO.read(inn);
	            String contentType = "image/png";
	            ControllerUtils.drawImageToWeb(image,contentType,response);
	        }
        }
        catch(Exception e){
            

        }
    }

}