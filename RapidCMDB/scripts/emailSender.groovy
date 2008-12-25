/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 23, 2008
 * Time: 2:23:36 PM
 * To change this template use File | Settings | File Templates.
 */
import connection.EmailConnection
import datasource.EmailDatasource

def con=EmailConnection.add(name:"emailcon",smtpHost:"192.168.1.100",smtpPort:25,username:"testaccount",userPassword:"123",protocol:EmailConnection.SMTP)
if(con.hasErrors())
{
    return "Error occured. Reason"+ con.errors
}
def emailDs=EmailDatasource.add(name:"emailds",connection:con)
if(emailDs.hasErrors())
{
    return "Error occured. Reason"+ emailDs.errors
}


def ds=EmailDatasource.get(name:"emailds")


def params=[:]
params.from="mustafa"
params.subject="test subject"
params.to="abdurrahim"
params.body="test body"

ds.sendEmail(params)
