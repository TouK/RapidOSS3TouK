/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Mar 6, 2009
 * Time: 3:57:23 PM
 * To change this template use File | Settings | File Templates.
 */
def domainClasses = [];
def domainClass = web.grailsApplication.getDomainClass("RsHistoricalEvent");
domainClasses.add(domainClass);
domainClasses.addAll(domainClass.getSubClasses());
domainClasses = domainClasses.sort{it.fullName}

web.render(contentType:'text/xml'){
    Classes(){
        domainClasses.each{
            Class(name:it.fullName)
        }
    }
}