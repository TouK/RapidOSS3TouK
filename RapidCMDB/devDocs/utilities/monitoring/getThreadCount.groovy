public static String[] getThreadNames(StringBuffer bf) {
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
          set.add(threads[i].getThreadGroup().getName()+","
                  +threads[i].getName()+","+i+","
                  +threads[i].getPriority());
        tset.add(threads[i]);
        } catch (Throwable e) {e.printStackTrace();bf.append(e.toString());}
      }
    }
    String[] result = (String[]) set.toArray(new String[0]);
    java.util.Arrays.sort(result);
    return result;
  }
def bf = new StringBuffer();
def threads = getThreadNames(bf).join("<br>");
return "a" +bf.toString()+"<br>"+threads;