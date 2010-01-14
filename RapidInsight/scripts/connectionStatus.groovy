import com.ifountain.core.connection.ConnectionManager;

RESULT="";

def totalCounts=[borrowed:0,active:0,idle:0,total:0];

logInfo("---------Connection Status----------")
def pools=ConnectionManager.pools;
pools.each{ poolName,pool ->
	def cons=pool.getBorrowedConnections();
	def total=pool.getNumActive() + pool.getNumIdle();

	logInfo("${poolName} > borrowed : ${cons.size()} \t active : ${pool.getNumActive()} \t idle : ${pool.getNumIdle()} \t total : ${total}");
	totalCounts.borrowed+=cons.size();
	totalCounts.active+=pool.getNumActive();
	totalCounts.idle+=pool.getNumIdle();
	totalCounts.total+=total;



}

logInfo("TOTAL > borrowed : ${totalCounts.borrowed} \t active : ${totalCounts.active} \t idle : ${totalCounts.idle} \t total : ${totalCounts.total}");

def logInfo(message)
{
	logger.warn(message);
	RESULT+=message.replace("\t","&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")+"<br>";
}

return RESULT;
