RrdVariable.removeAll()
RrdArchive.removeAll()

def oneH = RrdArchive.add(name:"1h1m", function:"AVERAGE", xff:0.5, step:1, row:60)
def sixH = RrdArchive.add(name:"6h4m", function:"AVERAGE", xff:0.5, step:4, row:90)
def twelveH = RrdArchive.add(name:"12h6m", function:"AVERAGE", xff:0.5, step:6, row:120)
def twentyfourH = RrdArchive.add(name:"24h9m", function:"AVERAGE", xff:0.5, step:9, row:160)
def fiveDays = RrdArchive.add(name:"5d30m", function:"AVERAGE", xff:0.5, step:30, row:240)
def oneMonth = RrdArchive.add(name:"1M2h", function:"AVERAGE", xff:0.5, step:120, row:360)
def threeMonth = RrdArchive.add(name:"3M4h", function:"AVERAGE", xff:0.5, step:240, row:540)

def dataCount=1000;
def now=Date.now()-(dataCount*100000);

long ntime = now

def device1Cpu = RrdVariable.add(name:"d1cpuUtil", resource:"device1", type:"GAUGE", heartbeat:120, file:"yahoo.rrd",
                            startTime: ntime,step:60, archives: [oneH, sixH, twelveH, twentyfourH])

def device1Network = RrdVariable.add(name:"d1networkTraffic", resource:"device1", type:"GAUGE", heartbeat:120, file:"msn.rrd",
                          startTime: ntime, step:60, archives: [oneH, sixH, twelveH, twentyfourH])

def device2Cpu = RrdVariable.add(name:"d2cpuUtil", resource:"device2", type:"GAUGE", heartbeat:120, file:"ifountain.rrd",
                                startTime: ntime, step:60, archives: [oneH, sixH, twelveH, twentyfourH])


RrdVariable.searchEvery("resource:device*").each{ variable ->
	variable.createDB();
}



RrdVariable.searchEvery("resource:device*").each{ variable ->

	dataCount.times{
		def dataTime=now+(it*100000);
	    def value=(int)(Math.random()*20);
	    value+=100;
	    variable.updateDB(value,dataTime);
    }
}

def lastTime=now+((dataCount-1)*100000);

10.times {
	RsEvent.add(name:"eventd1${it}",elementName:"device1",changedAt:lastTime-(it*1000000));
	RsEvent.add(name:"eventd2${it}",elementName:"device2",changedAt:lastTime-(it*500000));
}

