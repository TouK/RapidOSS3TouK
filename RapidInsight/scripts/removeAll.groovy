import org.codehaus.groovy.grails.commons.ApplicationHolder

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Aug 22, 2008
 * Time: 5:07:01 PM
 * To change this template use File | Settings | File Templates.
 */
def excludedList = [];

def exludedListQuery = "alias:"+excludedList.join(" OR alias:")
ApplicationHolder.application.getDomainClasses().each {grailsDomainClass ->
    Class domainClass = grailsDomainClass.clazz;
    if(domainClass.name.indexOf(".") < 0)
    {
        if(!excludedList.isEmpty())
        {
            query = "(alias:*) NOT (${exludedListQuery})"
            domainClass.removeAll(query);
        }
        else
        {
            domainClass.removeAll();
        }
    }
}

return "Script executed successfully"