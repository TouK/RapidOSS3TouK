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

//predefined RrdArchives
def oneHour = RrdArchive.get(name:"1hour")
def twelveHours = RrdArchive.get(name:"12hours")
def oneDay = RrdArchive.get(name:"1day")

long ntime = Date.now() - 50 * 300000
      
def sampleRrdVariable = RrdVariable.add(
								name:"sampleRrdVariable",					//name of RrdVariable
								resource:"default",							//Resource of this variable. Mostly for groupping
								type:"GAUGE",								//Type of this variable. See documentation for further understanding
								heartbeat:600,								//heartbeat of this variable. See documentation for further understanding
								startTime: ntime,							//start time of this variable
								step:300,									//step interval between data updating points
								archives: [oneHour, twelveHours, oneDay],	//archives of this variable
						        file: "sampleRrdDB.rrd"                     //file name of rrd db
                        )

sampleRrdVariable.createDB()

50.times
{
	sampleRrdVariable.updateDB(time: (ntime + (it+1) * 300000) , value : new Random().nextInt(1000))
}
