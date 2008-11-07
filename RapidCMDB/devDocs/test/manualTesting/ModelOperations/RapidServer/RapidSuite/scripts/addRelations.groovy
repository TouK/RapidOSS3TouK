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
       def nextElement=fictionList.remove();
       if(nextElement)
       {
            it.addRelation(referringBooks:nextElement);
            addCount++;
       }
    }
}


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
       def nextElement=scienceFictionList.remove();
       if(nextElement)
       {
            it.addRelation(books:nextElement);
            addCount++;
       }
    }
}
