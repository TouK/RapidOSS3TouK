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
    Long idSize;
    static hasMany = ['modelProperties':GeneratedModelProperty, 'fromRelations':GeneratedModelRelation];
    static mappedBy=["fromRelations":"firstModel"]
    static constraints = {
        parentModelName(nullable:true);
    }
}