/**
 * User: ifountain
 * Date: Jul 3, 2009
 * Time: 10:36:17 AM
 */

/*
 * Sample RrdVariable
 *		It uses default archives, one hour, twelve hours, and one day.
 *
 */

long ntime = Date.now() - 50 * 300000

def sampleRrdVariable = RrdVariable.add(
								name:"sampleRrdVariable",					//name of RrdVariable
								resource:"default",							//Resource of this variable. Mostly for groupping
								type:"GAUGE",								//Type of this variable. See documentation for further understanding
								heartbeat:300,								//heartbeat of this variable. See documentation for further understanding
								startTime: ntime,							//start time of this variable
								frequency:120,								//step interval between data updating points
						)

sampleRrdVariable.createDB()

50.times
{
	sampleRrdVariable.updateDB(new Random().nextInt(1000), (ntime + (it+1) * 300000))
}
