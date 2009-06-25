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
    public static final String DATABASE_NAME = "dbname";
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
    public static final  String RRD_VARIABLES = "rrdVariables";
    public static final  String RRD_VARIABLE = "rrdVariable";

    /**
    * sets the following properties of graphdef:
    * title, width, height, maxValue, minValue
    */
    public static void setGeneralSettings(RrdGraphDef graphDef, Map config){
        if(config.containsKey(TITLE) ){
           graphDef.setTitle(config.get(TITLE));
        }
        if(config.containsKey(MAX) ){
           graphDef.setMaxValue (config.get(MAX));
        }
        if(config.containsKey(MIN) ){
           graphDef.setMinValue (config.get(MIN));
        }
        if(config.containsKey(HEIGHT) ){
           graphDef.setHeight (config.get(HEIGHT));
        }
        if(config.containsKey(WIDTH) ){
           graphDef.setWidth(config.get(WIDTH));
        }
    }

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

        if(config.containsKey(STACK) ){
            addStack(graphDef,config.get(STACK));
        }

        if(config.containsKey(MAX) ){
           graphDef.setMaxValue (config.get(MAX));
        }

        if(config.containsKey(MIN) ){
           graphDef.setMinValue (config.get(MIN));
        }

        if(config.containsKey(HEIGHT) ){
           graphDef.setHeight (config.get(HEIGHT));
        }

        if(config.containsKey(WIDTH) ){
           graphDef.setWidth(config.get(WIDTH));
        }
//        graphDef.setImageQuality (1F);
//        graphDef.setSmallFont(new java.awt.Font("Serif",java.awt.Font.BOLD, 12) )
        RrdGraph graph = new RrdGraph(graphDef);

        return graph.getRrdGraphInfo().getBytes()

    }

    private static def getRrdVariable(){
        return Grapher.class.classLoader.loadClass("RrdVariable");
    }
    private long getCurrentTime(){
        Calendar cal = Calendar.getInstance();
        return cal.getTimeInMillis();
    }
    static def loadClass(String className){
        return Grapher.class.classLoader.loadClass(className);
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
    public static void addStack(RrdGraphDef rdef, slist){
         //sample area: graphDef.area("good", new Color(0, 0xFF, 0), "Good speed");
         slist.each{
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
                rdef.stack(it.get(NAME),color,it.get(DESCRIPTION) )
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
    public static byte[] graphMultipleDatasources(Map config){
       String typeVar = "line";
       String colorVar = "999999";
       Map fConfig = getGeneralSettingsMap(config);

       if(config.containsKey(TYPE) ){
          typeVar = config.get(TYPE);
       }

       if(config.containsKey(COLOR) ){
          colorVar = config.get(COLOR);
       }

       if(!config.containsKey(RRD_VARIABLES) ){
           throw new Exception("No rrd variable is specified");
       }
       def rrdVariables = config.get(RRD_VARIABLES);


       def datasourceList = [];
       def typeList = [];
       for(int i=0; i<rrdVariables.size(); i++){
           def rrdVar = loadClass("RrdVariable").get(name:rrdVariables[i][RRD_VARIABLE]);
           rrdVar.archives.each{
               def datasourceMap = [:];
               datasourceMap[Grapher.NAME] = rrdVar.name;
               datasourceMap[Grapher.DATABASE_NAME] = rrdVar.file;
               datasourceMap[Grapher.DSNAME] = rrdVar.name;
               datasourceMap[Grapher.FUNCTION] = it.function;
               datasourceList.add(datasourceMap);
           }
           if(rrdVariables[i].containsKey(RPN) ){
               def datasourceMap = [:];
               datasourceMap[Grapher.NAME] = rrdVariables[i][RPN];
               datasourceMap[Grapher.RPN] = rrdVariables[i][RPN];
               datasourceList.add(datasourceMap);
           }
           def typeMap = [:];
           typeMap[NAME] = rrdVariables[i].containsKey(RPN) ? rrdVariables[i][RPN] : rrdVar.name
           typeMap[DESCRIPTION] = rrdVariables[i].containsKey(DESCRIPTION)?rrdVariables[i][DESCRIPTION]:rrdVar.name;
           typeMap[COLOR] = rrdVariables[i].containsKey(COLOR)?rrdVariables[i][COLOR]:colorVar;
           typeList.add(typeMap);
       }

       fConfig[typeVar] = typeList;
       fConfig[DATASOURCE] = datasourceList;
       return graph(fConfig);
    }
    
    public static byte[] graphOneVariable(Map config){
       String rrdVarName = config.get(RRD_VARIABLE);
       def rrdvar = loadClass("RrdVariable").get(name:rrdVarName);

       Map rVariable = [:];
       rVariable[RRD_VARIABLE] = config.get(RRD_VARIABLE);
       rVariable[DESCRIPTION] = config.containsKey(DESCRIPTION)?config.get(DESCRIPTION):rrdvar.name;
       if(config.containsKey(COLOR) ){
           rVariable[COLOR] = config.get(COLOR);
       }
       if(config.containsKey(TYPE)) {
           rVariable[TYPE] = config.get(TYPE);
       }
       if(config.containsKey(RPN)) {
           rVariable[RPN] = config.get(RPN);
       }

       config.remove (RRD_VARIABLE);
       if (config.containsKey(DESCRIPTION) ){
           config.remove (DESCRIPTION);
       }
       def vlist = [];
       vlist.add(rVariable);
       config[RRD_VARIABLES] = vlist;

       println config;

       return graphMultipleDatasources(config);

    }
    public static Map getGeneralSettingsMap(Map config){
       Map fConfig = [:];

       if(!config.containsKey(START_TIME) ){
           throw new Exception("Start time is not specified");
       }else {
           fConfig[START_TIME] = config.get(START_TIME);
       }
       if(!config.containsKey(END_TIME) ){
           fConfig[END_TIME] = getCurrentTime();
       }
       else{
           fConfig[END_TIME] = config.get(END_TIME);
       }
       if(config.containsKey(MAX) ){
          fConfig[MAX] = config.get(MAX);
       }
       if(config.containsKey(MIN) ){
          fConfig[MIN] = config.get(MIN);
       }
       if(config.containsKey(HEIGHT) ){
          fConfig[HEIGHT] = config.get(HEIGHT);
       }
       if(config.containsKey(WIDTH) ){
          fConfig[WIDTH] = config.get(WIDTH);
       }
       return fConfig;
    }

}