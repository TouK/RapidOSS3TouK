package search
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 24, 2009
 * Time: 2:52:22 PM
 * To change this template use File | Settings | File Templates.
 */
class GridColumnOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
	//	changed for isLocal property
	def beforeInsert()
    {
		application.RsApplication.getUtility("RedundancyUtility").objectInBeforeInsert(this.domainObject);
    }   
	def beforeUpdate(params)
    {
		application.RsApplication.getUtility("RedundancyUtility").objectInBeforeUpdate(this.domainObject);
    }
	def afterDelete()
    {
		application.RsApplication.getUtility("RedundancyUtility").objectInAfterDelete(this.domainObject);
    }
	//change ended	
}