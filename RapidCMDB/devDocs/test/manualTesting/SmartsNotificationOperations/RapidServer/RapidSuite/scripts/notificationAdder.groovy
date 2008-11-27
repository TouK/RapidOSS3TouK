import connector.*;

logger.warn("*gonna do notsender");

def smcon=SmartsConnector.get(name:NotificationOperationsConstants.NOTIFICATION_CONNECTOR_NAME);
def ds=smcon.ds;

def addlimit=50;
def addcount=0;

def errorcount
def random=new Random(System.currentTimeMillis());
addlimit.times{

	def elid=random.nextInt(500)+1000;
	def evid=random.nextInt(500)+1000;
	ds.addNotification(ClassName:"Router",InstanceName:"trouter${elid}",EventName:"tevent${evid}");
	addcount++;

}

logger.warn("done notsender, added ${addcount} events");