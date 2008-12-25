/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 23, 2008
 * Time: 2:23:36 PM
 * To change this template use File | Settings | File Templates.
 */
import datasource.EmailDatasource

def ds=EmailDatasource.get(name:"emailds")
def adapter=ds.getAdapter();

def params=[:]
params.from="mustafa"
params.subject="test subject"
params.to="abdurrahim"
params.body="test body"

adapter.sendMail(params)
