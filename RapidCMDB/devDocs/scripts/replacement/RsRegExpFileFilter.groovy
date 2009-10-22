package replacement

import org.apache.commons.io.filefilter.AbstractFileFilter
import java.util.regex.Pattern

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Oct 22, 2009
* Time: 11:35:51 AM
* To change this template use File | Settings | File Templates.
*/
class RsRegExpFileFilter extends AbstractFileFilter{
    Pattern pattern;
    public RsRegExpFileFilter(String regExp)
    {
        pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
    }
    public boolean accept(File file) {
        return accept(file, file.getName()); //To change body of overridden methods use File | Settings | File Templates.
    }

    public boolean accept(File file, String s) {
        return pattern.matcher (file.getCanonicalPath().replaceAll("\\\\", "/")).matches();
    }
    
}