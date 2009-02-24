package search
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 24, 2009
 * Time: 2:52:22 PM
 * To change this template use File | Settings | File Templates.
 */
class SearchQueryGroupOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
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