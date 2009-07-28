package com.ifountain.rcmdb.rrd

import org.jrobin.core.RrdDef
import org.jrobin.core.RrdDb
import org.jrobin.core.DsDef
import org.jrobin.core.ArcDef
import org.jrobin.core.Sample
import org.jrobin.core.Archive
import org.jrobin.core.Datasource
import org.jrobin.core.FetchRequest
import org.jrobin.core.XmlWriter
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import org.jrobin.core.FetchData;

import groovy.xml.MarkupBuilder
import java.text.SimpleDateFormat


/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Jun 17, 2009
* Time: 9:37:31 AM
*/
class DbUtils {
    public static final String DATABASE_NAME = "databaseName";
    public static final String DATASOURCE = "datasource";
    public static final String START_TIME = "startTime";
    public static final String ARCHIVE = "archive";
    public static final String STEP = "step";

    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String HEARTBEAT= "heartbeat";
    public static final String MAX = "max";
    public static final String MIN = "min";

    public static final String FUNCTION = "function";
    public static final String XFF = "xff"
    public static final String STEPS = "steps"
    public static final String ROWS = "rows"

    /**
    * create a Round Robin database according to the given configuration map
    */
    public static void createDatabase(Map config) {

        if (!config.containsKey(DATABASE_NAME)) {
            throw new Exception("Database name is not specified");
        }
        if (!config.containsKey(DATASOURCE)) {
            throw new Exception("No Datasource specified");
        }
        if (!config.containsKey(ARCHIVE)) {
            throw new Exception("No archive specified");
        }

        RrdDef rrdDef = new RrdDef(config.get(DATABASE_NAME));

        if (config.containsKey(START_TIME) || config.containsKey("${START_TIME}"))
        {
            try {
                long ntime = (long)(config.get(START_TIME) / 1000)
                rrdDef.setStartTime(ntime)
            }
            catch (MissingMethodException e) {
                throw new Exception("Start time is not valid");
            }
        }

        if (config.containsKey(STEP))
        {
            try {
                rrdDef.setStep(config.get(STEP))
            }
            catch (MissingMethodException e) {
                throw new Exception("Step is not valid");
            }
        }

        try {
            rrdDef.addDatasource(getDsDefs(config.get(DATASOURCE)));
        }
        catch (Exception ex) {
            throw new Exception("At least one Datasource is distorted: " + ex.getMessage())
        }
        try {
            rrdDef.addArchive(getArcDefs(config.get(ARCHIVE)));
        }
        catch (Exception ex) {
            throw new Exception("At least one Archive is distorted: " + ex.getMessage())
        }

        RrdDb rrdDb = new RrdDb(rrdDef);
        rrdDb.close();
    }

    /**
    * removes database specified with its path from the system
    */
    public static void removeDatabase(String fileName) {
        File file = new File(fileName)
        if(file.exists())
            file.delete()
        else
            throw new Exception("File does not exists : " + fileName)
    }

    /**
    * checks whether the database file exists
    */
    public static boolean isDatabaseExists(String fileName) {
        return new File(fileName).exists()
    }

    /**
    *  returns the DsDef classes of given list of maps holding DsDef properties
    */
    private static DsDef[] getDsDefs(list) {
        def dsList = [];
        list.each{
           double min = Double.NaN
           double max = Double.NaN
           if(it.containsKey(MIN) ){
               min = it.get(MIN)
           }
           if(it.containsKey(MAX)){
               max = it.get(MAX)
           }
           DsDef dsTemp = new DsDef(it.get(NAME),it.get(TYPE),it.get(HEARTBEAT), min, max)
           dsList.add(dsTemp);
       }
       return dsList as DsDef[]
    }

    /**
    *  returns the ArcDef classes of given list of maps holding ArcDef properties
    */
    private static ArcDef[] getArcDefs(list) {
        def arcList = [];
        list.each{
           ArcDef arcTemp = new ArcDef(it.get(FUNCTION),it.get(XFF),(int)it.get(STEPS),(int)it.get(ROWS))
           arcList.add(arcTemp);
       }
       return arcList as ArcDef[]
    }

    private static def convertUpdateData(data) {
        def tokens = data.split(":")
        def timestamp = Long.parseLong(tokens[0])
        timestamp = (long)(timestamp / 1000)
        def newData = "" + timestamp

        for(int i = 1; i < tokens.size(); i++)
            newData = newData + ":" + tokens[i]
        return newData
    }

    /**
    * inserts a data to the rrd database
    * sample data is "timestamp:variable1:variable2:...:variablen"
    * e.g. : "978301200:200:1" or "978301200:200"
    */
    public synchronized static void updateData(String dbname, String data) {
        String[] dataList = new String[1]
        dataList[0] = data
        updateData(dbname, dataList)
    }

