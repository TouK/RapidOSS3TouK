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

    public static final String RRD_VARIABLES = "rrdVariables";
    public static final String RRD_VARIABLE = "rrdVariable";
    public static final String GRAPH_TEMPLATE = "template";

    public static final String RRD_FOLDER = "rrdFiles/"
    public static final String DATABASE_NAME = "databaseName";
    public static final String DATASOURCE = "datasource";

    /**
    * create a Round Robin database according to the given configuration map
    */
    public static void createDatabase(Map config) {
        config[DbUtils.DATABASE_NAME] = RRD_FOLDER + config[DbUtils.DATABASE_NAME]
        createDirectory();
        DbUtils.createDatabase(config);
    }

    /**
    * removes database specified with its path from the system
    */
    public static void removeDatabase(String fileName) {
        createDirectory();
        String fname = RRD_FOLDER + fileName
        DbUtils.removeDatabase(fname) ;
    }

    /**
    * checks whether the database file exists
    */
    public static boolean isDatabaseExists(String fileName) {
        createDirectory();
        String fname = RRD_FOLDER + fileName
        return DbUtils.isDatabaseExists(fname);
    }

    /**
    * inserts a data to the rrd database
    * sample data is "timestamp:variable1:variable2:...:variablen"
    * e.g. : "978301200:200:1" or "978301200:200"
    */
    public static void updateData(String dbname, String data){
        createDirectory();
        String fname = RRD_FOLDER + dbname
        try {
            DbUtils.updateData(fname,  data);
        }
        catch(Exception e) {
            if(e.getMessage().indexOf("Bad sample timestamp")<0)
                throw new Exception(e)
        }
    }

    /**
    *  inserts an array of data to the database at a time
    */
    public static void updateData(dbname, data){
        createDirectory();
        String fname = RRD_FOLDER + dbname
        try {
        DbUtils.updateData( fname, data as String[]);
        }
        catch(Exception e) {
            if(e.getMessage().indexOf("Bad sample timestamp")<0)
                throw new Exception(e)
        }
    }

    public static byte[] graph(Map config){
        def rrdFileGraph = new File(RRD_FOLDER);
        rrdFileGraph.mkdirs();

        def bytes=null;

        if(!config.containsKey(DATASOURCE)){
            throw new Exception("no datasource specified.")
        }
        def datasourceList = config[DATASOURCE];
        datasourceList.each{
            it[DATABASE_NAME] = RRD_FOLDER + it[DATABASE_NAME];
        }

        bytes=Grapher.graph(config);

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
              String filename = RRD_FOLDER + destination
              createDirectory();
              Grapher.toFile (bytes, filename);
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
        createDirectory();
        return DbUtils.fetchArchives(RRD_FOLDER + dbName);
    }

    public static def fetchArchives(RrdDb rrdDb){
        return DbUtils.fetchArchives(rrdDb);
    }

    /**
    * retrieves defined datasources in speficied database
    * Notice: it is better to use overriden function fetchDatasources(rrdDb) if an rrdDb is
    * already defined and the database is open. Otherwise this function will give
    * an exception since it cannot read the database
    */
    public static def fetchDatasources(String dbName){
        createDirectory();
        return DbUtils.fetchDatasources(RRD_FOLDER + dbName);
    }

    public static def fetchDatasources(RrdDb rrdDb){
       return DbUtils.fetchDatasources(rrdDb);
    }

    /**
    *  returns the configuration map of specified rrd database
    */
    public static Map getDatabaseInfo(String dbName){
        createDirectory();
        return DbUtils.getDatabaseInfo(RRD_FOLDER + dbName);
    }

    /**
    *  returns first time series of first data point
    *  it is the easiest call if the database has only one data source
    */
    public static double[] fetchData(String dbName){
        createDirectory();
        return DbUtils.fetchData(RRD_FOLDER + dbName);
    }

    /**
    * returns the all datasources in the database according to the first archive method.
    */
    public static double[][] fetchAllData(String dbName){
        createDirectory();
        return DbUtils.fetchAllData(RRD_FOLDER + dbName);
    }

    /**
    * returns time series of one data index specified with its datasource name
    */
    public static double[] fetchData(String dbName, String datasource){
        createDirectory();
        return DbUtils.fetchData(RRD_FOLDER + dbName, datasource);
    }

    /**
    *  returns time series of one data index specified with its datasource name,
    * archive function of datasource, start time and end time
    */
    public static double[] fetchData(String dbName, String datasource, String function,
                                   long startTime, long endTime){
        createDirectory();
        return DbUtils.fetchData(RRD_FOLDER + dbName, datasource, function, startTime, endTime);
    }
    /**
    *  returns time series of data indexes specified with its datasource names
    */
    public static double[][] fetchData(String dbName, String[] datasources){
        createDirectory();
        return DbUtils.fetchData(RRD_FOLDER + dbName, datasources);
    }

    //this method is more useful for models
    public static double[][] fetchData(String[] dbName, String[] datasources){
        if(dbName.length != datasources.length) {
            throw new Exception("number of database and number of datasources are not equal");
        }
        double[][] result = new double[dbName.length][];
        for(int i=0; i<dbName.length; i++){
            result[i] = DbUtils.fetchData(RRD_FOLDER + dbName[i], datasources[i]);
        }
        return result;
    }

    /**
    *  returns time series of data indexes specified with its datasource names,
    * archive function of datasource, start time and end time
    */
    public static double[][] fetchData(String dbName, String[] datasources, String function,
                                   long startTime, long endTime){
        createDirectory();
        return DbUtils.fetchData(RRD_FOLDER + dbName, datasources, function, startTime, endTime);
    }

    //this method is more useful for models
    public static double[][] fetchData(String[] dbName, String[] datasources, String function,
                                   long startTime, long endTime){
        if(dbName.length != datasources.length) {
            throw new Exception("number of database and number of datasources are not equal");
        }
        double[][] result = new double[dbName.length][];
        for(int i=0; i<dbName.length; i++){
            result[i] = DbUtils.fetchData(RRD_FOLDER + dbName[i], datasources[i], function, startTime, endTime);
        }
        return result;
    }

    //====following fetch data methods return map[timestamp:value] as result====//
    public static Map fetchDataAsMap(String dbName){
        createDirectory();
        return DbUtils.fetchDataAsMap(RRD_FOLDER + dbName)
    }

    public static Map fetchDataAsMap(String dbName, String datasource){
        createDirectory();
        return DbUtils.fetchDataAsMap(RRD_FOLDER + dbName, datasource);
    }

    public static Map fetchDataAsMap(String dbName, String datasource, String function,
                                   long[] startTime, long[] endTime){
        createDirectory();
        return DbUtils.fetchDataAsMap(RRD_FOLDER + dbName, datasource, function, startTime, endTime);

    }

    public static Map fetchDataAsMap(String dbName, String[] datasources, String function,
                                   long[] startTime, long[] endTime){
        createDirectory();
        return DbUtils.fetchDataAsMap(RRD_FOLDER + dbName, datasources, function, startTime, endTime);
    }

    //this method is more useful for models
    public static Map fetchDataAsMap(String[] dbName, String[] datasources){
        createDirectory();
        if(dbName.length != datasources.length) {
            throw new Exception("number of database and number of datasources are not equal");
        }
        Map result = [:];
        for(int i=0; i<dbName.length; i++){
            result[datasources[i]] = DbUtils.fetchDataAsMap(RRD_FOLDER + dbName[i], datasources[i]);
        }
        return result;
    }

    //this method is more useful for models
    public static Map fetchDataAsMap(String[] dbName, String[] datasources, String function, long[] startTime, long[] endTime){
        createDirectory();
        if(dbName.length != datasources.length) {
            throw new Exception("number of database and number of datasources are not equal");
        }
        Map result = [:];
        for(int i=0; i<dbName.length; i++){
            result[datasources[i]] = DbUtils.fetchDataAsMap(RRD_FOLDER + dbName[i],
                    datasources[i], function, startTime[i], endTime[i]);
        }
        return result;
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

    private static void createDirectory(){
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
    }
}