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
        config = new HashMap(config)
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
	            if(webResponse == null ) {
                    throw new Exception("Web response is not avaliable, web destination only usable from scripts or controllers");
                }

                InputStream inn = new ByteArrayInputStream(bytes);
                def image =  ImageIO.read(inn);

                ControllerUtils.drawImageToWeb (image,"image/png","png",webResponse);
           }
           else{
              String filename = destination
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

    /**
    *  returns the configuration map of specified rrd database
    */
    public static Map getDatabaseInfo(String dbName){
        createDirectory();
        return DbUtils.getDatabaseInfo(RRD_FOLDER + dbName);
    }

    private static def fetchDataInfoMap(dbName) {
        dbName = RRD_FOLDER + dbName
        def infoMap = [:]

        def arclist = DbUtils.fetchArchives(dbName)
        def dslist = DbUtils.fetchDatasources(dbName);
        def datasources = []
        for(int i=0; i<dslist.size(); i++){
            datasources.add(dslist[i][DbUtils.NAME]);
        }
        infoMap['function'] = arclist[0][DbUtils.FUNCTION];
        infoMap['datasources'] = datasources

        return infoMap
    }

    public static def fetchAllDataAsMap(dbName) {
        createDirectory();
        def info = fetchDataInfoMap(dbName)
        return DbUtils.fetchAllDataAsMap(RRD_FOLDER + dbName, info['datasources'], info['function']);
    }

    public static def fetchAllDataAsMap(dbName, datasource, function = 'notSpecified') {
        createDirectory();
        def info = fetchDataInfoMap(dbName)
        def datasources = []
        datasources.add(datasource)
        return DbUtils.fetchAllDataAsMap(RRD_FOLDER + dbName, datasources,
                                         function!='notSpecified'?function:info['function']);
    }

    public static def fetchAllDataAsMap(dbName, List datasources, function = 'notSpecified') {
        createDirectory();
        def info = fetchDataInfoMap(dbName)
        return DbUtils.fetchAllDataAsMap(RRD_FOLDER + dbName, datasources,
                                         function!='notSpecified'?function:info['function']);
    }

    public static def fetchDataAsMap(dbName, datasource, function,
                                      long startTime, long endTime) {
        createDirectory();
        def datasources =  []
        datasources.add(datasource)
        def startTimes = []
        startTimes.add(startTime)
        def endTimes = []
        endTimes.add(endTime)
        return DbUtils.fetchDataAsMap(RRD_FOLDER + dbName, datasources, function, startTimes, endTimes)
    }

    public static def fetchDataAsMap(dbName, datasource, long startTime, long endTime) {
        createDirectory();
        def datasources =  []
        datasources.add(datasource)
        def startTimes = []
        startTimes.add(startTime)
        def endTimes = []
        endTimes.add(endTime)
        def arclist = DbUtils.fetchArchives(dbName)
        def function = arclist[0][DbUtils.FUNCTION];
        return DbUtils.fetchDataAsMap(RRD_FOLDER + dbName, datasources, function, startTimes, endTimes)
    }

    public static def fetchDataAsMap(dbName, List datasources, function,
                                      long startTime, long endTime) {
        createDirectory();
        def startTimes = []
        startTimes.add(startTime)
        def endTimes = []
        endTimes.add(endTime)
        return DbUtils.fetchDataAsMap(RRD_FOLDER + dbName, datasources, function, startTimes, endTimes)
    }

    public static def fetchDataAsMap(dbName, datasource, function,
                                      List startTimes, List endTimes) {
        createDirectory();
        def datasources =  []
        datasources.add(datasource)
        return DbUtils.fetchDataAsMap(RRD_FOLDER + dbName, datasources, function, startTimes, endTimes)
    }

    public static def fetchDataAsMap(dbName, List datasources, function,
                                      List startTimes, List endTimes) {
        createDirectory();
        return DbUtils.fetchDataAsMap(RRD_FOLDER + dbName, datasources, function, startTimes, endTimes)
    }

    //this method is more useful for models
    public static def fetchAllDataAsMap(List dbNames, List datasources) {
        if(dbNames.size() != datasources.size()) {
            throw new Exception("number of database and number of datasources are not equal");
        }
        def result = [:]
        for(int i = 0; i < dbNames.size(); i++){
            result[datasources.get(i)] = fetchAllDataAsMap(dbNames.get(i), datasources.get(i)).get(datasources.get(i));
        }
        return result;
    }

    public static def fetchDataAsMap(List dbNames, List datasources, long startTime, long endTime){
        if(dbNames.size() != datasources.size()) {
            throw new Exception("number of database and number of datasources are not equal");
        }
        def result = [:]
        for(int i=0; i < dbNames.size(); i++){
            def arclist = DbUtils.fetchArchives(dbNames.get(i))
            def function = arclist[0][DbUtils.FUNCTION];
            result[datasources.get(i)] = fetchDataAsMap(dbNames.get(i), datasources.get(i),
                                                             function, startTime, endTime).get(datasources.get(i));
        }
        return result;
    }

    //this method is more useful for models
    public static def fetchDataAsMap(List dbNames, List datasources, function,
                                   long startTime, long endTime){
        if(dbNames.size() != datasources.size()) {
            throw new Exception("number of database and number of datasources are not equal");
        }
        def result = [:]
        for(int i=0; i < dbNames.size(); i++){
            result[datasources.get(i)] = fetchDataAsMap(dbNames.get(i), datasources.get(i),
                                                             function, startTime, endTime).get(datasources.get(i));
        }
        return result;
    }

    //this method is more useful for models
    public static def fetchDataAsMap(List dbNames, List datasources, function, List startTimes, List endTimes){
        createDirectory();
        if(dbNames.size() != datasources.size()) {
            throw new Exception("number of database and number of datasources are not equal");
        }
        Map result = [:];
        for(int i=0; i < dbNames.size(); i++){
            result[datasources.get(i)] = fetchDataAsMap(dbNames.get(i), datasources.get(i),
                                                             function, startTimes.get(i) as long, endTimes.get(i) as long).get(datasources.get(i));
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