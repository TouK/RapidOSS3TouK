package ui.map


class TopoMapOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
	//changed for isLocal property
	def afterInsert()
    {
		application.RapidApplication.getUtility("RedundancyUtility").objectInAfterInsert(this.domainObject);
    }   
	def afterUpdate(params)
    {
		application.RapidApplication.getUtility("RedundancyUtility").objectInAfterUpdate(this.domainObject);
    }
	def afterDelete()
    {
		application.RapidApplication.getUtility("RedundancyUtility").objectInAfterDelete(this.domainObject);
    }
	//change ended
	
}