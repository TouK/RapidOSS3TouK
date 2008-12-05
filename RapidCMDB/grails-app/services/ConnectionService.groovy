/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
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
import org.codehaus.groovy.grails.commons.GrailsApplication

class ConnectionService implements InitializingBean, DisposableBean, ConnectionParameterSupplier{
    boolean transactional = false
    def grailsApplication
    public ConnectionParam getConnectionParam(String connConfigName) {
        Connection connection = Connection.findByName(connConfigName);
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
            return new ConnectionParam(connection.getClass().getName(), connection.name, connection.connectionClass, optProps, connection.maxNumberOfConnections.intValue(), connection.minTimeout.intValue()*1000, connection.maxTimeout.intValue()*1000)
        }
        return null;

    }

    public void removeConnection(String connectionName) throws Exception{
        ConnectionManager.removeConnection(connectionName);
    }
    public void afterPropertiesSet()
    {
        String poolCheckIntervalStr = ((GrailsApplication)grailsApplication).config.flatten()["connection.pool.checker.interval"];
        String timeoutStrategyClassName = ((GrailsApplication)grailsApplication).config.flatten()["connection.pool.timeout.strategy"];
        if(poolCheckIntervalStr == null) poolCheckIntervalStr = "10000"
        long poolCheckInterval = Long.parseLong(String.valueOf(poolCheckIntervalStr));
        ConnectionManager.initialize (Logger.getRootLogger(), this, this.getClass().getClassLoader(), poolCheckInterval);
        if(timeoutStrategyClassName != null)
        {
            try
            {
                Class timeoutStrategy = ((GrailsApplication)grailsApplication).classLoader.loadClass(timeoutStrategyClassName)
                ConnectionManager.setTimeoutStrategyClass(timeoutStrategy);
            }
            catch(Throwable t)
            {
                log.info("Exception occurred while loading timeout strategy class");
            }
        }
    }

    public void destroy()
    {
        ConnectionManager.destroy();
    }

    
}
