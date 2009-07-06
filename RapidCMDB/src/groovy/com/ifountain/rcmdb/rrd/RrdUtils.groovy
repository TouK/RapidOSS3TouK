package com.ifountain.rcmdb.rrd

import org.jrobin.core.RrdDb

import org.jrobin.graph.RrdGraphDef
import com.ifountain.rcmdb.domain.util.ControllerUtils
import javax.imageio.ImageIO

/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Jun 17, 2009
* Time: 9:37:31 AM
*/
class RrdUtils {

    public static final  String RRD_VARIABLES = "rrdVariables";
    public static final  String RRD_VARIABLE = "rrdVariable";
    public static final  String GRAPH_TEMPLATE = "template";

    /**
    * create a Round Robin database according to the given configuration map
    */
    public static void createDatabase(Map config) {
        DbUtils.createDatabase(config);
    }

    /**
    * removes database specified with its path from the system
    */
    public static void removeDatabase(String fileName) {
        DbUtils.removeDatabase( fileName) ;
    }

    /**
    * checks whether the database file exists
    */
    public static boolean isDatabaseExists(String fileName) {
        return DbUtils.isDatabaseExists(fileName);
    }

    /**
    * inserts a data to the rrd database
    * sample data is "timestamp:variable1:variable2:...:variablen"
    * e.g. : "978301200:200:1" or "978301200:200"
    */
    public static void updateData(String dbname, String data){
        DbUtils.updateData( dbname,  data);
    }

    /**
    *  inserts an array of data to the database at a time
    */
    public static void updateData(String dbname, String[] data){
        DbUtils.updateData( dbname, data);
    }

    public static byte[] graph(Map config){
        def bytes=null;

        if(config.containsKey(RRD_VARIABLE)  ){
            bytes=graphOneVariable(config);
        }
        else if(config.containsKey(RRD_VARIABLES)){
            bytes=graphMultipleDatasources(config);
        }
        else{
            bytes=Grapher.graph(config);
        }


        if(config.containsKey("destination")) {
           def destination=config["destination"];
           if( destination == 'web')
           {
	            def webResponse=ControllerUtils.getWebResponse();
	            if(webResponse == null )
                {
                    throw new Exception("Web response is not avaliable, web destination only usable from scripts or controllers");
                }

                InputStream inn = new ByteArrayInputStream(bytes);
                def image =  ImageIO.read(inn);

                ControllerUtils.drawImageToWeb (image,"image/png","png",webResponse);
           }
           else{
              Grapher.toFile (bytes, destination);
           }

        }

        return bytes;
    }

    /**
    * retrieves defined archived in speficied database
    * Notice: it is better to use overriden function fetchArchives(rrdDb) if an rrdDb is
    * already defined and the database is open. Otherwise this function will give
    * an exception since it cannot read the database
    */
    public static def fetchArchives(String dbName){
        return DbUtils.fetchArchives( dbName);
    }

    public static def fetchArchives(RrdDb rrdDb){
        return fetchArchives(rrdDb);
    }

    /**
    * retrieves defined datasources in speficied database
    * Notice: it is better to use overriden function fetchDatasources(rrdDb) if an rrdDb is
    * already defined and the database is open. Otherwise this function will give
    * an exception since it cannot read the database
    */
    public static def fetchDatasources(String dbName){
        return DbUtils.fetchDatasources(dbName);
    }

    public static def fetchDatasources(RrdDb rrdDb){
       return DbUtils.fetchDatasources(rrdDb);
    }

    /**
    *  returns the configuration map of specified rrd database
    */
    public static Map getDatabaseInfo(String dbName){
        return DbUtils.getDatabaseInfo(dbName);
    }

    /**
    *  returns first time series of first data point
    *  it is the easiest call if the database has only one data source
    */
    public static double[] fetchData(String dbName){
        return   DbUtils.fetchData(dbName);
    }

    /**
    * returns the all datasources in the database according to the first archive method.
    */
    public static double[][] fetchAllData(String dbName){
        return DbUtils.fetchAllData(dbName);
    }

    /**
    * returns time series of one data index specified with its datasource name
    */
    public static double[] fetchData(String dbName, String datasource){
        return DbUtils.fetchData(dbName, datasource);
    }

    /**
    *  returns time series of one data index specified with its datasource name,
    * archive function of datasource, start time and end time
    */
    public static double[] fetchData(String dbName, String datasource, String function,
                                   long startTime, long endTime){
        return DbUtils.fetchData(dbName, datasource, function, startTime, endTime);
    }
    /**
    *  returns time series of data indexes specified with its datasource names
    */
    public static double[][] fetchData(String dbName, String[] datasources){
        return DbUtils.fetchData(dbName, datasources);
    }

