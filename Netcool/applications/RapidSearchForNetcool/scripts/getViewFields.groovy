import groovy.xml.MarkupBuilder
import com.ifountain.rcmdb.domain.util.DomainClassUtils

def extraFilteredProps = ["connectorname", "id"];
def allProps = DomainClassUtils.getFilteredProperties("NetcoolEvent", extraFilteredProps);
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