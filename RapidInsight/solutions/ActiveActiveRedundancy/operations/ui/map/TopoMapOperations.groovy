package ui.map


class TopoMapOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
	//changed for isLocal property
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