    /**
    *  returns time series of data indexes specified with its datasource names,
    * archive function of datasource, start time and end time
    */
    public static double[][] fetchData(String dbName, String[] datasources, String function,
                                   long startTime, long endTime){
        return DbUtils.fetchData(dbName, datasources, function, startTime, endTime);
    }

    /**
    * sets the following properties of graphdef:
    * title, width, height, maxValue, minValue
    */
    private static void setGeneralSettings(RrdGraphDef graphDef, Map config){
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
    private static def getRrdVariable(){
        return Grapher.class.classLoader.loadClass("RrdVariable");
    }
    private long getCurrentTime(){
        Calendar cal = Calendar.getInstance();
        return cal.getTimeInMillis();
    }
    private static def loadClass(String className){
        return Grapher.class.classLoader.loadClass(className);
    }
    public static def graphMultipleDatasources(Map config){
       String typeVar = "line";
       String colorVar = "999999";
       Map fConfig = getGeneralSettingsMap(config);

       if(config.containsKey(Grapher.TYPE) ){
          typeVar = config.get(Grapher.TYPE);
       }

       if(config.containsKey(Grapher.COLOR) ){
          colorVar = config.get(Grapher.COLOR);
       }

       if(!config.containsKey(RRD_VARIABLES) ){
           throw new Exception("No rrd variable is specified");
       }
       def rrdVariables = config.get(RRD_VARIABLES);

       def datasourceList = [];
       fConfig[Grapher.AREA] = [];
       fConfig[Grapher.LINE] = [];
       fConfig[Grapher.STACK] = [];
       def typeList = [];
       def rrdVar;
       for(int i=0; i<rrdVariables.size(); i++){
           rrdVar = loadClass("RrdVariable").get(name:rrdVariables[i][RRD_VARIABLE]);
           if(rrdVariables[i].containsKey(Grapher.FUNCTION) ){
               def datasourceMap = [:];
               datasourceMap[Grapher.NAME] = rrdVar.name;
               datasourceMap[Grapher.DATABASE_NAME] = rrdVar.file;
               datasourceMap[Grapher.DSNAME] = rrdVar.name;
               datasourceMap[Grapher.FUNCTION] = rrdVariables[i][Grapher.FUNCTION];
               datasourceList.add(datasourceMap);

           }else{
               rrdVar.archives.each{
                   def datasourceMap = [:];
                   datasourceMap[Grapher.NAME] = rrdVar.name;
                   datasourceMap[Grapher.DATABASE_NAME] = rrdVar.file;
                   datasourceMap[Grapher.DSNAME] = rrdVar.name;
                   datasourceMap[Grapher.FUNCTION] = it.function;
                   datasourceList.add(datasourceMap);
               }
           }
           if(rrdVariables[i].containsKey(Grapher.RPN) ){
               def datasourceMap = [:];
               datasourceMap[Grapher.NAME] = rrdVariables[i][Grapher.RPN];
               datasourceMap[Grapher.RPN] = rrdVariables[i][Grapher.RPN];
               datasourceList.add(datasourceMap);
           }
           def typeMap = [:];
           typeMap[Grapher.NAME] = rrdVariables[i].containsKey(Grapher.RPN) ? rrdVariables[i][Grapher.RPN] : rrdVar.name

           if(rrdVariables[i].containsKey(Grapher.DESCRIPTION))
                typeMap[Grapher.DESCRIPTION] = rrdVariables[i][Grapher.DESCRIPTION]
           else if(config.containsKey(Grapher.DESCRIPTION) && rrdVariables.size() == 1)
                typeMap[Grapher.DESCRIPTION] = config[Grapher.DESCRIPTION]
           else
                typeMap[Grapher.DESCRIPTION] = rrdVar.name;


           typeMap[Grapher.COLOR] = rrdVariables[i].containsKey(Grapher.COLOR)?rrdVariables[i][Grapher.COLOR]:colorVar;
           typeMap[Grapher.THICKNESS] = rrdVariables[i].containsKey(Grapher.THICKNESS) ? rrdVariables[i][Grapher.THICKNESS]:2;

           if(rrdVariables[i].containsKey(Grapher.TYPE) ){
               try{
                    fConfig[rrdVariables[i][Grapher.TYPE]].add(typeMap)
               }catch (Exception ex){
                   throw new Exception("Not valid type: "+ rrdVariables[i][Grapher.TYPE]);
               }
           }
           else{
               fConfig[typeVar].add(typeMap);
           }
       }
       Map dbInfo = DbUtils.getDatabaseInfo(rrdVar.file);
       if(!fConfig.containsKey (DbUtils.START_TIME)){
           fConfig[DbUtils.START_TIME] = dbInfo[DbUtils.START_TIME];
       }
       if(!fConfig.containsKey (Grapher.END_TIME)){
           fConfig[Grapher.END_TIME] = dbInfo[Grapher.END_TIME];
       }
       fConfig[Grapher.DATASOURCE] = datasourceList;

       byte[] bytes = Grapher.graph(fConfig);


       return bytes
    }
    public static byte[] graphOneVariable(Map config){
       if(!(config.get(RRD_VARIABLE) instanceof String )) {
           throw new Exception("Configuration map is distorted: RrdVariable should be an instance of string");
       }
       String rrdVarName = config.get(RRD_VARIABLE);
       def rrdvar = loadClass("RrdVariable").get(name:rrdVarName);
       if(rrdvar ==null){
           throw new Exception("RrdVariable \""+rrdVarName+"\" can not be found.");
       }

       Map rVariable = [:];
       rVariable[RRD_VARIABLE] = config.get(RRD_VARIABLE);
       rVariable[Grapher.DESCRIPTION] = config.containsKey(Grapher.DESCRIPTION)?config.get(Grapher.DESCRIPTION):rrdvar.name;
       if(config.containsKey(Grapher.COLOR) ){
           rVariable[Grapher.COLOR] = config.get(Grapher.COLOR);
       }
       if(config.containsKey(Grapher.THICKNESS)){
           rVariable[Grapher.THICKNESS] = config.get(Grapher.THICKNESS)
       }
       if(config.containsKey(Grapher.TYPE)) {
           rVariable[Grapher.TYPE] = config.get(Grapher.TYPE);
       }
       if(config.containsKey(Grapher.RPN)) {
           rVariable[Grapher.RPN] = config.get(Grapher.RPN);
       }

       config.remove (RRD_VARIABLE);
       if (config.containsKey(Grapher.DESCRIPTION) ){
           config.remove (Grapher.DESCRIPTION);
       }
       def vlist = [];
       vlist.add(rVariable);
       config[RRD_VARIABLES] = vlist;

       return graphMultipleDatasources(config);

    }
    private static Map getGeneralSettingsMap(Map config){

       Map fConfig = [:];
       if(config.containsKey(RrdUtils.GRAPH_TEMPLATE)){
           fConfig = getGeneralSettingsMapWithTemplate(config);
       }

       if(config.containsKey(Grapher.START_TIME) ){
           fConfig[Grapher.START_TIME] = config.get(Grapher.START_TIME);
       }
       if(config.containsKey(Grapher.END_TIME) ){
           fConfig[Grapher.END_TIME] = config.get(Grapher.END_TIME);
       }
       if(config.containsKey(Grapher.MAX) ){
          fConfig[Grapher.MAX] = config.get(Grapher.MAX);
       }
       if(config.containsKey(Grapher.MIN) ){
          fConfig[Grapher.MIN] = config.get(Grapher.MIN);
       }
       if(config.containsKey(Grapher.HEIGHT) ){
          fConfig[Grapher.HEIGHT] = config.get(Grapher.HEIGHT);
       }
       if(config.containsKey(Grapher.WIDTH) ){
          fConfig[Grapher.WIDTH] = config.get(Grapher.WIDTH);
       }
       if(config.containsKey(Grapher.TITLE) ){
          fConfig[Grapher.TITLE] = config.get(Grapher.TITLE);
       }
       if(config.containsKey(Grapher.VERTICAL_LABEL) ){
          fConfig[Grapher.VERTICAL_LABEL] = config.get(Grapher.VERTICAL_LABEL);
       }
       return fConfig;
    }
    private static Map getGeneralSettingsMapWithTemplate(Map config){
       Map fConfig = [:];

       def template = loadClass("RrdGraphTemplate").get(name:config.get(RrdUtils.GRAPH_TEMPLATE));

       if(template.max != Double.NaN )
          fConfig[Grapher.MAX] = template.max;

       if(template.min != Double.NaN )
          fConfig[Grapher.MIN] = template.min;

       fConfig[Grapher.HEIGHT] = (int)template.height;
       fConfig[Grapher.WIDTH] = (int)template.width;

       if(template.title.length()>0 )
          fConfig[Grapher.TITLE] = template.title;

       if(template.verticalLabel.length()>0 )
          fConfig[Grapher.VERTICAL_LABEL] = template.verticalLabel ;

       //note that they are not fConfig
       if(template.description.length()>0 )
          config[Grapher.DESCRIPTION] = template.description ;

       if(template.color.length()>0 )
          config[Grapher.COLOR] = template.color ;

       if(template.type.length()>0 )
          config[Grapher.TYPE] = template.type ;

       return fConfig;
    }

}