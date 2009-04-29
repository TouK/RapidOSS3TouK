
def getParameters() {
    return [:]
}

def init() {
}

def cleanUp() {
}

def update(trap) {
    logger.debug("Received Trap :" + trap)
    def eventProps = [:]
	if (trap.Enterprise.startsWith("1.3.6.1.4.1.2011.2.15.1.7")) {
//		def huaweeScript = new HuaweeTraps();
//		eventProps = huaweeScript.processTrap(trap)
	}

	if (trap.Enterprise.startsWith("1.3.6.1.4.1.637")) {
//		def alcatelScript = new AlcatelTraps();
//		eventProps = alcatelScript.processTrap(trap)
	}

	if (trap.Enterprise.startsWith("1.3.6.1.4.1.555")){
		def testTraps = new TestTraps();
		eventProps = testTraps.processTrap(trap)
	}
	logger.debug("eventProps: " + eventProps)

	if (eventProps.size() > 0) {
		// action property is populated by the appropriate trap processor (AlcatelTraps, TestTraps, ...)
		switch(eventProps.action) {
			case "add" :
	     		RsRiEvent.notify(eventProps)
	     		break;
			case "update" :
				RsRiEvent.update(eventProps)
	     		break;
			case "clear" :
				def ev = RsRiEvent.get(name:eventProps.name)
				if (ev) {
					logger.debug("found event to clear" + ev.name)
					ev.clear()
				} else {
					logger.debug("could not find the event to clear: " + eventProps.name)
				}
	     		break;
			default: logger.debug("no action necessary")
		}
	}
}
