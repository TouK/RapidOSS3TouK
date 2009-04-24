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
package datasource

import com.ifountain.rcmdb.datasource.ListeningAdapterManager
import script.CmdbScript
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 2:53:23 PM
 * To change this template use File | Settings | File Templates.
 */
class BaseListeningDatasourceOperations extends BaseDatasourceOperations
{

    static Logger logger = Logger.getLogger(BaseListeningDatasourceOperations.class)
    static String logPrefix="[BaseListeningDatasource]: ";

    def afterDelete(){
        try
        {
            ListeningAdapterManager.getInstance().removeAdapter(this.domainObject);
        }catch(Exception e)
        {
            logger.info ("Exception occurred while removing adapter for ${this.domainObject.name} datasource", e);
        }
    }
    def afterInsert(){
        try
        {
            ListeningAdapterManager.getInstance().addAdapterIfNotExists (this.domainObject);
        }catch(Exception e)
        {
            logger.info ("Exception occurred while adding adapter for ${this.domainObject.name} datasource", e);
        }
    }

    def startListening() throws Exception{
         ListeningAdapterManager.getInstance().startAdapter(this.domainObject);
         this.update(isSubscribed:true);

    }
    def stopListening() throws Exception{
        ListeningAdapterManager.getInstance().stopAdapter(this.domainObject);
        this.update(isSubscribed:false);     
    }

    def isStartable()
    {
        return ListeningAdapterManager.getInstance().isStartable(this.domainObject);
    }

    def isSubscribed()
    {
        return ListeningAdapterManager.getInstance().isSubscribed(this.domainObject);
    }

    def getListeningAdapter(Map params,Logger adapterLogger){
        return null;
    }
}