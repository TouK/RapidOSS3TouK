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
        config[START_TIME] = rrdDef.getStartTime() * 1000;
        config[Grapher.END_TIME] = rrdDb.getLastArchiveUpdateTime()*1000;
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

    /**
    * returns the all datasources in the database according to the first archive method.
    */
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

    /**
    * returns time series of one data index specified with its datasource name
    */
    public static double[] fetchData(String dbName, String datasource){
        RrdDb rrdDb = new RrdDb(dbName);
        def arclist = fetchArchives(rrdDb);
        long endTime = rrdDb.getLastUpdateTime();
        rrdDb.close();
        String function = arclist[0][FUNCTION];
        long startTime = arclist[0][START_TIME];
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

        double[] data = fetchRequest.fetchData().getValues(datasource)
        rrdDb.close();
        return data
    }
    /**
    *  returns time series of data indexes specified with its datasource names
    */
    public static double[][] fetchData(String dbName, String[] datasources){
        RrdDb rrdDb = new RrdDb(dbName);
        def arclist = fetchArchives(rrdDb);
        String function = arclist[0][DbUtils.FUNCTION];
        long startTime = arclist[0][DbUtils.START_TIME];
        long endTime = rrdDb.getLastUpdateTime();
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

}