import groovy.xml.MarkupBuilder
import com.ifountain.rcmdb.domain.util.DomainClassUtils

def notificationProps = DomainClassUtils.getFilteredProperties(RsSmartsNotification.class.getName(), ["id", "rsDatasource"]);
def props = notificationProps.sort{it.name};
def writer = new StringWriter();
def builder = new MarkupBuilder(writer);
builder.Fields() {
    props.each {
        builder.Field(Name: it.name)
    }
}
return writer.toString();