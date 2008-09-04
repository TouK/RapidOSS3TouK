import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events

import com.ifountain.comp.utils.CaseInsensitiveMap
import com.ifountain.core.connection.ConnectionManager
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.ConnectionParameterSupplier
import connection.Connection
import org.apache.log4j.Logger
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import com.ifountain.rcmdb.domain.util.DomainClassUtils

class ConnectionService implements InitializingBean, DisposableBean, ConnectionParameterSupplier{
    boolean transactional = false
    def grailsApplication
    public ConnectionParam getConnectionParam(String connConfigName) {
        def connection = Connection.findByName(connConfigName);
        if(connection){
            def excludedProps = ['version',
                                'id',
                                'maxNumberOfConnections',
                                "errors", "__operation_class__",
                                "__is_federated_properties_loaded__",
                                Events.ONLOAD_EVENT,
                                Events.BEFORE_DELETE_EVENT,
                                Events.BEFORE_INSERT_EVENT,
                                Events.BEFORE_UPDATE_EVENT]
            def domainClass = grailsApplication.getDomainClass(connection.getClass().getName());
            def rels = DomainClassUtils.getRelations(domainClass);
            def props = domainClass.properties.findAll { !excludedProps.contains(it.name) }
            def optProps = new CaseInsensitiveMap();
            props.each{
	            if(!rels.containsKey(it.name)){
		        	def propName = it.name
	        		optProps.put(propName, connection."$propName")
		        }
	       	}
            if(optProps.userPassword)
            {
                optProps.put ("password",optProps.userPassword);
            }
            else
            {
                optProps.put ("password","");                
            }
            return new ConnectionParam(connection.getClass().getName(), connection.name,
                    connection.connectionClass,optProps, connection.maxNumberOfConnections)
        }
        return null;

    }

    public void removeConnection(String connectionName) throws Exception{
        ConnectionManager.removeConnection(connectionName);
    }
    public void afterPropertiesSet()
    {
        ConnectionManager.initialize (Logger.getRootLogger(), this, this.getClass().getClassLoader());
    }

    public void destroy()
    {
        ConnectionManager.destroy();
    }

    
}
