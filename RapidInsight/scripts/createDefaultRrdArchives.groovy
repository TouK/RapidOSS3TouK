
//note that these all assumes step of RrdVariable to be 300 seconds(5 minutes)
//otherwise timespans of these archives will not be what their name say
                  
RrdArchive.add(name:"1hour",function: "AVERAGE",xff: 0.5,step:1,numberOfDatapoints:12);
RrdArchive.add(name:"6hours",function:"AVERAGE",xff:0.5,step:4,numberOfDatapoints:18);
RrdArchive.add(name:"12hours",function:"AVERAGE",xff:0.5,step:6,numberOfDatapoints:24);
RrdArchive.add(name:"1day",function:"AVERAGE",xff:0.5,step:9,numberOfDatapoints:32);
RrdArchive.add(name:"1week",function:"AVERAGE",xff:0.5,step:16,numberOfDatapoints:126);
RrdArchive.add(name:"2weeks",function:"AVERAGE",xff:0.5,step:21,numberOfDatapoints:192);
RrdArchive.add(name:"1month",function:"AVERAGE",xff:0.5,step:30,numberOfDatapoints:288);
RrdArchive.add(name:"3months",function:"AVERAGE",xff:0.5,step:54,numberOfDatapoints:480);
RrdArchive.add(name:"6months",function:"AVERAGE",xff:0.5,step:72,numberOfDatapoints:720);
RrdArchive.add(name:"1year",function:"AVERAGE",xff:0.5,step:96,numberOfDatapoints:1080);  


