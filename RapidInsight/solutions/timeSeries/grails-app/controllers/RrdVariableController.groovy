import com.ifountain.rcmdb.rrd.*;
import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.awt.image.BufferedImage
import java.awt.Graphics
import java.awt.Color;

/**
 * User: ifountain
 * Date: Jul 1, 2009
 * Time: 2:18:27 PM
 */

class RrdVariableController {

    def index = {redirect(action: graph, params: params)}

    def graph = {
    	//take parameters:
    	Map config = [:];
    	Map rrdVariable = [:];
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

        config["destination"] = 'web'

    	try {
            def variable=RrdVariable.get(name:params.name);
            if(variable != null )
            {
                variable.graph(config);
            }
            else{
                throw new Exception("RrdVariable ${params.name} does not exist");
            }


        }
        catch(Exception e) {
            def image = new BufferedImage(500, 250, BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            g.setColor(new Color(255, 204, 204));
            g.fillRect(20, 10, 460, 200);
            g.setColor(new Color(77, 85, 95));
            g.drawString(e.getMessage()?e.getMessage():e.toString(), 50, 50);
            g.dispose();

            ControllerUtils.drawImageToWeb(image,"image/png", "png", response);
        }
    }

}