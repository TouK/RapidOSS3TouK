
import com.ifountain.rcmdb.rrd.RrdUtils;
import com.ifountain.rcmdb.rrd.Grapher;

public class RrdVariableOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    static String TYPE = "type";
    static String RRD_VARIABLES = "rrdVariables";
    String typeVar = "line";
    String colorVar = "000000";

    def createDB() {
        RrdUtils.createDatabase (createDBConfig())
    }

    def removeDB() {
        RrdUtils.removeDatabase(file)
    }

    def updateDB(Map config) {
        String data = createUpdateData(config)
        RrdUtils.updateData(file, data)
    }

    def updateDB(List config) {
        String[] dataList = new String[config.size()]
        for(int i = 0; i < dataList.length; i++)
        {
            dataList[i] = createUpdateData(config.get(i))
        }
        RrdUtils.updateData(file, dataList)
    }

    def graph(Map config){
       Map fConfig = [:];  //converts the given config map to a formatted config map

       if(!config.containsKey(Grapher.START_TIME) ){
           throw new Exception("Start time is not specified");
       }
       if(!config.containsKey(Grapher.END_TIME) ){
           fConfig[Grapher.END_TIME] = getCurrentTime();
       }
       else{
           fConfig[Grapher.END_TIME] = config.get(Grapher.END_TIME);
       }
       if(config.containsKey(TYPE) ){
          typeVar = config.get(TYPE);
       }
       if(config.containsKey(Grapher.COLOR) ){
          colorVar = config.get(Grapher.COLOR);
       }
       if(config.containsKey(Grapher.MAX) ){
          fconfig[Grapher.MAX] = config.get(Grapher.MAX);
       }
       if(config.containsKey(Grapher.MIN) ){
          fconfig[Grapher.MIN] = config.get(Grapher.MIN);
       }
       if(config.containsKey(Grapher.HEIGHT) ){
          fconfig[Grapher.HEIGHT] = config.get(Grapher.HEIGHT);
       }
       if(config.containsKey(Grapher.WIDTH) ){
          fconfig[Grapher.WIDTH] = config.get(Grapher.WIDTH);
       }

       fConfig[Grapher.START_TIME] = config.get(Grapher.START_TIME);

       def typeMap = [:];
       typeMap[Grapher.NAME] = name;
       typeMap[Grapher.DESCRIPTION] = name;
       typeMap[Grapher.COLOR] = color;

       fConfig[typeVar] = [];
       fConfig[typeVar].add[typeMap];           

       def datasourceList = [];
       archives.each{
           def datasourceMap = [:];
           datasourceMap[Grapher.NAME] = name;
           datasourceMap[Grapher.DATABASE_NAME] = file;
           datasourceMap[Grapher.DSNAME] = name;
           datasourceMap[Grapher.FUNCTION] = it.function;
           datasourceList.add(dataSourceMap);
       }

       fConfig[Grapher.DATASOURCE] = datasourceList;

       return RrdUtils.graph(fconfig);
    }

    private long getCurrentTime(){
        Calendar cal = Calendar.getInstance();
        return cal.getTimeInMillis();
    }

    def graphLastHour(Map config) {
        
    }

    def graphLastDay(Map config) {
        
    }

    private def createDBConfig() {
        def dbConfig = [:]

        //there could be change in file name
        dbConfig[RrdUtils.DATABASE_NAME] = file
        dbConfig[RrdUtils.START_TIME] = startTime
        dbConfig[RrdUtils.STEP] = step

        def datapoint = [:]

        datapoint[RrdUtils.NAME] = name
        datapoint[RrdUtils.TYPE] = type
        datapoint[RrdUtils.HEARTBEAT] = heartbeat
        datapoint[RrdUtils.MAX] = max
        datapoint[RrdUtils.MIN] = min

        dbConfig[RrdUtils.DATASOURCE] = [datapoint]

        def archiveList = []

        archives.each
        {
            def archive = [:]
            archive[RrdUtils.FUNCTION] = it.function
            archive[RrdUtils.XFF] = it.xff
            archive[RrdUtils.STEPS] = it.step
            archive[RrdUtils.ROWS] = it.row

            archiveList.add(archive)
        }

        dbConfig[RrdUtils.ARCHIVE] = archiveList

        return dbConfig
    }

    private def createUpdateData(Map config) {
        def timestamp = config["time"] != null ? config["time"] : new Date().getTime()
        def value = config["value"] != null ? config["value"] : Double.NaN

        return "" + timestamp + ":" + value
    }

    

    
    

}
    