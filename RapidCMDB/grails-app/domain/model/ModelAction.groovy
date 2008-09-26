package model
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 3, 2008
 * Time: 6:17:51 PM
 * To change this template use File | Settings | File Templates.
 */
class ModelAction {
    public static final String DELETE_MODEL = "deletModel"
    public static final String DELETE_ALL_INSTANCES = "deleteall"
    public static final String GENERATE_RESOURCES = "generateResources"
    static searchable = true;
    String rsOwner = "p"
    String modelName;
    String action;
    boolean willBeDeleted = false;
}