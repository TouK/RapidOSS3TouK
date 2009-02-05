package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 29, 2009
 * Time: 6:58:14 PM
 * To change this template use File | Settings | File Templates.
 */
class UiTopUnit extends UiLayoutUnit{
    static searchable = {
        storageType "File"
    };
    Long height = 200;
    Boolean resize = false;
    Long maxHeight = 0;
    Long minHeight = 0;
    static datasources = [:]
    static relations = [:]
    static constraints={
        resize(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [:];
}