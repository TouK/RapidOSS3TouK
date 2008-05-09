package model
/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: May 1, 2008
 * Time: 4:36:25 AM
 * To change this template use File | Settings | File Templates.
 */
class ModelModificationSqls {
    String sqlStatement;

    static constraints = {
        sqlStatement(unique:true)
    }
}