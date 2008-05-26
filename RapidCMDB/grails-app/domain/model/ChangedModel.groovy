package model
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: May 23, 2008
 * Time: 5:30:15 PM
 * To change this template use File | Settings | File Templates.
 */
class ChangedModel {
    String modelName;
    boolean isDeleted = false;
    boolean isPurged = false;

    public boolean equals(Object obj) {
        if(obj instanceof ChangedModel)
        {
            return obj.modelName == modelName;
        }
        return false;
    }

}