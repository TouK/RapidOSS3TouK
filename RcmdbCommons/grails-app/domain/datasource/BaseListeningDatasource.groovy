package datasource

import script.CmdbScript
import com.ifountain.rcmdb.datasource.ListeningAdapterManager

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jul 18, 2008
 * Time: 10:45:10 AM
 */
class BaseListeningDatasource extends BaseDatasource
{

    static searchable = {
        except = ["listeningScript"];
    };
    static datasources = [:]
    CmdbScript listeningScript;
    boolean isSubscribed = false;

    static relations = [
            listeningScript:[type:CmdbScript, reverseName:"listeningDatasource", isMany:false]
    ]
    static constraints={
        listeningScript(nullable:true)
    }
    static transients = [];

    def beforeDelete = {
        if(this.listeningScript && this.listeningScript.type == CmdbScript.LISTENING){
            ListeningAdapterManager.getInstance().stopAdapter(this);
        }
    }


    def getListeningAdapter(Map params){
        return null;
    }

}