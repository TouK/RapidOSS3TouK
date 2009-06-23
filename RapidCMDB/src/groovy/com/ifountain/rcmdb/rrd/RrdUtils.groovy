package com.ifountain.rcmdb.rrd

import org.jrobin.core.RrdDef
import org.jrobin.core.RrdDb
import org.jrobin.core.DsDef
import org.jrobin.core.ArcDef
import org.jrobin.core.Sample
import org.jrobin.core.Archive
import org.jrobin.core.Datasource
import org.jrobin.core.FetchRequest

/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Jun 17, 2009
* Time: 9:37:31 AM
*/
class RrdUtils {
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
                rrdDef.setStartTime(config.get(START_TIME))
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

    public static void removeDatabase(String fileName) {
        File file = new File(fileName)
        if(file.exists())
            file.delete()
        else
            throw new Exception("File does not exists : " + fileName)
    }

    public static boolean isDatabaseExists(String fileName) {
        return new File(fileName).exists()
    }

    public static DsDef[] getDsDefs(list){
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

    public static ArcDef[] getArcDefs(list){
        def arcList = [];
        list.each{
           ArcDef arcTemp = new ArcDef(it.get(FUNCTION),it.get(XFF),(int)it.get(STEPS),(int)it.get(ROWS))
           arcList.add(arcTemp);
       }
       return arcList as ArcDef[]
    }

    public static void updateData(String dbname, String data){
        RrdDb rrdDb = new RrdDb(dbname);

        Sample sample = rrdDb.createSample();
        sample.setAndUpdate(data);
        rrdDb.close();
    }

    public static void updateData(String dbname, String[] data){
        RrdDb rrdDb = new RrdDb(dbname);

        Sample sample = rrdDb.createSample();
        for(int i=0; i<data.length; i++){
            sample.setAndUpdate(data[i]);
        }
        rrdDb.close();
    }

    public static byte[] graph(Map config){
        return Grapher.graph(config);
    }
    /**
    * retrieves defined archived in speficied database
    * Notice: it is better to use overriden function fetchArchives(rrdDb) if an rrdDb is
    * already defined and the database is open. Otherwise this function will give
    * an exception since it cannot read the database
    */
    private static def fetchArchives(String dbName){
        RrdDb rrdDb = new RrdDb(dbName);
        def result = fetchArchives(rrdDb);
        rrdDb.close();
        return result;
    }

    private static def fetchArchives(RrdDb rrdDb){
        int arcCount = rrdDb.getArcCount();
        Archive[] arcs = new Archive[arcCount];
        for(int i=0; i<arcCount; i++){
            arcs[i] = rrdDb.getArchive(i);
        }
        def alist = [];
        for(int i=0; i<arcs.length; i++){
            Map m = [:];

            m[RrdUtils.FUNCTION] = arcs[i].getConsolFun();
            m[RrdUtils.STEPS] = arcs[i].getSteps();
            m[RrdUtils.ROWS] = arcs[i].getRows();
            m[RrdUtils.XFF] = arcs[i].getXff();
            m[RrdUtils.START_TIME] = arcs[i].getStartTime();

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
    private static def fetchDatasources(String dbName){
        RrdDb rrdDb = new RrdDb(dbName);
        def result = fetchDatasources(rrdDb);
        rrdDb.close();
        return result;
    }

    private static def fetchDatasources(RrdDb rrdDb){
        int datCount = rrdDb.getDsCount();
        Datasource[] dats = new Datasource[datCount];
        for(int i=0; i<datCount; i++){
            dats[i] = rrdDb.getDatasource(i);
        }

        def dslist = [];
        for(int i=0; i<dats.length; i++){
            Map m = [:];
            m[RrdUtils.NAME] = dats[i].getDsName();
            m[RrdUtils.TYPE] = dats[i].getDsType();
            m[RrdUtils.HEARTBEAT] = dats[i].getHeartbeat();
            m[RrdUtils.MAX] = dats[i].getMaxValue();
            m[RrdUtils.MIN] = dats[i].getMinValue();

            dslist.add(m);
        }
        return dslist;
    }

    public static Map getDatabaseInfo(String dbName){
        RrdDb rrdDb = new RrdDb(dbName);
        RrdDef rrdDef = rrdDb.getRrdDef();

        Map config = [:];
        config[DATABASE_NAME] = dbName;
        config[START_TIME] = rrdDef.getStartTime();
        config[STEP] = rrdDef.getStep();
        config[DATASOURCE] = fetchDatasources(rrdDb );
        config[ARCHIVE] = fetchArchives(rrdDb);
   
        rrdDb.close();
        return config;
    }
    /**
    *  returns first time series of first data point
    *  it is the easiest call if the database has only one data source
    */
    public static double[] fetchData(String dbName){
        return   fetchAllData(dbName)[0];
    }

    public static double[][] fetchAllData(String dbName){
        RrdDb rrdDb = new RrdDb(dbName);
        def arclist = fetchArchives(rrdDb);
        def dslist = fetchDatasources(rrdDb);
        String[] datasources = new String[dslist.size()];
        for(int i=0; i<datasources.length; i++){
            datasources[i] = dslist[i][NAME];
        }
        long endTime = rrdDb.getLastUpdateTime();
        rrdDb.close();
        String function = arclist[0][FUNCTION];
        long startTime = arclist[0][START_TIME];
        return fetchData(dbName, datasources, function, startTime, endTime);
    }

    public static double[] fetchData(String dbName, String datasource){
        RrdDb rrdDb = new RrdDb(dbName);
        def arclist = fetchArchives(rrdDb);
        long endTime = rrdDb.getLastUpdateTime();
        rrdDb.close();
        String function = arclist[0][FUNCTION];
        long startTime = arclist[0][START_TIME];
        return fetchData(dbName, datasource, function, startTime, endTime);
    }

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
        FetchRequest fetchRequest = new RrdDb(dbName).createFetchRequest(function, startTime, endTime);
        fetchRequest.setFilter (datasource);
        rrdDb.close();
        return fetchRequest.fetchData().getValues()[0];
    }

    public static double[][] fetchData(String dbName, String[] datasources){
        RrdDb rrdDb = new RrdDb(dbName);
        def arclist = fetchArchives(rrdDb);
        String function = arclist[0][RrdUtils.FUNCTION];
        long startTime = arclist[0][RrdUtils.START_TIME];
        long endTime = rrdDb.getLastUpdateTime();
        rrdDb.close();
        return fetchData(dbName, datasources, function, startTime, endTime);
    }

    public static double[][] fetchData(String dbName, String[] datasources, String function,
                                   long startTime, long endTime){
        RrdDb rrdDb = new RrdDb(dbName);
        FetchRequest fetchRequest = new RrdDb(dbName).createFetchRequest(function, startTime, endTime);
        fetchRequest.setFilter (datasources);
        rrdDb.close();
        return fetchRequest.fetchData().getValues();
    }

}