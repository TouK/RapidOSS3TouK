<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="main" />
  <title>Login</title>
</head>
<body>
<script type="text/javascript">
    document.getElementById('logoutwrp').style.display = 'none';
</script>
<div class="front"><h1 >Login</h1></div>
  <g:render template="/common/messages" model="[flash:flash]"></g:render>
  <g:form action="signIn">
    <input type="hidden" name="targetUri" value="${targetUri}" />
  <center>
   <div class="loginbox">
       <table class="login">
      <tbody>
        <tr>
          <td>Username:</td>
          <td><input type="text" name="login" value="${username}" /></td>
        </tr>
        <tr>
          <td>Password:</td>
          <td><input type="password" name="password" value="" /></td>
        </tr>
        %{--<tr>--}%
          %{--<td>Remember me?:</td>--}%
          %{--<td><g:checkBox name="rememberMe" value="${rememberMe}" /></td>--}%
        %{--</tr>--}%
        <tr>
          <td />
          <td><input type="submit" value="Sign in" /></td>
        </tr>
      </tbody>
    </table>
    </div>
   </center>

  </g:form>
</body>
</html>
