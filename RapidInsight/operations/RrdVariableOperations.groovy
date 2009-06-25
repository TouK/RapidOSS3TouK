
import com.ifountain.rcmdb.rrd.RrdUtils;
import com.ifountain.rcmdb.rrd.Grapher;

public class RrdVariableOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    static final long ONE_HOUR = 3600000L
    static final long ONE_DAY = 24 * ONE_HOUR

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
        config[Grapher.RRD_VARIABLE] = name
        if(!config.containsKey(Grapher.END_TIME))
            config[Grapher.END_TIME] = getCurrentTime()
        return RrdUtils.graph(config)
    }

    def graphLastHour(Map config) {
        config[Grapher.RRD_VARIABLE] = name
        config[Grapher.START_TIME] = getCurrentTime() - ONE_HOUR
        config[Grapher.END_TIME] =  getCurrentTime()
        return RrdUtils.graph(config)
    }

    def graphLastDay(Map config) {
        config[Grapher.RRD_VARIABLE] = name
        config[Grapher.START_TIME] = getCurrentTime() - ONE_DAY
        config[Grapher.END_TIME] =  getCurrentTime()
        return RrdUtils.graph(config)
    }

    private def getCurrentTime() {
        Calendar calendar = Calendar.getInstance()
        calendar.getTimeInMillis()
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
        def timestamp = config["time"] != null ? config["time"] : getCurrentTime()
        def value = config["value"] != null ? config["value"] : Double.NaN

        return "" + timestamp + ":" + value
    }

}
    