import org.codehaus.groovy.grails.plugins.web.ControllersGrailsPlugin
import org.codehaus.groovy.grails.plugins.LoggingGrailsPlugin
import com.ifountain.rcmdb.execution.ExecutionContextManager
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils
import com.ifountain.rcmdb.util.RapidCmdbLogFactory

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 7, 2009
* Time: 5:58:15 PM
* To change this template use File | Settings | File Templates.
*/
class ExecutionContextFilters {
    def filters = {
        allURIs(uri:'/**') {
			before = {
                ExecutionContextManager.getInstance().clearExecutionContexts();
                ExecutionContextManager.getInstance().startExecutionContext ([:])
                ExecutionContextManagerUtils.addUsernameToCurrentContext (session.username)
                if(controllerName != null && controllerName != "")
                {
                    ExecutionContextManagerUtils.addLoggerToCurrentContext (RapidCmdbLogFactory.getControllerLogger(controllerName));
                }
                ExecutionContextManagerUtils.addWebResponseToCurrentContext (response);
            }
            afterView = {
                //TODO:In afterview context should never be empty. The if statement should be unnecessary
                if(ExecutionContextManager.getInstance().hasExecutionContext())
                {
                    ExecutionContextManager.getInstance().endExecutionContext();
                }
            }
		}
    }   
}