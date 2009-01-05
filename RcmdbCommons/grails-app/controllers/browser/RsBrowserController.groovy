package browser

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import com.ifountain.rcmdb.domain.util.DomainClassUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 5, 2009
* Time: 10:40:01 AM
* To change this template use File | Settings | File Templates.
*/
class RsBrowserController {

    def index = {
        def domainClasses = grailsApplication.domainClasses;
        def domainClassList = domainClasses;
        if (params.sort && params.order == "desc") {
            domainClassList = domainClasses.sort {first, second ->
                return first.fullName < second.fullName ? 1 : -1;
            }
        }
        else {
            domainClassList = domainClasses.sort {it.fullName}
        }
        return [domainClassList: domainClassList]
    }

    def listDomain = {
        if(params.max == null){
            params.max = 20;
        }
        def domainClass = grailsApplication.getArtefactByLogicalPropertyName(DomainClassArtefactHandler.TYPE, params.domain)
        if (domainClass) {
            def mc = domainClass.metaClass;
            def propertyList = [];
            def properties = domainClass.clazz."getPropertiesList"();
            def propertiesCanBeListed = properties.findAll{it.name != "id" && !it.isKey && !it.isRelation && !it.isOperationProperty || (it.isRelation && it.isOneToOne() && it.isManyToOne())}
            def count = domainClass.clazz."count"();
            def keySet = domainClass.clazz."keySet"();
            if(propertiesCanBeListed.size() + keySet.size() > 5){
                propertyList = keySet;
            }
            else{
                propertyList.addAll(keySet)
                propertyList.addAll(propertiesCanBeListed)
            }
            def objectList = mc.invokeStaticMethod(mc.theClass, "list", [params] as Object[])
            return [objectList: objectList, propertyList: propertyList, count:count]
        }
        else {
            flash.message = "Domain class with name ${params.domain} does not exist"
            redirect(action: index);
        }

    }

    def show = {

    }
}