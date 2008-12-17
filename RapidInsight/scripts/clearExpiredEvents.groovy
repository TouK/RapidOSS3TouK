/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 17, 2008
 * Time: 1:33:03 PM
 * To change this template use File | Settings | File Templates.
 */
logger.info("Preparing to clear expired exvents");
def events = RsEvent.search("willExpireAt:[1 TO ${System.currentTimeMillis()}]").results;
if(!events.isEmpty())
{
    logger.warn("Clearing expired exvents ${events}");
    events.each{
        it.clear();
    }
}
else
{
    logger.info("No expired events found");    
}
