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

    private static def getArchiveUpdateTime(dbName, key) {
        return executeAction(dbName) {RrdDb rrdDb ->
            int archiveCount = rrdDb.getArcCount();

            long initialValue = (key == 'Last')?0L:Long.MAX_VALUE
            int counter = 0;
            for(int i=0; i<archiveCount; i++)
            {
                try{
                    Archive archive = rrdDb.getArchive(i)
                    if(key == 'Last' && archive.getEndTime() > initialValue)
                        initialValue = archive.getEndTime()
                    else if(key == 'First' && archive.getStartTime() < initialValue)
                        initialValue = archive.getStartTime()
                }
                catch(ArrayIndexOutOfBoundsException e) { break;}
            }
            return (initialValue * 1000) as Long;
        }
    }

    /*
    * returns the last archive-update time for all archives.
    */
    public static Long getLastArchiveUpdate(String dbName) {
        return getArchiveUpdateTime(dbName, 'Last')
    }

    /*
    * returns the earliest start time of archives.
    */
    public static Long getFirstArchiveUpdate(String dbName) {
        return getArchiveUpdateTime(dbName, 'First')
    }

    /*
    * returns the minimum step size (maximum resolution value) among all archives
    */
    public static Long getMinimumArchiveStep(String dbName) {
        return executeAction(dbName) {RrdDb rrdDb ->
            int archiveCount = rrdDb.getArcCount();

            long minStep = Long.MAX_VALUE;
            for(int i=0; i<archiveCount; i++)
            {
                try{
                    Archive archive = rrdDb.getArchive(i)
                    if(archive.getArcStep() < minStep)
                        minStep = archive.getArcStep()
                }
                catch(ArrayIndexOutOfBoundsException e) { break;}
            }
            return minStep;
        }
    }

    /**
    *  returns first time series of first data point
    *  it is the easiest call if the database has only one data source
    */
    public static double[] fetchData(String dbName) {
        return fetchAllData(dbName)[0];
    }

    private static def fetchDataInfoMap(dbName, key='normal') {
        return executeAction(dbName) {RrdDb rrdDb ->
            def infoMap = [:]
            infoMap['startTime'] = (key=='list')?getFirstArchiveUpdateTimes(dbName) as long[]:getFirstArchiveUpdate(dbName)
            infoMap['endTime'] = (key=='list')?getLastArchiveUpdateTimes(dbName) as long[]:getLastArchiveUpdate(dbName)

            def arclist = fetchArchives(rrdDb);
            def dslist = fetchDatasources(rrdDb);
            String[] datasources = new String[dslist.size()];
            for(int i=0; i<datasources.length; i++){
                datasources[i] = dslist[i][NAME];
            }

            infoMap['function'] = arclist[0][FUNCTION];
            infoMap['datasources'] = datasources as String[]

            return infoMap
        }
    }

    /**
    * returns the all datasources in the database according to the first archive method.
    */
    public static double[][] fetchAllData(String dbName) {
        def infoMap = fetchDataInfoMap(dbName)
        return fetchData(dbName, infoMap['datasources'], infoMap['function'], infoMap['startTime'], infoMap['endTime']);
    }

    /**
    * returns time series of one data index specified with its datasource name
    */
    public static double[] fetchData(String dbName, String datasource){
        def infoMap = fetchDataInfoMap(dbName)
        return fetchData(dbName, datasource, infoMap['function'], infoMap['startTime'], infoMap['endTime']);
    }

    /**
    *  returns time series of data indexes specified with its datasource names
    */
    public static double[][] fetchData(String dbName, String[] datasources){
        def infoMap = fetchDataInfoMap(dbName)
        return fetchData(dbName, datasources, infoMap['function'], infoMap['startTime'], infoMap['endTime']);
    }

    private static def fetchDataToDestination(String dbName, String[] datasource, String function,
                                      long startTime, long endTime, destination='return', xmlFile='') {
        return executeAction(dbName) {RrdDb rrdDb ->
            long nstarttime = (long)(startTime / 1000)
            long nendtime = (long)(endTime / 1000)

            def data

            try {
                FetchRequest fetchRequest = rrdDb.createFetchRequest(function, nstarttime, nendtime);
                fetchRequest.setFilter(datasource);

                data = fetchRequest.fetchData().getValues()

                if(destination == 'xml') {
                    fetchRequest.fetchData().exportXml (xmlFile);
                    convertXmlFile (xmlFile);
                }
            }
            catch(Exception e){
                throw new Exception(e.getMessage())
            }
            return data
        }
    }

    /**
    *  returns time series of one data index specified with its datasource name,
    * archive function of datasource, start time and end time
    */
    public static double[] fetchData(String dbName, String datasource, String function,
                                      long startTime, long endTime) {
        String[] datasources = new String[1]
        datasources[0] = datasource
        return fetchDataToDestination(dbName, datasources, function, startTime, endTime)[0] as double[]
    }

    /**
    *  returns time series of data indexes specified with its datasource names,
    * archive function of datasource, start time and end time
    */
    public static double[][] fetchData(String dbName, String[] datasources, String function,
                                        long startTime, long endTime) {
        return fetchDataToDestination(dbName, datasources, function, startTime, endTime) as double[][]
    }

    //===================================================================================//

    /**
    * returns the all datasources in the database according to the first archive method.
    * this method also writes an xml file to the given destination file compatible with
    * flex chart application
    */
    public static Map fetchDataToXml(String dbName, String xmlFile){
        Map data = fetchDataAsMap (dbName);
        createXml(data, xmlFile);
        return data;
    }

    /**
    * returns time series of one data index specified with its datasource name
    */
    public static Map fetchDataToXml(String dbName, String datasource, String xmlFile){
        Map data = fetchDataAsMap (dbName, datasource);
        createXml(data, xmlFile);
        return data;
    }

    /**
    *  returns time series of one data index specified with its datasource name,
    * archive function of datasource, start time and end time
    */
    public static Map fetchDataToXml(String dbName, String datasource, String function,
                                   long startTime, long endTime, String xmlFile){
        Map data = fetchDataAsMap (dbName, datasource, function, startTime, endTime);
        createXml(data, xmlFile);
        return data;
    }
    /**
    *  returns time series of data indexes specified with its datasource names
    */
    public static Map fetchDataToXml(String dbName, String[] datasources, String xmlFile){
        Map data = [:];
        for(int i; i<datasources.length; i++){
            data[datasources[i]] = fetchDataAsMap(dbName,datasources[i]);
        }
        createXml(data, xmlFile);
        return data;
    }

    /**
    *  returns time series of data indexes specified with its datasource names,
    * archive function of datasource, start time and end time
    */
    public static Map fetchDataToXml(String dbName, String[] datasources, String function,
                                   long startTime, long endTime, String xmlFile){
        Map data = [:];
        for(int i=0; i<datasources.length; i++){
            data[datasources[i]] = fetchDataAsMap(dbName,datasources[i], function, startTime, endTime);
        }
        createXml(data, xmlFile);
        return data;
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
            return list as Long[];
        }
    }

    /*
    * returns the endTime s of all archives
    */
    public static Long[] getLastArchiveUpdateTimes(String dbName){
        return getArchiveUpdateTimes(dbName, 'Last')
    }

    /*
    * returns the startTime s of all archives
    */
    public static Long[] getFirstArchiveUpdateTimes(String dbName){
        return getArchiveUpdateTimes(dbName, 'First')
    }

    /**
    * this method retrieves the data of all archives with a map.
    * Map keys are timestamps (as String) values are data
    * since data are coming from all archives, increment of timestamps are not regular.
    */
    public static Map fetchDataAsMap(String dbName){
        def infoMap = fetchDataInfoMap(dbName, 'list')
        return fetchDataAsMap(dbName, infoMap['datasources'], infoMap['function'],
                              infoMap['startTime'], infoMap['endTime'])
    }
    public static Map fetchDataAsMap(String dbName, String datasource){
        def infoMap = fetchDataInfoMap(dbName, 'list')
        return fetchDataAsMap(dbName, datasource, infoMap['function'],
                              infoMap['startTime'], infoMap['endTime'])
    }
    /**
    * this method retrieves the data with a map. Map keys are timestamps (as String) values are data
    */
    public static Map fetchDataAsMap(String dbName, String[] datasources, String function,
                                   long[] startTime, long[] endTime){
        return executeAction(dbName){ RrdDb rrdDb ->
            FetchRequest fetchRequest;
            FetchData fd;
            Map values = [:];
            datasources.each{
                values[it] = [:];
            }
            for(int i=0; i<startTime.length; i++){
                long nstarttime = (long)(startTime[i] / 1000)
                long nendtime = (long)(endTime[i] / 1000)

                datasources.each {
                    fetchRequest = rrdDb.createFetchRequest(function, nstarttime, nendtime);
                    fetchRequest.setFilter (it);
                    fd = fetchRequest.fetchData();
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

    public static Map fetchDataAsMap(String dbName, String datasource, String function,
                                   long[] startTime, long[] endTime){
        String[] datasources = new String[1]
        datasources[0] = datasource
        return fetchDataAsMap(dbName, datasources, function, startTime, endTime).get(datasource)
    }

    public static Map fetchDataAsMap(String dbName, String datasource, String function,
                                   long startTime, long endTime) {
        String[] datasources = new String[1]
        datasources[0] = datasource

        long[] startTimes = new long[1]
        startTimes[0] = startTime

        long[] endTimes = new long[1]
        endTimes[0] = endTime

        return fetchDataAsMap(dbName, datasources, function, startTimes, endTimes).get(datasource)
    }

    private static def createXmlCommons(data, xmlFile, key='single') {
        String newXmlFile = xmlFile+"new.xml";
        XmlWriter xwriter = new XmlWriter(new DataOutputStream(new FileOutputStream(newXmlFile) ) );
        xwriter.startTag("rrd");

        String[] datasources = data.keySet().toArray();

        String[] keyArray = key=='multiple'?data[datasources[0]].keySet().toArray():data.keySet().toArray();
        Arrays.sort(keyArray);

        for(int i=0; i<keyArray.length; i++){
            xwriter.startTag("data");
            long timestamp = Long.parseLong(keyArray[i]);
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            long timeLong = timestamp*1000L;
            String dateString = formatter.format(new Date(timeLong));

            xwriter.writeTag ("date",dateString);
            if(key == 'multiple') {
                datasources.each {
                    xwriter.writeTag ("value",data.get(it).get(keyArray[i]));
                }
                xwriter.writeTag ("volume",data.get(datasources[0]).get(keyArray[i]));
            }
            else {
                xwriter.writeTag ("value",data.get(keyArray[i]));
                xwriter.writeTag ("volume",data.get(keyArray[i]));
            }
            xwriter.closeTag();
        }

        xwriter.closeTag();
        writeNewFileToOldFile (xmlFile,newXmlFile);
    }

    /*
    * creates an xml file compatible with flex chart application according to the given map
    * map's keys comprise of timestamps (as String) and values are data.
    */
    public static void createXml(Map data, String xmlFile){
        try {
            long trialForLong = Long.parseLong(data.keySet().toArray()[0]);
        }
        catch (Exception ex){
            createXmlCommons(data,xmlFile,'multiple');
            return;
        }
        createXmlCommons(data, xmlFile)
    }
   
    private static void convertXmlFile(String xmlFile){
        //  retrieve xml file to be converted
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(xmlFile);
        org.w3c.dom.Node node = document.getDocumentElement();
        node = node.getElementsByTagName ("data").item (0);
        org.w3c.dom.NodeList dataList = node.getElementsByTagName ("row");

        //creates new xml file
        String newXmlFile = xmlFile+"new.xml";
        XmlWriter xwriter = new XmlWriter(new DataOutputStream(new FileOutputStream(newXmlFile) ) );
        xwriter.startTag("rrd");

        for(int i=0; i<dataList.length; i++){
            xwriter.startTag("data");

            org.w3c.dom.Node data = dataList.item(i);
            String timestamp = data.getElementsByTagName("timestamp").item(0).getFirstChild().getNodeValue();
            org.w3c.dom.Node values = data.getElementsByTagName("values").item(0);
            String value = values.getElementsByTagName("v").item(0).getFirstChild().getNodeValue();

            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            long timeLong = Long.parseLong(timestamp)*1000L;

            String dateString = formatter.format(new Date(timeLong));

            xwriter.writeTag ("date",dateString);
            xwriter.writeTag ("value",value);
            xwriter.writeTag ("volume",value);

            xwriter.closeTag();
        }

        xwriter.closeTag();

        writeNewFileToOldFile (xmlFile,newXmlFile);
    }

    private static writeNewFileToOldFile(String oldFile, String newFile){
        DataInputStream dis = new DataInputStream(new FileInputStream(newFile));
        long length = new File(newFile).length();
        byte[] content = new byte[length];
        dis.readFully(content);
        dis.close();
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(oldFile));
        dos.write (content);
        dos.close();
        new File(newFile).delete();
    }

    public static String getValueFromXml(String fileName, long timestamp){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(fileName);
        org.w3c.dom.Node node = document.getDocumentElement();
        org.w3c.dom.NodeList list = node.getElementsByTagName ("data");

        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = formatter.format(timestamp);

        String xmlDate;

        for(int i=0; i<list.length; i++){
            org.w3c.dom.Node data = list.item (i);
            org.w3c.dom.Node dataDate = data.getElementsByTagName("date").item(0)
            xmlDate = dataDate.getFirstChild().getNodeValue();
            if(dateString.equals(xmlDate) ){
                String value = data.getElementsByTagName("value").item(0).getFirstChild().getNodeValue();
                return value;
            }
        }
        return null;
    }

    private static executeAction(String dbName, Closure closure)
    {
        RrdDb rrdDb = new RrdDb(dbName);
        try{
            return closure(rrdDb)
        }
        finally{
            rrdDb.close();
        }
    }

}