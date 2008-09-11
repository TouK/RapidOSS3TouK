package datasource

import com.ifountain.rcmdb.datasource.ListeningAdapterManager
import script.CmdbScript

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 2:53:23 PM
 * To change this template use File | Settings | File Templates.
 */
class BaseListeningDatasourceOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    def beforeDelete(){
        if(this.listeningScript && this.listeningScript.type == CmdbScript.LISTENING){
            ListeningAdapterManager.getInstance().stopAdapter(this);
        }
    }


    def getListeningAdapter(Map params){
        return null;
    }
}