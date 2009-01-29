package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 29, 2009
 * Time: 6:58:14 PM
 * To change this template use File | Settings | File Templates.
 */
class UiLeftUnit extends UiLayoutUnit{
    static searchable = {
        storageType "File"
    };
    Long width = 200;
    Boolean resize = false;
    static datasources = [:]
    static relations = [:]
    static constraints={
        resize(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [:];
}