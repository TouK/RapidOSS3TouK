package model
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: May 26, 2008
 * Time: 5:01:22 PM
 * To change this template use File | Settings | File Templates.
 */
class PropertyAction {
    public static final String CLEAR_RELATION = "clearrelation"
    public static final String SET_DEFAULT_VALUE = "setdefaultvalue"
    static searchable = {
        except = ["propType", "defaultValue"];
    };
    Long id;
    Long version;
    String rsOwner = "p"
    String propName;
    String reverseName="";
    String modelName;
    String action;
    String propTypeName = "";
    Class propType;
    Object defaultValue;
    boolean willBeDeleted = false;
    static transients = ["propType", "defaultValue"]
}