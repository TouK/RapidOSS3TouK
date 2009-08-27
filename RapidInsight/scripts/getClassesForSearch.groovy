import groovy.xml.MarkupBuilder

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Mar 6, 2009
* Time: 3:57:23 PM
* To change this template use File | Settings | File Templates.
*/
def domainClasses = [];
def rootClass = params.rootClass != null ? params.rootClass : "RsEvent"
def domainClass = web.grailsApplication.getDomainClass(rootClass);
domainClasses.add(domainClass);
domainClasses.addAll(domainClass.getSubClasses());
domainClasses = domainClasses.sort {it.fullName}
def sw = new StringWriter();
def builder = new MarkupBuilder(sw);
builder.Classes() {
    domainClasses.each {
        builder.Class(name: it.fullName)
    }
}
web.render(contentType: 'text/xml', text: sw.toString())

