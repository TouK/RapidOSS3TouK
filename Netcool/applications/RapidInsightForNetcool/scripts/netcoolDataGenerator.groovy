import datasource.NetcoolDatasource

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 31, 2008
 * Time: 1:34:14 PM
 * To change this template use File | Settings | File Templates.
 */
def static RealValues = new NetcoolRealValues();
def t = System.nanoTime();
def totalTime = 0;
def totalEventInsertTime = 0;
def totalJournalInsertTime = 0;
def totalInsertedEvents = 0;
def totalInsertedJournals = 0;
NetcoolDatasource ds = null;
NetcoolDatasource.list().each
{
    ds = it;   
}
if(ds == null)
{
    throw new Exception("No netcooldatasopurce defined");
}
def events = ds.getEvents();
events.each{
    ds.removeEvent(Long.parseLong(it.SERVERSERIAL));
}
for(int i=0; i < 10; i++)
{
    def props = RealValues.getEventProperties();
    ds.addEvent(props);
}




