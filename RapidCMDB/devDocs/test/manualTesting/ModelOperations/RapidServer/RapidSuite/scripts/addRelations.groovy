def random=new Random(System.currentTimeMillis());

def addLimit=30;
def addCount=0;


def fictionList=[]
addCount=0;
Fiction.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(10)==0 && addCount<addLimit)
    {
       fictionList.add(it);
       addCount++;
    }
}

addCount=0;
Person.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(10)==0 && addCount<addLimit)
    {
       if(!fictionList.isEmpty())
       {    
            it.addRelation(referringBooks:fictionList.remove(0));
            addCount++;
       }
    }
}
logger.info("Added ${addCount} Ficton - Person Relations");


def scienceFictionList=[]
addCount=0;
ScienceFiction.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(10)==0 && addCount<addLimit)
    {
       scienceFictionList.add(it);
       addCount++;
    }
}

addCount=0;
Author.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(10)==0 && addCount<addLimit)
    {
        if(!scienceFictionList.isEmpty())
       {
            it.addRelation(books:scienceFictionList.remove(0));
            addCount++;
       }       
    }
}


logger.info("Added ${addCount} ScienceFicton - Author Relations");