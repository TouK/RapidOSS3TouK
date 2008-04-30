package model
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 29, 2008
 * Time: 6:01:25 PM
 * To change this template use File | Settings | File Templates.
 */
class GeneratedModel {
    String modelName;
    String parentModelName;
    static hasMany = ['modelProperties':GeneratedModelProperty, 'relations':GeneratedModelRelation];
    static constraints = {
        parentModelName(nullable:true);
    }
}