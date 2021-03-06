package com.ifountain.rcmdb.rrd

import org.jrobin.core.RrdDef
import org.jrobin.graph.RrdGraphDef
import java.awt.Color
import org.jrobin.graph.RrdGraph
import org.jrobin.core.RrdDb

/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Jun 18, 2009
* Time: 9:24:26 AM
*/
class Grapher {
    public static final String DATABASE_NAME = "databaseName";
    public static final String DATASOURCE = "datasource";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";

    public static final String FUNCTION = "function";
    public static final String VERTICAL_LABEL = "vlabel";
    public static final String HORIZONTAL_LABEL = "hlabel";
    public static final String LINE = "line";
    public static final String AREA = "area";
    public static final String STACK = "stack";
    public static final String HRULE = "hrule";
    public static final String VRULE = "vrule";
    public static final String RPN = "rpn";
    public static final String DSNAME = "dsname";
    public static final String NAME = "name";
    public static final String COLOR = "color";
    public static final String DESCRIPTION = "description";
    public static final String THICKNESS = "thickness";
    public static final String TITLE = "title";
    public static final String MAX = "max";
    public static final String MIN = "min";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";

    public static final  String TYPE = "type";
    public static final def colorList = ["0000ff", "00ff00", "ff0000",
                                      "ffff00","ff00ff","00ffff",
                                      "888888",
                                      "ff8800","ff0088","00ff88"];
    public static int colorIndex = 0;

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

        RrdGraphDef graphDef = new RrdGraphDef();

        try{
            long ntime = (long)(config.get(START_TIME) / 1000)
            graphDef.setStartTime(ntime)
        }
        catch(MissingMethodException e)
        {
            throw new Exception("Invalid timestamps specified")
        }

        try{
            long ntime = (long)(config.get(END_TIME) / 1000)
            graphDef.setEndTime(ntime);
        }
        catch(MissingMethodException e)
        {
             throw new Exception("Invalid timestamps specified")
        }
        if(config.containsKey(DbUtils.DATABASE_NAME)){
            config.get(DATASOURCE).each{
                if(!it.containsKey(DATABASE_NAME)){
                    it[DATABASE_NAME] = config.get(DATABASE_NAME);
                }
            }
        }


        addDataSource(graphDef,config.get(DATASOURCE));

        if(config.containsKey(AREA) ){
            addArea(graphDef,config.get(AREA));
        }

        if(config.containsKey(LINE) ){
            addLine(graphDef,config.get(LINE));
        }

        if(config.containsKey(STACK) ){
            addStack(graphDef,config.get(STACK));
        }

        setGeneralSettings(graphDef,config);
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
                if(!(it.containsKey(DSNAME) && it.containsKey(FUNCTION) &&
                        it.containsKey(DATABASE_NAME)  )){
                    throw new Exception("Datasource distorted");
                }
                try{
                   rdef.datasource(it.get(NAME),it.get(DATABASE_NAME),it.get(DSNAME),it.get(FUNCTION) )
                   isSourceExist = true;
                }
                catch(Exception e){
                    throw new Exception("Datasource distorted: " + e.getMessage())
                }
            }
        }
        if(!isSourceExist){
            throw new Exception("There is no database selected")
        }
    }

    public static String getColor(){
        String colorStr = colorList[colorIndex];
        colorIndex = (colorIndex+1)%10;
        return colorStr; 
    }

    private static def retriveColor(element){
        String colorStr = element.get(COLOR);
        if(colorStr == null ){
            colorStr = getColor();
        }
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
        return color
    }
    
    public static void addArea(RrdGraphDef rdef, alist){
         //sample area: graphDef.area("good", new Color(0, 0xFF, 0), "Good speed");
         alist.each{
            try{
                rdef.area(it.get(NAME),retriveColor(it),it.get(DESCRIPTION) )
            }
            catch(Exception ex){
                throw new Exception("Area distorted: " + ex.getMessage()) ;
            }
        }
    }

    public static void addStack(RrdGraphDef rdef, slist){
         //sample area: graphDef.area("good", new Color(0, 0xFF, 0), "Good speed");
         slist.each{
            try{
                rdef.stack(it.get(NAME),retriveColor(it),it.get(DESCRIPTION) )
            }
            catch(Exception ex){
                throw new Exception("Stack distorted: " + ex.getMessage()) ;
            }
        }
    }

    public static void addLine(RrdGraphDef rdef, llist){
         //sample line: graphDef.line("lineb", Color.GREEN, "Line B", 3);
         llist.each{
            try{
                if(it.containsKey(THICKNESS) ){
                    rdef.line(it.get(NAME),retriveColor(it),it.get(DESCRIPTION),it.get(THICKNESS) );
                }else{
                    rdef.line(it.get(NAME),retriveColor(it),it.get(DESCRIPTION) );
                }
            }
            catch(Exception ex){
                throw new Exception("Line distorted: " + ex.getMessage()) ;
            }
        }
    }

    public static void setGeneralSettings(RrdGraphDef graphDef, Map config){
        if(config.containsKey(Grapher.TITLE) ){
           graphDef.setTitle(config.get(Grapher.TITLE));
        }
        if(config.containsKey(Grapher.MAX) ){
           graphDef.setMaxValue (config.get(Grapher.MAX));
        }
        if(config.containsKey(Grapher.MIN) ){
           graphDef.setMinValue (config.get(Grapher.MIN));
        }
        if(config.containsKey(Grapher.HEIGHT) ){
           graphDef.setHeight (config.get(Grapher.HEIGHT));
        }
        if(config.containsKey(Grapher.WIDTH) ){
           graphDef.setWidth(config.get(Grapher.WIDTH));
        }

        if(config.containsKey(Grapher.VERTICAL_LABEL) ){
           graphDef.setVerticalLabel(config.get(Grapher.VERTICAL_LABEL));
        }
    }

    public static void toFile(byte[] bytes, String path) {
        DataOutputStream outputStream = new DataOutputStream( new FileOutputStream(path))
        outputStream.write(bytes)
        outputStream.flush()
        outputStream.close()
    }

}
