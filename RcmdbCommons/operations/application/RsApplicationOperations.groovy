package application

import com.ifountain.rcmdb.domain.statistics.OperationStatistics

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 13, 2008
 * Time: 4:10:27 PM
 * To change this template use File | Settings | File Templates.
 */
class RsApplicationOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
    public static String getCompassStatistics()
    {
        return OperationStatistics.getInstance().getGlobalStatistics();
    }

    public static void resetCompassStatistics()
    {
        OperationStatistics.getInstance().reset();
    }
}