    /**
    *  inserts an array of data to the database at a time
    */
    public static void updateData(String dbName, String[] data) {
        executeAction(dbName){RrdDb rrdDb ->
            if(!(new File(dbName).exists())){
                throw new Exception("database file is not existent.")
            }
            Sample sample = rrdDb.createSample();
            for(int i=0; i<data.length; i++){
                sample.setAndUpdate( convertUpdateData(data[i]) );
            }
        }
    }

    /**
    * retrieves defined archived in speficied database
    * Notice: it is better to use overriden function fetchArchives(rrdDb) if an rrdDb is
    * already defined and the database is open. Otherwise this function will give
    * an exception since it cannot read the database
    */
    public static def fetchArchives(String dbName) {
        return executeAction(dbName) {RrdDb rrdDb ->
            def result = fetchArchives(rrdDb);
            return result;
        }
    }

    public static def fetchArchives(RrdDb rrdDb) {
        int arcCount = rrdDb.getArcCount();
        Archive[] archives = new Archive[arcCount];
        for(int i=0; i<arcCount; i++){
            archives[i] = rrdDb.getArchive(i);
        }
        def archiveList = [];
        for(int i=0; i<archives.length; i++){
            Map archiveMap = [:];
            archiveMap[DbUtils.FUNCTION] = archives[i].getConsolFun();
            archiveMap[DbUtils.STEPS] = archives[i].getSteps();
            archiveMap[DbUtils.ROWS] = archives[i].getRows();
            archiveMap[DbUtils.XFF] = archives[i].getXff();
            archiveMap[DbUtils.START_TIME] = archives[i].getStartTime();
            archiveList.add(archiveMap);
        }
        return archiveList;
    }

    /**
    * retrieves defined datasources in speficied database
    * Notice: it is better to use overriden function fetchDatasources(rrdDb) if an rrdDb is
    * already defined and the database is open. Otherwise this function will give
    * an exception since it cannot read the database
    */
    public static def fetchDatasources(String dbName) {
        return executeAction(dbName){ RrdDb rrdDb ->
            def result = fetchDatasources(rrdDb);
            return result;
        }
    }

    public static def fetchDatasources(RrdDb rrdDb) {
        int datCount = rrdDb.getDsCount();
        Datasource[] datasources = new Datasource[datCount];
        for(int i=0; i<datCount; i++){
            datasources[i] = rrdDb.getDatasource(i);
        }
        def datasourceList = [];
        for(int i=0; i<datasources.length; i++){
            Map datasourceMap = [:];
            datasourceMap[DbUtils.NAME] = datasources[i].getDsName();
            datasourceMap[DbUtils.TYPE] = datasources[i].getDsType();
            datasourceMap[DbUtils.HEARTBEAT] = datasources[i].getHeartbeat();
            datasourceMap[DbUtils.MAX] = datasources[i].getMaxValue();
            datasourceMap[DbUtils.MIN] = datasources[i].getMinValue();
            datasourceList.add(datasourceMap);
        }
        return datasourceList;
    }

    /**
    *  returns the configuration map of specified rrd database
    */
    public static Map getDatabaseInfo(String dbName) {
        return executeAction(dbName) {RrdDb rrdDb ->
            RrdDef rrdDef = rrdDb.getRrdDef();

            Map config = [:];
            config[DATABASE_NAME] = dbName;
            long max = 0
            long min = Long.MAX_VALUE
            int counter = 0;
            while(true)
            {
                try{
                    Archive archive = rrdDb.getArchive(counter++)
                if(archive.getStartTime() < min)
                    min = archive.getStartTime()
                if(archive.getEndTime() > max)
                    max = archive.getEndTime()
                }
                catch(ArrayIndexOutOfBoundsException e) { break;}
            }

            config[START_TIME] = min * 1000;
            config[Grapher.END_TIME] = max * 1000;

            config[STEP] = rrdDef.getStep();
            config[DATASOURCE] = fetchDatasources(rrdDb );
            config[ARCHIVE] = fetchArchives(rrdDb);
            return config;
        }
    }

    private static def getArchiveUpdateTimes(dbName, key) {
        return executeAction(dbName) {RrdDb rrdDb ->
            int archiveCount = rrdDb.getArcCount();
            def list = [];
            int counter = 0;
            for(int i=0; i<archiveCount; i++)
            {
                try{
                    Archive archive = rrdDb.getArchive(i)
                    list.add( ((key=='Last')?archive.getEndTime():archive.getStartTime()) * 1000 );
                }
                catch(ArrayIndexOutOfBoundsException e) { break;}
            }
            return list;
        }
    }

