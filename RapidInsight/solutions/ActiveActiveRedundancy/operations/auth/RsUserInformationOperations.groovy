package auth
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 24, 2009
 * Time: 2:52:22 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUserInformationOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
	//	changed for Active-Active Redundancy
	def afterInsert()
    {
		application.RapidApplication.getUtility("RedundancyUtility").objectInAfterInsert(this.domainObject);
    }   
	def afterUpdate(params)
    {
		application.RapidApplication.getUtility("RedundancyUtility").objectInAfterUpdate(this.domainObject,params);
    }
	def afterDelete()
    {
		application.RapidApplication.getUtility("RedundancyUtility").objectInAfterDelete(this.domainObject);
    }
	//change ended	
}