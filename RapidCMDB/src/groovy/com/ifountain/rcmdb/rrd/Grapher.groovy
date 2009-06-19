package com.ifountain.rcmdb.rrd

import org.jrobin.core.RrdDef
import org.jrobin.graph.RrdGraphDef
import java.awt.Color
import org.jrobin.graph.RrdGraph

/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Jun 18, 2009
* Time: 9:24:26 AM
*/
class Grapher {
    static final String DATABASE_NAME = "dbname";
    static final String DATASOURCE = "datasource";
    static final String START_TIME = "startTime";
    static final String END_TIME = "endTime";

    static final String FUNCTION = "function";
    static final String VERTICAL_LABEL = "vlabel";
    static final String HORIZONTAL_LABEL = "hlabel";
    static final String LINE = "line";
    static final String AREA = "area";
    static final String HRULE = "hrule";
    static final String VRULE = "vrule";
    static final String RPN = "rpn";
    static final String DSNAME = "dsname";
    static final String NAME = "name";
    static final String COLOR = "color";
    static final String DESCRIPTION = "description";
    static final String THICKNESS = "thickness";

    public static byte[] graph(Map config){
        
        if (!config.containsKey(DATASOURCE)) {
            throw new Exception("No Datasource specified");
        }
        if (!config.containsKey(START_TIME)) {
            throw new Exception("Start time is not specified");
        }
        if (!config.containsKey(END_TIME)) {
            throw new Exception("End time is not specified");
        }

        RrdGraphDef graphDef = new RrdGraphDef()

        try{
            graphDef.setStartTime(config.get(START_TIME))
        }
        catch(MissingMethodException e)
        {
            throw new Exception("Invalid timestamps specified")
        }

        try{
            graphDef.setEndTime(config.get(END_TIME));
        }
        catch(MissingMethodException e)
        {
             throw new Exception("Invalid timestamps specified")
        }


        addDataSource(graphDef,config.get(DATASOURCE));

        if(config.containsKey(AREA) ){
            addArea(graphDef,config.get(AREA));
        }

        if(config.containsKey(LINE) ){
            addLine(graphDef,config.get(LINE));
        }

        RrdGraph graph = new RrdGraph(graphDef);

        return graph.getRrdGraphInfo().getBytes()
                
    }
    public static void addDataSource(RrdGraphDef rdef, dslist){
        boolean isSourceExist = false;
        dslist.each{
            if(!it.containsKey(NAME)){
                 throw new Exception("Datasource distorted: Name of datasource is not specified");    
            }
            try{
                rdef.datasource(it.get(NAME),it.get(RPN) )
            }
            catch(Exception ex){
                if(!(it.containsKey(DSNAME) &&
                        it.containsKey(DATABASE_NAME) &&   it.containsKey(FUNCTION))){
                    throw new Exception("Datasource distorted");
                }

                try{
                   rdef.datasource(it.get(NAME),it.get(DATABASE_NAME),it.get(DSNAME),it.get(FUNCTION) )
                   isSourceExist = true;
                }
                catch(Exception e){
                    e.printStackTrace()
                    throw new Exception("Datasource distorted: " + e.getMessage())
                }
            }
        }
        if(!isSourceExist){
            throw new Exception("There is no database selected")
        }
    }
    public static void addArea(RrdGraphDef rdef, alist){
         //sample area: graphDef.area("good", new Color(0, 0xFF, 0), "Good speed");
         alist.each{
            try{
                String colorStr = it.get(COLOR);
                if(colorStr.length()!=6){
                    throw new Exception("Invalid color: "+colorStr);
                }
                Color color;
                try{
                    int r = Integer.parseInt(colorStr.substring(0,2),16);
                    int g = Integer.parseInt(colorStr.substring(2,4),16);
                    int b = Integer.parseInt(colorStr.substring(4,6),16);
                    color = new Color(r,g,b );
                }catch(Exception e){
                    throw new Exception("Invalid color: "+colorStr);
                }
                rdef.area(it.get(NAME),color,it.get(DESCRIPTION) )
            }
            catch(Exception ex){
                throw new Exception("Area distorted: " + ex.getMessage()) ;
            }
        }
    }
    public static void addLine(RrdGraphDef rdef, llist){
         //sample line: graphDef.line("lineb", Color.GREEN, "Line B", 3);
         llist.each{
            try{
                String colorStr = it.get(COLOR);
                if(colorStr.length()!=6){
                    throw new Exception("Invalid color: "+colorStr);
                }
                Color color;
                try{
                    int r = Integer.parseInt(colorStr.substring(0,2),16);
                    int g = Integer.parseInt(colorStr.substring(2,4),16);
                    int b = Integer.parseInt(colorStr.substring(4,6),16);
                    color = new Color(r,g,b );
                }catch(Exception e){
                    throw new Exception("Invalid color: "+colorStr);
                }
                if(it.containsKey(THICKNESS) ){
                    rdef.line(it.get(NAME),color,it.get(DESCRIPTION),it.get(THICKNESS) );
                }else{
                    rdef.line(it.get(NAME),color,it.get(DESCRIPTION) );
                }
            }
            catch(Exception ex){
                throw new Exception("Line distorted: " + ex.getMessage()) ;
            }
        }
    }
}