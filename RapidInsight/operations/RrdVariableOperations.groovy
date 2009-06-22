
import com.ifountain.rcmdb.rrd.RrdUtils;

public class RrdVariableOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    def createDB()
    {
        RrdUtils.createDatabase (createDBConfig);
    }

    private def Map createDBConfig()
    {
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



    
    

}
    