    /*
    * returns the endTimes of all archives
    */
    public static List getLastArchiveUpdateTimes(String dbName){
        return getArchiveUpdateTimes(dbName, 'Last')
    }

    /*
    * returns the startTimes of all archives
    */
    public static List getFirstArchiveUpdateTimes(String dbName){
        return getArchiveUpdateTimes(dbName, 'First')
    }

    public static def fetchAllDataAsMap(String dbName, List datasources, String function) {
        def startTimes = getFirstArchiveUpdateTimes (dbName);
        def endTimes = getLastArchiveUpdateTimes(dbName);
        return fetchDataAsMap(dbName, datasources, function, startTimes, endTimes);
    }

    public static def fetchDataAsMap(String dbName, List datasources, String function,
                                     List startTime, List endTime) {
        return executeAction(dbName){ RrdDb rrdDb ->
            Map values = [:];
            datasources.each{
                values[it] = [:];
            }
            for(int i=0; i<startTime.size(); i++){
                long nstarttime = (long)(startTime[i] / 1000)
                long nendtime = (long)(endTime[i] / 1000)
                datasources.each {
                    FetchRequest fetchRequest = rrdDb.createFetchRequest(function, nstarttime, nendtime);
                    fetchRequest.setFilter (it);
                    FetchData fd = fetchRequest.fetchData();
                    long[] timeStamps = fd.getTimestamps();
                    double[] dataValues = fd.getValues(it);
                    Map datasourceMap = [:];
                    for(int j=0; j<timeStamps.length; j++){
                         values[it][timeStamps[j]+""] =dataValues[j];
                    }
                }
            }
            return values;
        }
    }
    /*
        these are discared exporttoxml methods

    public static def fetchAllDataToXmlFile(String dbName, List datasources, String function, String xmlFile){
        return fetchAllDataToXml(dbName, datasources, function,xmlFile)
    }

    public static def fetchAllDataToXmlString(String dbName, List datasources, String function) {
        return fetchAllDataToXml(dbName, datasources, function)
    }

    public static def fetchDataToXmlFile(String dbName, List datasources, String function,
                                            List startTimes, List endTimes, String xmlFile) {
         return fetchDataToXml(dbName, datasources, function, startTimes, endTimes, xmlFile)
    }

    public static def fetchDataToXmlString(String dbName, List datasources, String function,
                                            List startTimes, List endTimes) {
        return fetchDataToXml(dbName, datasources, function, startTimes, endTimes)
    }

    private static def fetchAllDataToXml(String dbName, List datasources, String function, String xmlFile='') {
        Map data = fetchAllDataAsMap(dbName, datasources, function);
        def xml = createXml(data)
        if(xmlFile != '') {
            new File(xmlFile).append(xml)
        }
        return xml
    }

    private static def fetchDataToXml(String dbName, List datasources, String function,
                                     List startTimes, List endTimes, String xmlFile='') {
        Map data = fetchDataAsMap(dbName, datasources, function, startTimes, endTimes);
        def xml = createXml(data)
        if(xmlFile != '') {
            new File(xmlFile).append(xml)
        }
        return xml
    }

    private static def createXmlCommons(data, key='single') {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        def datasources = data.keySet().toArray()
        def keys = key=='multiple'?data[datasources[0]].keySet().toArray():data.keySet().toArray();
        Arrays.sort(keys)

        xml.rrd(){
            for(int i=0; i<keys.length; i++){
                xml.data() {
                    long timestamp = Long.parseLong(keys[i]) * 1000;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String dateString = formatter.format(new Date(timestamp));

                    xml.date(dateString)

                    if(key == 'multiple') {
                        datasources.each {
                            xml.value(data.get(it).get(keys[i]));
                        }
                        xml.volume(data.get(datasources[0]).get(keys[i]));
                    }
                    else {
                        xml.value(data.get(keys[i]));
                        xml.volume(data.get(keys[i]));
                    }
                }
            }
        }
        return writer.toString()
    }

    public static def createXml(Map data){
        try {
            long trialForLong = Long.parseLong(data.keySet().toArray()[0]);
        }
        catch (Exception ex){
            return createXmlCommons(data,'multiple');
        }
        return createXmlCommons(data)
    }
    */

    private static executeAction(String dbName, Closure closure) {
        RrdDb rrdDb = new RrdDb(dbName);
        try{
            return closure(rrdDb)
        }
        finally{
            rrdDb.close();
        }
    }

}