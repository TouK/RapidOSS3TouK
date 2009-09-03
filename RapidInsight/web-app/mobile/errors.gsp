<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Sep 3, 2009
  Time: 2:10:22 PM
  To change this template use File | Settings | File Templates.
--%>

<script>
    iui.defaultFailureFunction = function(req, httpStatus){
        var errorEl = document.getElementById('mobileErrors');
        var innerHTML;
        if(httpStatus == -1){
            innerHTML = 'Request received timeout.'
        }
        else if(httpStatus == 13030){
            innerHTML = 'Server is not reachable.'
        }
        else if(httpStatus == 404){
             innerHTML = 'Specified url cannot be found.'
        }
        else if (httpStatus == 500){
            innerHTML = 'Internal server error.'
        }
        errorEl.innerHTML = innerHTML
        iui.showPage(errorEl);
    }
</script>
<div style="color:red;text-align:center;font-weight:bold;padding-top:10px" id="mobileErrors"></div>