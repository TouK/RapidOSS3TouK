import utils.TestingConstants;


def pid=java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
def fileName = TestingConstants.getHeapDumpFile().path;
def javaDir = "C:/Program Files/Java/jdk1.6.0_04/bin/";
def cmd = "${javaDir}jmap -dump:format=b,file=${fileName} ${pid}"


def process = "${cmd}".execute();
return process.in.text;