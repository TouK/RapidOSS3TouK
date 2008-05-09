package model
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 29, 2008
 * Time: 6:02:26 PM
 * To change this template use File | Settings | File Templates.
 */
class GeneratedModelRelation {
    GeneratedModel firstModel;
    GeneratedModel secondModel;
    String firstName;
    String secondName;
    String firstCardinality;
    String secondCardinality;
    boolean isReverse = false;
    Long relationId;
    static mappedBy=["firstModel":"fromRelations"]
    static constraints = {
        firstModel(nullable:true)
        secondModel(nullable:true)
    }

    public String toString()
    {
    	return firstModel.modelName + "_" + secondModel.modelName + "_" + firstName;
    }
}