package com.ifountain.rcmdb.rrd

import org.jrobin.core.RrdDef
import org.jrobin.core.RrdDb
import org.jrobin.core.DsDef
import org.jrobin.core.ArcDef
import org.jrobin.core.Sample

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
           ArcDef arcTemp = new ArcDef(it.get(FUNCTION),it.get(XFF),it.get(STEPS), it.get(ROWS))
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
    
    /*
    public static Calendar getFormattedStartTime(String str){
        def date = str.trim().split(",");
        Calendar cal = Calendar.getInstance();
        cal.clear()
        int year,month,day,hour=0,min=0,sec=0;
        if(date.length==1){
           def ymd = date[0].split("-");
           if(ymd.length!=3){
               throw new Exception("date is not valid")
           }
           year = Integer.parseInt(ymd[0]);
           month = Integer.parseInt(ymd[1]);
           day = Integer.parseInt(ymd[2])
        }
        if(date.length==2){
           def hms = date[1].split(":");
           if(hms.length!=3){
               throw new Exception("time is not valid")
           }
           hour = Integer.parseInt(hms[0].trim());
           min = Integer.parseInt(hms[1]);
           sec = Integer.parseInt(hms[2])
        }
        println hour +" "+min+" "+sec
        cal.set(year, month ,day,hour,min,sec);

        return cal;
    }
    */
}