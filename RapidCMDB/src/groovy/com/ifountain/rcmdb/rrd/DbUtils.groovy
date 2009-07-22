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
import java.text.DecimalFormat
import org.jrobin.core.Util.Xml;
import org.jrobin.core.Util
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import org.jrobin.data.DataProcessor
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
        } catch (Exception ex) {
            throw new Exception("At least one Datasource is distorted: " + ex.getMessage())
        }
        try {
            rrdDef.addArchive(getArcDefs(config.get(ARCHIVE)));
        } catch (Exception ex) {
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
    private static DsDef[] getDsDefs(list){
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
    private static ArcDef[] getArcDefs(list){
        def arcList = [];
        list.each{
           ArcDef arcTemp = new ArcDef(it.get(FUNCTION),it.get(XFF),(int)it.get(STEPS),(int)it.get(ROWS))
           arcList.add(arcTemp);
       }
       return arcList as ArcDef[]
    }

    /**
    * inserts a data to the rrd database
    * sample data is "timestamp:variable1:variable2:...:variablen"
    * e.g. : "978301200:200:1" or "978301200:200"
    */
    public static void updateData(String dbname, String data){
        if(!new File(dbname).exists()){
            throw new Exception("database file is not existent: "+dbname)
        }
        RrdDb rrdDb = new RrdDb(dbname);

        Sample sample = rrdDb.createSample();

        String[] stime = data.split(":")

        long ntime = Long.parseLong(stime[0]);
        ntime = (long)(ntime / 1000)

        String ndata = "" + ntime
        for(int i = 1; i < stime.length; i++){
            ndata = ndata + ":" + stime[i]
        }

        sample.setAndUpdate(ndata);
        rrdDb.close();
    }

    /**
    *  inserts an array of data to the database at a time
    */
    public static void updateData(String dbname, String[] data){
        if(!(new File(dbname).exists())){
            throw new Exception("database file is not existent.")
        }
        RrdDb rrdDb = new RrdDb(dbname);

        Sample sample = rrdDb.createSample();
        for(int i=0; i<data.length; i++){
            String[] stime = data[i].split(":")
            long ntime = Long.parseLong(stime[0]);
            ntime = (long)(ntime / 1000)

            String ndata = "" + ntime
            for(int j = 1; j < stime.length; j++){
                ndata = ndata + ":" + stime[j]
            }
            sample.setAndUpdate(ndata);
        }
        rrdDb.close();
    }

    /**
    * retrieves defined archived in speficied database
    * Notice: it is better to use overriden function fetchArchives(rrdDb) if an rrdDb is
    * already defined and the database is open. Otherwise this function will give
    * an exception since it cannot read the database
    */
    public static def fetchArchives(String dbName){
        RrdDb rrdDb = new RrdDb(dbName);
        def result = fetchArchives(rrdDb);
        rrdDb.close();
        return result;
    }

    public static def fetchArchives(RrdDb rrdDb){
        int arcCount = rrdDb.getArcCount();
        Archive[] arcs = new Archive[arcCount];
        for(int i=0; i<arcCount; i++){
            arcs[i] = rrdDb.getArchive(i);
        }
        def alist = [];
        for(int i=0; i<arcs.length; i++){
            Map m = [:];

            m[DbUtils.FUNCTION] = arcs[i].getConsolFun();
            m[DbUtils.STEPS] = arcs[i].getSteps();
            m[DbUtils.ROWS] = arcs[i].getRows();
            m[DbUtils.XFF] = arcs[i].getXff();
            m[DbUtils.START_TIME] = arcs[i].getStartTime();

            alist.add(m);
        }

        return alist;
    }

    /**
    * retrieves defined datasources in speficied database
    * Notice: it is better to use overriden function fetchDatasources(rrdDb) if an rrdDb is
    * already defined and the database is open. Otherwise this function will give
    * an exception since it cannot read the database
    */
    public static def fetchDatasources(String dbName){
        RrdDb rrdDb = new RrdDb(dbName);
        def result = fetchDatasources(rrdDb);
        rrdDb.close();
        return result;
    }

    public static def fetchDatasources(RrdDb rrdDb){
        int datCount = rrdDb.getDsCount();
        Datasource[] dats = new Datasource[datCount];
        for(int i=0; i<datCount; i++){
            dats[i] = rrdDb.getDatasource(i);
        }

        def dslist = [];
        for(int i=0; i<dats.length; i++){
            Map m = [:];
            m[DbUtils.NAME] = dats[i].getDsName();
            m[DbUtils.TYPE] = dats[i].getDsType();
            m[DbUtils.HEARTBEAT] = dats[i].getHeartbeat();
            m[DbUtils.MAX] = dats[i].getMaxValue();
            m[DbUtils.MIN] = dats[i].getMinValue();

            dslist.add(m);
        }
        return dslist;
    }

    /**
    *  returns the configuration map of specified rrd database
    */
    public static Map getDatabaseInfo(String dbName){
        RrdDb rrdDb = new RrdDb(dbName);
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

        rrdDb.close();
        return config;
    }
    /*
    * returns the last archive-update time for all archives.
    */
    public static Long getLastArchiveUpdate(String dbName){
        RrdDb rrdDb = new RrdDb(dbName);
        int archiveCount = rrdDb.getArcCount();

        long max = 0
        int counter = 0;
        for(int i=0; i<archiveCount; i++)
        {
            try{
                Archive archive = rrdDb.getArchive(i)
                if(archive.getEndTime() > max)
                    max = archive.getEndTime()
            }
            catch(ArrayIndexOutOfBoundsException e) { break;}
        }
        rrdDb.close();
        return (max * 1000);
    }
    /*
    * returns the earliest start time of archives.
    */
    public static Long getFirstArchiveUpdate(String dbName){
        RrdDb rrdDb = new RrdDb(dbName);
        int archiveCount = rrdDb.getArcCount();

        long min = Long.MAX_VALUE;
        int counter = 0;
        for(int i=0; i<archiveCount; i++)
        {
            try{
                Archive archive = rrdDb.getArchive(i)
                if(archive.getStartTime() < min)
                    min = archive.getStartTime()
            }
            catch(ArrayIndexOutOfBoundsException e) { break;}
        }
        rrdDb.close();
        return (min * 1000);
    }
    /*
    * returns the minimum step size (maximum resolution value) among all archives
    */
    public static Long getMinimumArchiveStep(String dbName){
        RrdDb rrdDb = new RrdDb(dbName);
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
        rrdDb.close();
        return minStep;
    }
    /**
    *  returns first time series of first data point
    *  it is the easiest call if the database has only one data source
    */
    public static double[] fetchData(String dbName){
        return   fetchAllData(dbName)[0];
    }

    /**
    * returns the all datasources in the database according to the first archive method.
    */
    public static double[][] fetchAllData(String dbName){
        long startTime = getFirstArchiveUpdate(dbName);
        long endTime = getLastArchiveUpdate(dbName);
        RrdDb rrdDb = new RrdDb(dbName);
        def arclist = fetchArchives(rrdDb);
        def dslist = fetchDatasources(rrdDb);
        String[] datasources = new String[dslist.size()];
        for(int i=0; i<datasources.length; i++){
            datasources[i] = dslist[i][NAME];
        }
//        long endTime = rrdDb.getLastUpdateTime() * 1000;
        rrdDb.close();
        String function = arclist[0][FUNCTION];
//        long startTime = arclist[0][START_TIME] * 1000;
        return fetchData(dbName, datasources, function, startTime, endTime);
    }

    /**
    * returns time series of one data index specified with its datasource name
    */
    public static double[] fetchData(String dbName, String datasource){
        long startTime = getFirstArchiveUpdate(dbName);
        long endTime = getLastArchiveUpdate(dbName);
        RrdDb rrdDb = new RrdDb(dbName);
        def arclist = fetchArchives(rrdDb);
//        long endTime = rrdDb.getLastUpdateTime() * 1000;
        rrdDb.close();
        String function = arclist[0][FUNCTION];
//        long startTime = arclist[0][START_TIME] * 1000;
        return fetchData(dbName, datasource, function, startTime, endTime);
    }

    /**
    *  returns time series of one data index specified with its datasource name,
    * archive function of datasource, start time and end time
    */
    public static double[] fetchData(String dbName, String datasource, String function,
                                   long startTime, long endTime){
        boolean found = false;
        RrdDb rrdDb = new RrdDb(dbName);
        def dslist = fetchDatasources(rrdDb);
        for(int i=0; i<dslist.size(); i++){
            Map m = dslist[i];
            if(m.get(NAME).equals(datasource)){
                found = true;
            };
        }
        if(!found){
            rrdDb.close();
            throw new Exception("data source not found")
            return null;
        }

        long nstarttime = (long)(startTime / 1000)
        long nendtime = (long)(endTime / 1000)

        FetchRequest fetchRequest = rrdDb.createFetchRequest(function, nstarttime, nendtime);
        fetchRequest.setFilter(datasource);

        double[] data = fetchRequest.fetchData().getValues(datasource)
        rrdDb.close();
        return data
    }
    /**
    *  returns time series of data indexes specified with its datasource names
    */
    public static double[][] fetchData(String dbName, String[] datasources){
        long startTime = getFirstArchiveUpdate(dbName);
        long endTime = getLastArchiveUpdate(dbName);
        RrdDb rrdDb = new RrdDb(dbName);
        def arclist = fetchArchives(rrdDb);
        String function = arclist[0][DbUtils.FUNCTION];
//        long startTime = arclist[0][DbUtils.START_TIME] * 1000;
//        long endTime = rrdDb.getLastUpdateTime() * 1000;
        rrdDb.close();
        return fetchData(dbName, datasources, function, startTime, endTime);
    }

    /**
    *  returns time series of data indexes specified with its datasource names,
    * archive function of datasource, start time and end time
    */
    public static double[][] fetchData(String dbName, String[] datasources, String function,
                                   long startTime, long endTime){
        RrdDb rrdDb = new RrdDb(dbName);
        long nstarttime = (long)(startTime / 1000)
        long nendtime = (long)(endTime / 1000)
        FetchRequest fetchRequest = rrdDb.createFetchRequest(function, nstarttime, nendtime);
        fetchRequest.setFilter (datasources);

        double[][] data = fetchRequest.fetchData().getValues();
        rrdDb.close();

        return data
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
    /*
    * returns the endTime s of all archives
    */
    public static Long[] getLastArchiveUpdateTimes(String dbName){
        RrdDb rrdDb = new RrdDb(dbName);
        int archiveCount = rrdDb.getArcCount();

        def list = [];

        long max = 0
        int counter = 0;
        for(int i=0; i<archiveCount; i++)
        {
            try{
                Archive archive = rrdDb.getArchive(i)
                list.add(archive.getEndTime()*1000);
            }
            catch(ArrayIndexOutOfBoundsException e) { break;}
        }
        rrdDb.close();
        return list;
    }
    /*
    * returns the startTime s of all archives
    */
    public static Long[] getFirstArchiveUpdateTimes(String dbName){
        RrdDb rrdDb = new RrdDb(dbName);
        int archiveCount = rrdDb.getArcCount();

        def list = [];

        long min = Long.MAX_VALUE;
        int counter = 0;
        for(int i=0; i<archiveCount; i++)
        {
            try{
                Archive archive = rrdDb.getArchive(i)
                list.add(archive.getStartTime()*1000)
            }
            catch(ArrayIndexOutOfBoundsException e) { break;}
        }
        rrdDb.close();
        return list;
    }
    /**
    * this method retrieves the data of all archives with a map.
    * Map keys are timestamps (as String) values are data
    * since data are coming from all archives, increment of timestamps are not regular.
    */
    public static Map fetchDataAsMap(String dbName){
        long[] startTime = getFirstArchiveUpdateTimes(dbName);
        long[] endTime = getLastArchiveUpdateTimes(dbName);
        RrdDb rrdDb = new RrdDb(dbName);
        def arclist = fetchArchives(rrdDb);
        def dslist = fetchDatasources(rrdDb);
        String[] datasources = new String[dslist.size()];
        for(int i=0; i<datasources.length; i++){
            datasources[i] = dslist[i][NAME];
        }
//        long endTime = rrdDb.getLastUpdateTime() * 1000;
        rrdDb.close();
        String function = arclist[0][FUNCTION];
//        long startTime = arclist[0][START_TIME] * 1000;
        return fetchDataAsMap(dbName, datasources, function, startTime, endTime);
    }
    public static Map fetchDataAsMap(String dbName, String datasource){
        long[] startTime = getFirstArchiveUpdateTimes(dbName);
        long[] endTime = getLastArchiveUpdateTimes(dbName);
        RrdDb rrdDb = new RrdDb(dbName);
        def arclist = fetchArchives(rrdDb);

//        long endTime = rrdDb.getLastUpdateTime() * 1000;
        rrdDb.close();
        String function = arclist[0][FUNCTION];
//        long startTime = arclist[0][START_TIME] * 1000;
        return fetchDataAsMap(dbName, datasource, function, startTime, endTime);
    }
    /**
    * this method retrieves the data with a map. Map keys are timestamps (as String) values are data
    */
    public static Map fetchDataAsMap(String dbName, String[] datasources, String function,
                                   long[] startTime, long[] endTime){
        RrdDb rrdDb = new RrdDb(dbName);
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
                double[] dataValues = fd.getValues(0);
                Map datasourceMap = [:];
                for(int j=0; j<timeStamps.length; j++){
//                    datasourceMap[timeStamps[j]+""] =dataValues[j];
                    values[it][timeStamps[j]+""] =dataValues[j];
                }
//                values[it] = datasourceMap;
            }

        }
        rrdDb.close();
        return values;
    }
    public static Map fetchDataAsMap(String dbName, String datasource, String function,
                                   long[] startTime, long[] endTime){
        RrdDb rrdDb = new RrdDb(dbName);
        FetchRequest fetchRequest;
        FetchData fd;
        Map values = [:];
        for(int i=0; i<startTime.length; i++){
            long nstarttime = (long)(startTime[i] / 1000)
            long nendtime = (long)(endTime[i] / 1000)
            fetchRequest = rrdDb.createFetchRequest(function, nstarttime, nendtime);
            fetchRequest.setFilter (datasource);
            fd = fetchRequest.fetchData();
            long[] timeStamps = fd.getTimestamps();
            double[] dataValues = fd.getValues(0);
            for(int j=0; j<timeStamps.length; j++){
                values[timeStamps[j]+""] =dataValues[j];
            }
        }
        rrdDb.close();
        return values;
    }
    public static Map fetchDataAsMap(String dbName, String datasource, String function,
                                   long startTime, long endTime){
        RrdDb rrdDb = new RrdDb(dbName);
        FetchRequest fetchRequest;
        FetchData fd;
        Map values = [:];
        long nstarttime = (long)(startTime / 1000)
        long nendtime = (long)(endTime / 1000)
        fetchRequest = rrdDb.createFetchRequest(function, nstarttime, nendtime);
        fetchRequest.setFilter (datasource);
        fd = fetchRequest.fetchData();
        long[] timeStamps = fd.getTimestamps();
        double[] dataValues = fd.getValues(0);
        for(int j=0; j<timeStamps.length; j++){
            values[timeStamps[j]+""] =dataValues[j];
        }
        rrdDb.close();
        return values;
    }
    /*
    * creates an xml file compatible with flex chart application according to the given map
    * map's keys comprise of timestamps (as String) and values are data.
    */
    public static void createXml(Map data, String xmlFile){
//        for multiple datasources there is another method
          try{
              long trialForLong = Long.parseLong(data.keySet().toArray()[0]);
          }
          catch (Exception ex){
              createXmlMultipleDatasources(data,xmlFile);
              return;
          }
//        creates new xml file
          String newXmlFile = xmlFile+"new.xml";
          XmlWriter xwriter = new XmlWriter(new DataOutputStream(new FileOutputStream(newXmlFile) ) );
          xwriter.startTag("rrd");

          String[] keyArray = data.keySet().toArray();
          Arrays.sort(keyArray);
          
          for(int i=0; i<keyArray.length; i++){
              xwriter.startTag("data");
              long timestamp = Long.parseLong(keyArray[i]);
              java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
              long timeLong = timestamp*1000L;
//              println timeLong;
              String dateString = formatter.format(new Date(timeLong));

              xwriter.writeTag ("date",dateString);
              xwriter.writeTag ("value",data.get(keyArray[i]));
              xwriter.writeTag ("volume",data.get(keyArray[i]));
              xwriter.closeTag();
          }
          xwriter.closeTag();
          writeNewFileToOldFile (xmlFile,newXmlFile);
    }
    public static void createXmlMultipleDatasources(Map data, String xmlFile){
         String newXmlFile = xmlFile+"new.xml";
          XmlWriter xwriter = new XmlWriter(new DataOutputStream(new FileOutputStream(newXmlFile) ) );
          xwriter.startTag("rrd");

          String[] datasources = data.keySet().toArray();

          String[] keyArray = data[datasources[0]].keySet().toArray();
          Arrays.sort(keyArray);

          for(int i=0; i<keyArray.length; i++){
              xwriter.startTag("data");
              long timestamp = Long.parseLong(keyArray[i]);
              java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
              long timeLong = timestamp*1000L;
//              println timeLong;
              String dateString = formatter.format(new Date(timeLong));

              xwriter.writeTag ("date",dateString);
              datasources.each {
                    xwriter.writeTag ("value",data.get(it).get(keyArray[i]));
              }
              xwriter.writeTag ("volume",data.get(datasources[0]).get(keyArray[i]));
              xwriter.closeTag();
          }

          xwriter.closeTag();

          writeNewFileToOldFile (xmlFile,newXmlFile);
    }
    private static void convertXmlFile(String xmlFile){
//        retrieve xml file to be converted
          DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          DocumentBuilder builder = factory.newDocumentBuilder();
          org.w3c.dom.Document document = builder.parse(xmlFile);
          org.w3c.dom.Node node = document.getDocumentElement();
          node = node.getElementsByTagName ("data").item (0);
          org.w3c.dom.NodeList dataList = node.getElementsByTagName ("row");

//        creates new xml file
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
//              println timeLong;
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
}