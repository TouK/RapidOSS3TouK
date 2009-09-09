import groovy.xml.MarkupBuilder

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Sep 4, 2009
* Time: 6:19:25 PM
* To change this template use File | Settings | File Templates.
*/
def buttons = [
        [displayName:"Last Hour", query:"[currenttime-1HOURS/HOUR TO currenttime]"],
        [displayName:"Last Day", query:"[currenttime-1DAYS/DAY TO currenttime]"],
        [displayName:"Last Month", query:"[currenttime-1MONTHS/MONTH TO currenttime]"],
        [displayName:"All", query:"[currenttime-30MONTHS/MONTH TO currenttime]", selected:true]
]

def fields = [
    [displayName:"changedAt", name:"changedAt"],
    [displayName:"createdAt", name:"createdAt"]
]

def sw = new StringWriter();
def mb = new MarkupBuilder(sw);
mb.ButtonAndFieldsConfig(){
    mb.Buttons{
        buttons.each{
            mb.Button(it)
        }
    }
    mb.Fields{
        fields.each{
            mb.Field(it)
        }
    }
}

return sw.toString();