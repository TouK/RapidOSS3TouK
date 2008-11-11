import connector.*;
import org.apache.log4j.Logger




logger.info("Starting searchInstances");

def output=" "

def random=new Random(System.currentTimeMillis());
def limit=random.nextInt(20)+5

limit.times{
    Book.search("alias:*",[max:150]);
    Fiction.search("alias:*",[max:150]);
    ScienceFiction.search("alias:*",[max:150]);
    Person.search("alias:*",[max:150]);
    Author.search("alias:*",[max:150]);
}

logger.info("Search all models ${limit} times");

logger.info("Ended searchInstances");


