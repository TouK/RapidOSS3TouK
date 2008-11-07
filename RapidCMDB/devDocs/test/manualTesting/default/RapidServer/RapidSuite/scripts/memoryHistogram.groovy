import utils.TestingConstants

def pid=java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
def javaDir = "C:/Program Files/Java/jdk1.6.0_04/bin/";
def cmd = "${javaDir}jmap -histo ${pid}"


def process = "${cmd}".execute()
TestingConstants.getHeapHistogramFile().append(process.in.text);