package timer;
class TimerFactory 
{
	static Map timers = [:]
	public static void clear()
	{
		timers = [:]
	}
	public static void startTimer(String name)
	{
		timer.DurationTimer timer = timers[name];
		if(timer == null)
		{
			timer = new timer.DurationTimer();
			timers[name] = timer;
		}
		timer.start();
	}
	public static void stopTimer(String name)
	{
		timer.DurationTimer timer = timers[name];
		if(timer != null)
		{
			timer.stop();
		}
	}
	
	public static void stopAll()
	{
		timers.each{key, timer->
			timer.stop();
		}
	}
	
	public static String timersToString()
	{
		def str = "";
		timers.each{key, timer->
			str+=key+"\t\t:"+timer.toString()+"\n";
		}
		return str;
		
	}
}
