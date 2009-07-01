
import com.ifountain.rcmdb.rrd.RrdUtils;

public class RrdVariableOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    static final long ONE_HOUR = 3600000L
    static final long ONE_DAY = 24 * ONE_HOUR

    public static final String DATABASE_NAME = "databaseName";
    public static final String DATASOURCE = "datasource";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String FUNCTION = "function";
    public static final String VERTICAL_LABEL = "vlabel";
    public static final String HORIZONTAL_LABEL = "hlabel";
    public static final String LINE = "line";
    public static final String AREA = "area";
    public static final String STACK = "stack";
    public static final String HRULE = "hrule";
    public static final String VRULE = "vrule";
    public static final String RPN = "rpn";
    public static final String DSNAME = "dsname";
    public static final String NAME = "name";
    public static final String COLOR = "color";
    public static final String DESCRIPTION = "description";
    public static final String THICKNESS = "thickness";
    public static final String TITLE = "title";
    public static final String MAX = "max";
    public static final String MIN = "min";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final String TYPE = "type";
    public static final String ARCHIVE = "archive";
    public static final String STEP = "step";
    public static final String HEARTBEAT= "heartbeat";
    public static final String XFF = "xff"
    public static final String STEPS = "steps"
    public static final String ROWS = "rows"

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

    def graph(Map config) {
        config[RrdUtils.RRD_VARIABLE] = name
        if(!config.containsKey(END_TIME))
            config[END_TIME] = getCurrentTime()
        return RrdUtils.graph(config)
    }

    def graphLastHour(Map config) {
        config[RrdUtils.RRD_VARIABLE] = name
        config[START_TIME] = getCurrentTime() - ONE_HOUR
        config[END_TIME] =  getCurrentTime()
        return RrdUtils.graph(config)
    }

    def graphLastDay(Map config) {
        config[RrdUtils.RRD_VARIABLE] = name
        config[START_TIME] = getCurrentTime() - ONE_DAY
        config[END_TIME] =  getCurrentTime()
        return RrdUtils.graph(config)
    }

    private def getCurrentTime() {
        Calendar calendar = Calendar.getInstance()
        calendar.getTimeInMillis()
    }

    private def createDBConfig() {
        def dbConfig = [:]

        //there could be change in file name
        if(file == "" || file == null)
            dbConfig[DATABASE_NAME] = name + ".rrd"
        else
            dbConfig[DATABASE_NAME] = file
        
        dbConfig[START_TIME] = startTime
        dbConfig[STEP] = step

        def datapoint = [:]

        datapoint[NAME] = name
        datapoint[TYPE] = type
        datapoint[HEARTBEAT] = heartbeat
        datapoint[MAX] = max
        datapoint[MIN] = min

        dbConfig[DATASOURCE] = [datapoint]

        def archiveList = []

        archives.each
        {
            def archive = [:]
            archive[FUNCTION] = it.function
            archive[XFF] = it.xff
            archive[STEPS] = it.step
            archive[ROWS] = it.row

            archiveList.add(archive)
        }

        dbConfig[ARCHIVE] = archiveList

        return dbConfig
    }

    private def createUpdateData(Map config) {
        def timestamp = config["time"] != null ? config["time"] : getCurrentTime()
        def value = config["value"] != null ? config["value"] : Double.NaN

        return "" + timestamp + ":" + value
    }

}
    