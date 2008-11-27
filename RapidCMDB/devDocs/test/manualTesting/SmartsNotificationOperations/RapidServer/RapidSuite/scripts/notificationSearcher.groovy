import connector.*;
import org.apache.log4j.Logger



logger.warn("*gonna do notsearcher");


def output=" "

def random=new Random(System.currentTimeMillis());
def limit=random.nextInt(100)+1000

output+=" gonna search ${limit} RsSmartsNotification items <br> "
SmartsNotification.search("alias:*",[max:limit]).results.each{

}
output+=" searched ${limit} items <br> "

limit=random.nextInt(100)+1000

output+=" gonna search ${limit} RsEvent items <br> "
RsEvent.search("alias:*",[max:limit]).results.each{

}
output+=" searched ${limit} items <br> "

limit=random.nextInt(100)+1000

output+=" gonna search ${limit} RsRiEvent items <br> "
RsRiEvent.search("alias:*",[max:limit]).results.each{

}
output+=" searched ${limit} items <br> "

logger.warn("done notsearcher");
