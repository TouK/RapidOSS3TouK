Runtime.getRuntime().gc();
Runtime.getRuntime().gc();
Runtime.getRuntime().gc();
def total = Runtime.getRuntime().totalMemory();
def free = Runtime.getRuntime().freeMemory();
new File("memres.log").append ("MEM at time ${new Date()} -> Total: ${total/Math.pow(2, 20)} Used:${(total - free)/Math.pow(2, 20)}\n");