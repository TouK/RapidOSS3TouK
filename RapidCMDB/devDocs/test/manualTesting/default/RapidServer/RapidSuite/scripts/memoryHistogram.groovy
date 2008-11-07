import java.sql.Timestamp;
import java.text.SimpleDateFormat;

def pid=java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
def javaDir = "C:/Program Files/Java/jdk1.6.0_04/bin/";
def cmd = "${javaDir}jmap -histo ${pid}"
def timeStampFormat = "_dd_HH_mm_ss";
def formatter = new SimpleDateFormat(timeStampFormat);


def process = "${cmd}".execute()
new File("memory${formatter.format(new Timestamp(System.currentTimeMillis()))}.txt").append(process.in.text);