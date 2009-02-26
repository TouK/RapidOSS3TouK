package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 29, 2009
 * Time: 6:58:14 PM
 * To change this template use File | Settings | File Templates.
 */
class UiRightUnit extends UiLayoutUnit{
    static searchable = {
        storageType "FileAndMemory"
    };
    Long width = 200;
    Boolean resize = false;
    Long minWidth = 0;
    Long maxWidth = 0;
    static datasources = [:]
    static relations = [:]
    static constraints={
        resize(nullable:true)
        minWidth(nullable:true)
        maxWidth(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [:];
}