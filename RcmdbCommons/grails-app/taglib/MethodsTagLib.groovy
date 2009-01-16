import com.ifountain.rcmdb.methods.MethodFactory

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 16, 2009
* Time: 10:18:06 AM
* To change this template use File | Settings | File Templates.
*/
class MethodsTagLib {
    def withSession = {attrs, body->
        def method = MethodFactory.createMethod (MethodFactory.WITH_SESSION_METHOD);
        method(attrs.username, {
        out << body();
        });
    }
}