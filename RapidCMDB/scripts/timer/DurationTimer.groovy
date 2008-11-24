package timer;
public class DurationTimer {
    private long snapshot = -1;
    long totalTime = 0;
    public Timer() {
        
    }
    public void start()
    {
	    snapshot = System.nanoTime();
    }

    public void stop(){
	    if(snapshot>=0)
	    {
	        totalTime += System.nanoTime() - snapshot;
	        snapshot =-1;
    	}
    }
    
    public String toString()
    {
    	return "${totalTime/Math.pow(10,9)}"
    }
}

