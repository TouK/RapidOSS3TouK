def random=new Random(System.currentTimeMillis());

logger.info("Starting removeRelations");

def deleteLimit=20;
def deleteCount=0;

//Note that no books are added to repo but Fictons and ScienceFictions are added
Book.search("alias:*",[max:3000]).results.each{    
    if(it.mainCharacter!=null  && deleteCount<deleteLimit)
    {
	   //long t = System.nanoTime();
       it.removeRelation(mainCharacter:it.mainCharacter);
       //logger.info((System.nanoTime()-t)/Math.pow(10,9));
       deleteCount++;
    }
}
logger.info("Deleted ${deleteCount} Books' all relations ");

deleteCount=0;
Author.search("alias:*",[max:3000]).results.each{
    if(it.books.size()>0  && deleteCount<deleteLimit)
    {
	   //long t = System.nanoTime();
       it.removeRelation(books:it.books);
       //logger.info((System.nanoTime()-t)/Math.pow(10,9));
       deleteCount++;
    }
}
logger.info("Deleted ${deleteCount} Authors' all relations ");

logger.info("Ended removeRelations");
