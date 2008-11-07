import java.text.SimpleDateFormat
import utils.TestingConstants;


Runtime.getRuntime().gc();
Runtime.getRuntime().gc();


def total = Runtime.getRuntime().totalMemory() / Math.pow(2,20);
def free = Runtime.getRuntime().freeMemory() / Math.pow(2,20);
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
def currentTime=sdf.format(Calendar.getInstance().getTime());

    

def used = total - free;

def file = new File("${TestingConstants.MEMORY_TEST_RESULTS_ROOT_DIR}/memory.txt");
def line = "Time: "+currentTime +"\tTotal: " +total + "\tFree: " + free + "\tUsed: " + used + "\n";
file.append(line);
return line;