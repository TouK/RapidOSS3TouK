/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Aug 6, 2009
 * Time: 4:38:26 PM
 * To change this template use File | Settings | File Templates.
 */
class MobileFilters {
    def filters = {
        allURIs(uri: '/**') {
            before = {
                String userAgent = String.valueOf(request.getHeader("user-agent")).toLowerCase();
                if (userAgent.indexOf("mobile") >= 0 || userAgent.indexOf("ipod") >= 0 || userAgent.indexOf("iphone") >= 0)
                {
                	if(request.getRequestURL().indexOf("/auth") < 0 && request.getRequestURL().indexOf("/layouts") < 0  && request.getRequestURL().indexOf("/mobile") < 0)
                    {
                    	response.sendRedirect("/RapidSuite/mobile/home.gsp");
                    }
                }
            }
        }
    }
}