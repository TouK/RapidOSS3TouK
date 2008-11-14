import org.apache.commons.lang.math.RandomUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 31, 2008
 * Time: 1:34:14 PM
 * To change this template use File | Settings | File Templates.
 */
def DemoValues = new NetcoolDemoValues();
def t = System.nanoTime();
def totalTime = 0;
def totalEventInsertTime = 0;
def totalJournalInsertTime = 0;
def totalInsertedEvents = 0;
def totalInsertedJournals = 0;
new File("perf.log").delete();
println "Inserted number of events, Number of Journals, Event insertion time, journal insertion time, total  time";
for(int i=0; i < 1000000; i++)
{
    def props = DemoValues.getEventProperties();
    def tempT = System.nanoTime();
    def event = NetcoolEvent.add(props);
    def interval = System.nanoTime() - tempT;
    if(event.hasErrors())
    {
        println event.errors;
    }
    else
    {
        totalInsertedEvents++;
        totalEventInsertTime += interval;
        for(int j = 0; j < DemoValues.nextNumber(5); j++)
        {
            def journalProps = DemoValues.getJournalProperties(props.rsDatasource, props.servername, props.serial);
            tempT = System.nanoTime();
            def journal = NetcoolJournal.add(journalProps);
            interval = System.nanoTime() - tempT;
            if(journal.hasErrors())
            {
                println journal.errors
            }
            else
            {
                totalInsertedJournals++;
                totalJournalInsertTime += interval;
            }
        }
    }
    if(i  % 100 == 0)
    {
        totalTime = System.nanoTime() - t;
        def usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/ Math.pow(2, 20);
        new File("perf.log").append("${totalInsertedEvents}, ${totalInsertedJournals},${totalEventInsertTime/Math.pow(10,9)},${totalJournalInsertTime/Math.pow(10,9)},${totalTime/Math.pow(10,12)},${usedMem}\n");
        totalEventInsertTime = 0;
        totalJournalInsertTime = 0;
        totalInsertedEvents = 0;
        totalInsertedJournals = 0;
        t = System.nanoTime();
    }
}



