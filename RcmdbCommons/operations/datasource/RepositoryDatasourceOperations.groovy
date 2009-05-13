package datasource

import org.apache.log4j.Logger
import com.ifountain.rcmdb.domain.datasource.RepositoryListenerAdapter

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 13, 2009
* Time: 3:54:03 PM
*/
class RepositoryDatasourceOperations extends BaseListeningDatasourceOperations {

    def getListeningAdapter(Map params, Logger adapterLogger) {
        def subscribeClasses = params["Classes"]
        if (subscribeClasses == null) {
            subscribeClasses = [:]
        }
        return new RepositoryListenerAdapter(getProperty("connection").name, adapterLogger, subscribeClasses, params.FilterClosure);
    }
}