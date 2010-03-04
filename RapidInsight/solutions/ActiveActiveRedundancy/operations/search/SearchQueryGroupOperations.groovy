package search
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 24, 2009
 * Time: 2:52:22 PM
 * To change this template use File | Settings | File Templates.
 */
class SearchQueryGroupOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
	//	changed for isLocal property
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
	def beforeDelete(){
        def tempQueries=getProperty("queries");        
        if(tempQueries!=null)
        {
            if(tempQueries.size()>0)
            {
                throw new Exception("Can not delete Query Group ${name}. Group contains queries. Please first move or remove sub queries");
            }
        }
    }
}