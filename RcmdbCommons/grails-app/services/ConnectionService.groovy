import com.ifountain.rcmdb.connection.RcmdbConnectionManagerAdapter
import connection.Connection
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import com.ifountain.rcmdb.connection.RcmdbConnectionManagerAdapter

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
class ConnectionService implements InitializingBean, DisposableBean{
    boolean transactional = false
    def grailsApplication

    public void afterPropertiesSet()
    {
        def poolCheckIntervalStr = ((GrailsApplication)grailsApplication).config.flatten()["connection.pool.checker.interval"];
        String timeoutStrategyClassName = ((GrailsApplication)grailsApplication).config.flatten()["connection.pool.timeout.strategy"];
        long poolCheckInterval = 10000;
        if(poolCheckIntervalStr == null)
        {
            log.info("No connection.pool.checker.interval property is defined. it will be assigned to default value ${poolCheckInterval}.");
        }
        else
        {
            try
            {
                poolCheckInterval = Long.parseLong(String.valueOf(poolCheckIntervalStr));
            }
            catch(Throwable t)
            {
                log.info("Invalid connection.pool.checker.interval property ${poolCheckIntervalStr}. it will be assigned to default value ${poolCheckInterval}.");
            }
        }
        Class timeoutStrategy = null;
        if(timeoutStrategyClassName != null)
        {
            try
            {
                timeoutStrategy = ((GrailsApplication)grailsApplication).classLoader.loadClass(timeoutStrategyClassName)
            }
            catch(Throwable t)
            {
                log.info("Exception occurred while loading timeout strategy class");
            }
        }
        RcmdbConnectionManagerAdapter.getInstance().initialize (Logger.getRootLogger(), this.getClass().getClassLoader(), poolCheckInterval, timeoutStrategy);
    }

    public void destroy()
    {
        RcmdbConnectionManagerAdapter.destroyInstance();
    }


}
