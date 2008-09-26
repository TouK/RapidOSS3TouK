package datasource
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 4, 2008
 * Time: 8:53:58 AM
 * To change this template use File | Settings | File Templates.
 */
class NetcoolColumn {
    static searchable = true;
    String netcoolName;
    String localName
    String rsOwner = "p"
    String type;
    boolean isDeleteMarker;

    static constraints = {
        netcoolName(key:[], nullable:false, blank:false);
        localName(nullable:false, blank:false);
        type(nullable:false, blank:false);
    }
}