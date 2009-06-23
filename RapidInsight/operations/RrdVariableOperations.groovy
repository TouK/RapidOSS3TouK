
import com.ifountain.rcmdb.rrd.RrdUtils;

public class RrdVariableOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{

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
    