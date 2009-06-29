/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 8, 2009
* Time: 4:03:26 PM
* To change this template use File | Settings | File Templates.
*/
def objectName=params.objectName;
def model=params.model;




if(model=="maintenance")
{
    if(objectName)
    {
        def inMaintenance=params.inMaintenance;
        def minutes=params.minutes;
        def source = "User ${web.session.username}";
        def info=params.info;

        if(inMaintenance)
        {
            if(minutes)
            {
                try{
                    minutes=minutes.toInteger();
                }
                catch(e)
                {
                    throw new Exception("Minutes should be an integer");
                }

                def endTime=new Date(new Date().getTime()+(1000*60*minutes))
                def props=["objectName":objectName,"source":source,"info":info,"ending":endTime];
                RsInMaintenance.putObjectInMaintenance (props);
            }
            else
            {
                def props=["objectName":objectName,"source":source,"info":info];
                RsInMaintenance.putObjectInMaintenance (props);
            }
        }
        else
        {
             RsInMaintenance.takeObjectOutOfMaintenance (objectName);
        }
    }

}
else if(model=="schedule")
{
    def mode=params.mode;
    if(mode=="create")
    {
        if(objectName)
        {
            def info=params.info;
            def startTime=getDate("startTime");
            def endTime=getDate("endTime");
            RsInMaintenanceSchedule.addObjectSchedule(objectName,info,startTime,endTime);
        }
    }
    else if(mode=="delete")
    {
       RsInMaintenanceSchedule.removeSchedule(params.scheduleid);
    }
}


def getDate(dateName)
{
    def propertiesToGet=["year","month","day","hour","minute"];

    def props=[:]

    propertiesToGet.each{   propName ->
        try{
            props[propName]=params[dateName+"_"+propName].toInteger();
        }
        catch(e)
        {
            throw new Exception("${propName} of ${dateName} is missing or not an integer");
        }
    }

    def calendar=Calendar.getInstance()
    calendar.clear()
    calendar.setLenient(false);
    calendar.set(props.year, props.month-1, props.day, props.hour, props.minute);

    return calendar.getTime();
}
