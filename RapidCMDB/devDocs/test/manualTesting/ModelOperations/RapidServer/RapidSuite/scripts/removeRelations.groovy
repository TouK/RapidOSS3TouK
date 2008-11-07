def random=new Random(System.currentTimeMillis());

def deleteLimit=40;
def deleteCount=0;

//Note that no books are added to repo but Fictons and ScienceFictions are added
Book.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(10)==0 && deleteCount<deleteLimit)
    {
       it.removeRelation(mainCharacter:it.mainCharacter);
       deleteCount++;
    }
}
logger.info("Deleted ${deleteCount} Books' all relations ");

deleteCount=0;
Author.search("alias:*",[max:1000]).results.each{
    if(random.nextInt(10)==0 && deleteCount<deleteLimit)
    {
       it.removeRelation(books:it.books);
       deleteCount++;
    }
}
logger.info("Deleted ${deleteCount} Authors' all relations ");
