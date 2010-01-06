def bf = new StringBuffer();

def threads=getThreadNames(bf);

def output="----------- Thread List Start------------------- <br>";
output+="Total : ${threads.size()} threads : <br>";
output+=bf.toString()+"<br>"+threads.join("<br>");
output+="<br> ----------- Thread List End------------------- <br>";

logger.warn(output.replace("<br>","\n"))
return output;



def getThreadNames(StringBuffer bf) {
    ThreadGroup group = Thread.currentThread().getThreadGroup();
    ThreadGroup parent = null;
    while ( (parent = group.getParent()) != null ) {
      group = parent;
    }
    Thread[] threads = new Thread[group.activeCount()];
    group.enumerate(threads);
    java.util.HashSet set = new java.util.HashSet();
        java.util.HashSet tset = new java.util.HashSet();
    for (int i=0; i < threads.length; ++i) {
      if (threads[i] != null && threads[i].isAlive()) {
		 try {
          set.add(threads[i].getThreadGroup().getName()+", "
                  +threads[i].getName()+", "+i+", "
                  +threads[i].getPriority());
        tset.add(threads[i]);
        } catch (Throwable e) {e.printStackTrace();bf.append(e.toString());}
      }
    }
    String[] result = (String[]) set.toArray(new String[0]);
    java.util.Arrays.sort(result);
    return result;
  }