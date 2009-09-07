import groovy.xml.MarkupBuilder

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Sep 4, 2009
* Time: 6:19:25 PM
* To change this template use File | Settings | File Templates.
*/
def buttons = [
        [displayName:"Last Hour", query:"[currenttime-1HOURS TO currenttime]"],
        [displayName:"Last Day", query:"[currenttime-1DAYS TO currenttime]"],
        [displayName:"Last Month", query:"[currenttime-1MONTHS TO currenttime]"],
]

def sw = new StringWriter();
def mb = new MarkupBuilder(sw);

mb.Buttons{
    buttons.each{
        mb.Button(it)
    }
}

return sw.toString();