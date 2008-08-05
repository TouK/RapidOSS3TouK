/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Aug 4, 2008
 * Time: 6:34:13 PM
 * To change this template use File | Settings | File Templates.
 */
while(true)
{
    def searchRes = NetcoolEvent.search("id:[0 TO *]");
    if(searchRes.results.isEmpty())
    {
        break;
    }
    searchRes.results.each{
        it.remove();
    }
}

while(true)
{
    def searchRes = NetcoolJournal.search("id:[0 TO *]");
    if(searchRes.results.isEmpty())
    {
        break;
    }
    searchRes.results.each{
        it.remove();
    }
}
