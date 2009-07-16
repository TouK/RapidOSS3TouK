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

    public static final String RRD_FOLDER = "rrdFiles/"

    /**
    * create a Round Robin database according to the given configuration map
    */
    public static void createDatabase(Map config) {
        config[DbUtils.DATABASE_NAME] = RRD_FOLDER + config[DbUtils.DATABASE_NAME]
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
        DbUtils.createDatabase(config);
    }

    /**
    * removes database specified with its path from the system
    */
    public static void removeDatabase(String fileName) {
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
        String fname = RRD_FOLDER + fileName
        DbUtils.removeDatabase(fname) ;
    }

    /**
    * checks whether the database file exists
    */
    public static boolean isDatabaseExists(String fileName) {
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
        String fname = RRD_FOLDER + fileName
        return DbUtils.isDatabaseExists(fname);
    }

    /**
    * inserts a data to the rrd database
    * sample data is "timestamp:variable1:variable2:...:variablen"
    * e.g. : "978301200:200:1" or "978301200:200"
    */
    public static void updateData(String dbname, String data){
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
        String fname = RRD_FOLDER + dbname
        DbUtils.updateData( fname,  data);
    }

    /**
    *  inserts an array of data to the database at a time
    */
    public static void updateData(String dbname, String[] data){
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
        String fname = RRD_FOLDER + dbname
        DbUtils.updateData( fname, data);
    }

    public static byte[] graph(Map config){
        def bytes=null;

        bytes=Grapher.graph(config);

        println "destination: "+config.containsKey("destination")
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
              def rrdFile = new File(RRD_FOLDER);
              println "written file:"+new File(filename).getAbsolutePath();
              rrdFile.mkdirs();
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
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
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
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
        return DbUtils.fetchDatasources(RRD_FOLDER + dbName);
    }

    public static def fetchDatasources(RrdDb rrdDb){
       return DbUtils.fetchDatasources(rrdDb);
    }

    /**
    *  returns the configuration map of specified rrd database
    */
    public static Map getDatabaseInfo(String dbName){
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
        return DbUtils.getDatabaseInfo(RRD_FOLDER + dbName);
    }

    /**
    *  returns first time series of first data point
    *  it is the easiest call if the database has only one data source
    */
    public static double[] fetchData(String dbName){
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
        return DbUtils.fetchData(RRD_FOLDER + dbName);
    }

    /**
    * returns the all datasources in the database according to the first archive method.
    */
    public static double[][] fetchAllData(String dbName){
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
        return DbUtils.fetchAllData(RRD_FOLDER + dbName);
    }

    /**
    * returns time series of one data index specified with its datasource name
    */
    public static double[] fetchData(String dbName, String datasource){
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
        return DbUtils.fetchData(RRD_FOLDER + dbName, datasource);
    }

    /**
    *  returns time series of one data index specified with its datasource name,
    * archive function of datasource, start time and end time
    */
    public static double[] fetchData(String dbName, String datasource, String function,
                                   long startTime, long endTime){
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
        return DbUtils.fetchData(RRD_FOLDER + dbName, datasource, function, startTime, endTime);
    }
    /**
    *  returns time series of data indexes specified with its datasource names
    */
    public static double[][] fetchData(String dbName, String[] datasources){
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
        return DbUtils.fetchData(RRD_FOLDER + dbName, datasources);
    }

    /**
    *  returns time series of data indexes specified with its datasource names,
    * archive function of datasource, start time and end time
    */
    public static double[][] fetchData(String dbName, String[] datasources, String function,
                                   long startTime, long endTime){
        def rrdFile = new File(RRD_FOLDER);
        rrdFile.mkdirs();
        return DbUtils.fetchData(RRD_FOLDER + dbName, datasources, function, startTime, endTime);
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
}