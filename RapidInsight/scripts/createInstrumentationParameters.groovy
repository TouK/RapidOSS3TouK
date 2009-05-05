def paramList=[];

paramList.add([name:"system.totalMemory",enabled:true])
paramList.add([name:"system.usedMemory",enabled:true])
paramList.add([name:"user.userLogin",enabled:true])
paramList.add([name:"ui.objectDetails",enabled:true])
paramList.add([name:"ui.eventDetails",enabled:true])


paramList.each{ props->
    def param=InstrumentationParameters.add(props);
    if(param.hasErrors())
    {
        logger.warn("Error occured while adding InstrumentationParameters with props ${props}, Reason : ${params.errors}")
    }
}

