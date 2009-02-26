package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 6, 2009
 * Time: 10:07:31 AM
 * To change this template use File | Settings | File Templates.
 */
class UiImageObjectMapContent extends UiObjectMapContent{
    static searchable = {
        storageType "FileAndMemory"
    };

    String mapping="";
    static datasources = [:]
    static relations = [:]
    static constraints={
    }

    static propertyConfiguration= [:]
    static transients = [:];
}