/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Nov 12, 2008
 * Time: 10:51:30 AM
 */
def targetURI = params.targetURI;
def domainClassName = params.domainClassName;
def domainClass = web.grailsApplication.getDomainClass(domainClassName);
try
{
    domainClass.clazz."reloadOperations"()
    web.flash.message = "Operation reloaded successfully"
}
catch(Exception e)
{
    web.flash.message = "Operation could not be reloaded successfully. Reason :" + e.getMessage()
}


web.redirect(uri:targetURI);