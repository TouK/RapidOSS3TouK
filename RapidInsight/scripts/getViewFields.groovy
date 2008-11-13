import groovy.xml.MarkupBuilder
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass

def extraFilteredProps = ["rsDatasource", "id"];
def allProps = [];
GrailsDomainClass domainClass = web.grailsApplication.getDomainClass("RsEvent")
domainClass.getSubClasses().each{
    allProps.addAll(DomainClassUtils.getFilteredProperties(it.name, extraFilteredProps));
}
allProps.addAll(DomainClassUtils.getFilteredProperties("RsEvent", extraFilteredProps));
def sortedProps = allProps.sort{it.name}
def propertyMap = [:]
def writer = new StringWriter();
def builder = new MarkupBuilder(writer);
builder.Fields() {
    sortedProps.each {
        def propertyName = it.name;
        if(!propertyMap.containsKey(propertyName)){
            builder.Field(Name: propertyName)
            propertyMap.put(propertyName, propertyName);
        }
    }
}
return writer.toString();