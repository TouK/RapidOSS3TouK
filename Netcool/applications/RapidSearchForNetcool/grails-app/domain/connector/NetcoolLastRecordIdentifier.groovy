package connector

import datasource.NetcoolDatasource

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 3, 2008
 * Time: 6:18:10 PM
 * To change this template use File | Settings | File Templates.
 */
class NetcoolLastRecordIdentifier
{
    static searchable = true;
    Long eventLastRecordIdentifier;
    Long journalLastRecordIdentifier;
    String datasourceName;
    static constraints = {
        datasourceName(nullable:false, blank:false, key:[]);
        eventLastRecordIdentifier(nullable:true);
        journalLastRecordIdentifier(nullable:true);
    }
}