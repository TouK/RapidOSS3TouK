import connector.*;
logger.warn("*gonna do notdeleter");

def smcon=SmartsConnector.get(name:"smnot");
def ds=smcon.ds;

def output=" ";

def deletelimit=50;
def deletecount=0;

def random=new Random(System.currentTimeMillis());

SmartsNotification.search("alias:*",[max:2500]).results.each{	
	if(random.nextInt(10)==0 && deletecount<deletelimit)
	{
		output+=" Gonna clear ${it} <br>";
		//ds.archiveNotification(ClassName:it.className,InstanceName:it.instanceName,EventName:it.eventName,User:"ProxyUser",AuditTrailText:"cleared by notdeleter script");
		ds.clearNotification(ClassName:it.className,InstanceName:it.instanceName,EventName:it.eventName,User:"ProxyUser",SourceDomainName:"Proxy",AuditTrailText:"cleared by notdeleter script");
		output+=" Cleared ${it} <br>";
		deletecount++;

	}
}

logger.warn("done notdeleter, deleted ${deletecount} events");

//return output;
