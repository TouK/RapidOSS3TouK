def random=new Random(System.currentTimeMillis());

def addlimit=50;

def addTimes=random.nextInt(addlimit)+20;

addTimes.times{
    def name="Fiction${random.nextInt(5000)}";
    Fiction.add(name:name,publishDate:new Date(),description:"fiction book ${Math.random()}",mainCharacterName:"myfictionless");
}
logger.info("Added ${addTimes} Ficton");

addTimes=random.nextInt(addlimit)+20;
addTimes.times{
    def name="ScienceFiction${random.nextInt(5000)}";
    ScienceFiction.add(name:name,publishDate:new Date(),description:"fiction book ${Math.random()}");
}
logger.info("Added ${addTimes} ScienceFicton");

addTimes=random.nextInt(addlimit)+20;
addTimes.times{
    def name="Author${random.nextInt(5000)}";
    Author.add(name:name,birthDate:new Date(),address:"street ${Math.random()}",email:"@d@g@@f@e@s@@a@",numberOfBooks:Random.nextInt(5000));
}
logger.info("Added ${addTimes} Author");


addTimes=random.nextInt(addlimit)+20;
addTimes.times{
    def name="Person${random.nextInt(5000)}";
    Person.add(name:name,birthDate:new Date(),address:"street ${Math.random()}",email:"@d@g@@f@e@s@@a@");
}
logger.info("Added ${addTimes} Person");

