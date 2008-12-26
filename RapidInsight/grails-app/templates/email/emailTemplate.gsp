<html>
<body>
Event Properties 
<ul>
<g:each in="${eventParams.entrySet()}">
<li>${it.key} : ${it.value}</li>
</g:each>
</ul>
</body>
</html>