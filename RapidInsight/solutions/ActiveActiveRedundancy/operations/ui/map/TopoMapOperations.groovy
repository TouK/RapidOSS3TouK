package ui.map


class TopoMapOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
	//changed for isLocal property
	def beforeInsert()
    {
		application.RapidApplication.getUtility("RedundancyUtility").objectInBeforeInsert(this.domainObject);
    }   
	def beforeUpdate(params)
    {
		application.RapidApplication.getUtility("RedundancyUtility").objectInBeforeUpdate(this.domainObject);
    }
	def afterDelete()
    {
		application.RapidApplication.getUtility("RedundancyUtility").objectInAfterDelete(this.domainObject);
    }
	//change ended
	
}