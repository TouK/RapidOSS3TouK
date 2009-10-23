import com.ifountain.comp.utils.HttpUtils

//configuration, urlPrefix, urlSuffix identifies test location , they are left of build number and right of build number
startBuild=2100;
endBuild=2120;


//sample url to split
//http://192.168.1.134:8080/job/RapidCMDBTests/2113/testReport/connectorScriptTests/NotificationSubscriberTest/testNotificationSubscriberWithEvents_Notify_Update_Clear/

urlPrefix="http://192.168.1.134:8080/job/RapidCMDBTests/";
urlSuffix="/testReport/connectorScriptTests/NotificationSubscriberTest/testNotificationSubscriberWithEvents_Notify_Update_Clear/";

//configuration ended

HttpUtils httpUtil=new HttpUtils();


def runs=[];

for(build=startBuild;build<=endBuild;build++)
{
    try{
        def res=httpUtil.doGetRequest ("${urlPrefix}${build.toString()}${urlSuffix}",[:]);

        def runInfo=[build:build];
        runs.add(runInfo);

        def resTagStartIndex=res.indexOf('<h1 class="result-');
        if(resTagStartIndex<0)
        {
            runInfo.result="unknown";
        }
        else
        {
            def resStartIndex=res.indexOf('>',resTagStartIndex+1);
            def resEndIndex=res.indexOf('<',resStartIndex+1);
            runInfo.result=res.substring(resStartIndex,resEndIndex);

        }

        println "${build} is ${runInfo.result} "

    }
    catch(com.ifountain.comp.utils.HttpStatusException e)
    {
        println "${build} does not exist "
    }
    Thread.sleep(100);
}

println "---------------------------------------------------------"
println "Test History Is"

runs.each{  runInfo ->
    println "${runInfo.build} ${runInfo.result}"
}