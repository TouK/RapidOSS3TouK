import com.ifountain.comp.utils.HttpUtils


def url="http://192.168.1.134:8080/job/RapidCMDBTests/2261/console";
def testStartPrefix="Running test";
def testEndPrefix="...";

HttpUtils httpUtil=new HttpUtils();

def res=httpUtil.doGetRequest (url,[:]);

def startIndex=res.indexOf(testStartPrefix);
while(startIndex>=0)
{
    def  endIndex=res.indexOf(testEndPrefix,startIndex+1);
    if(endIndex>0)
    {
        def testName=res.substring(startIndex+testStartPrefix.length(),endIndex)?.trim();
        println testName
        startIndex=res.indexOf(testStartPrefix,endIndex+1);
    }
    else
    {
        startIndex=-1;
    }

}