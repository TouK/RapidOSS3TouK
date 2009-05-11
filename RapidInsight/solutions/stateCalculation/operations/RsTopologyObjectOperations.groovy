import application.RsApplication

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
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111NOTSET307
* USA.
*/
public class RsTopologyObjectOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {

    int getState()
    {
        return RsApplication.getUtility("StateCalculator").getObjectState(this.domainObject);
    }
    def currentState()
    {
        return RsApplication.getUtility("StateCalculator").loadObjectState(this.domainObject);
    }
    
    def beforeInsert(){
        RsApplication.getUtility("ObjectProcessor").objectInBeforeInsert(this.domainObject);
	}
	def beforeUpdate(params)
    {
        RsApplication.getUtility("ObjectProcessor").objectInBeforeUpdate(this.domainObject,params);
    }
	def afterInsert(){
        RsApplication.getUtility("ObjectProcessor").objectIsAdded(this.domainObject);
    }
    def afterUpdate(params){
        RsApplication.getUtility("ObjectProcessor").objectIsUpdated(this.domainObject,params);
    }
    def afterDelete()
    {
        RsApplication.getUtility("ObjectProcessor").objectIsDeleted(this.domainObject);
    }

}

