import application.RapidApplication

public class CorrelationProcessor {
	
	// VARIOUS CORRELATION CONFIGURATIONS
	static def syslogConfig = [name:"SyslogCorrelation", eventCount:2, period:1000]
	static def trapConfig = [name:"TrapCorrelation", eventCount:3, period:2000]
	
	// called everytime an event is created
	static def eventIsNotified(event){
	
		def logger = RapidApplication.getLogger()
		logger.debug("event.source:" + event.source) 
		
		//	CUSTOMIZE THE CONDITION
		if(event.source == "SyslogAdapter"){
			queueRsXinYCorrelation(event, syslogConfig)
		}
		//	CUSTOMIZE THE CONDITION
		if(event.source == "TrapAdapter"){
			queueRsXinYCorrelation(event, trapConfig)
		}
    }
    
    static def syslogAction(eventName){
    	// CUSTOMIZE ACTION TO BE TAKEN
    	RsEvent.add([name: "correlation" + System.currentTimeMillis()])
    }

    static def trapAction(eventName){
    	// CUSTOMIZE ACTION TO BE TAKEN
    	RsEvent.add([name: "correlation" + System.currentTimeMillis()])
    }
    
    
    private static def queueRsXinYCorrelation(event, correlationConfig){
    	    def logger = RapidApplication.getLogger()
	    def expireTime = System.currentTimeMillis() + correlationConfig.period
	    def identifier = correlationConfig.name + "|" + event.name
	    def myCorrelation = RsXinYCorrelation.add([eventId:event.name,identifier:identifier, willExpireAt:expireTime])
	    logger.debug("added RsXinYCorrelation with name: ${event.name}, identifier: $identifier and expireTime: $expireTime")
    }
    
    // will be called by the XTimesInYActions periodic script
	static def takeAction(){
	        def logger = RapidApplication.getLogger()
		RsXinYCorrelation.propertySummary("alias:*",["identifier"]).identifier.each {correlation,count->
			def correlationData = getCorrelationData(correlation)
			logger.debug("correlationData: " + correlationData)
			//logger.debug("correlationName :${correlationData.correlationName} eventName:${correlationData.eventName}")

			switch(correlationData.correlationName){
				case syslogConfig.name:
					if(count >= syslogConfig.eventCount){
						syslogAction(correlationData.eventName)
						removeUsedCorrelationItems(correlation)
					}
					break
				case trapConfig.name:
					if(count >= trapConfig.eventCount){
						trapAction(correlationData.eventName)
						removeUsedCorrelationItems(correlation)
					}
					break
			}
		}		
	}

    // will be called by the XTimesYActions periodic script
	static def removeExpiredCorrelationItems(){
		def logger = RapidApplication.getLogger()
		logger.debug("begin expired removal")
		def currentTime = System.currentTimeMillis()
		RsXinYCorrelation.list().each{
		    logger.debug("currentTime: $currentTime > it.willExpireAt: ${it.willExpireAt}")
		    if (currentTime > it.willExpireAt) {
		    	logger.debug("expired: ${it.identifier}")
		        it.remove()
		    }
		}
	}
    
    private static def getCorrelationData(correlation){
    	def tokens = correlation.split("\\|")
    	def data = [correlationName:tokens[0], eventName:tokens[1]]
    	return data;
    }
    
    private static def removeUsedCorrelationItems(correlation){
        def logger = RapidApplication.getLogger()
    	RsXinYCorrelation.search("identifier:$correlation").results.each{
    		logger.debug("used in correlation and removed: ${it.identifier}")
    		it.remove()
    	}
    }
}