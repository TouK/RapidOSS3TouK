def random=new Random(System.currentTimeMillis());

logger.info("Starting removeInstances");

def deletelimit=40;
def deletecount=0;

def deleteList=[]

logger.info("loop with delete");
deletecount=0;
Fiction.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(10)==0 && deletecount<deletelimit)
    {
       it.remove();
       deletecount++;
    }
}
logger.info("loops ended");



logger.info("Deleted ${deletecount} Ficton");

deletecount=0;
ScienceFiction.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(10)==0 && deletecount<deletelimit)
    {
       it.remove();
       deletecount++;
    }
}

logger.info("Deleted ${deletecount} ScienceFicton");


deletecount=0;
Person.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(10)==0 && deletecount<(deletelimit*2))
    {
       it.remove();
       deletecount++;
    }
}

logger.info("Deleted ${deletecount} Person");

logger.info("ended removeInstances");