<html>
<head>
    <title>RapidCMDB Modeler Admin UI</title>
    <meta name="layout" content="main"/>
</head>
<body>

<div class="front"><h1>RapidCMDB Modeler Admin UI</h1></div>
<p style="margin-left:20px;width:80%">This is the basic administration UI where you can define model your classes and maintain them.</p>
<br>
<p/>
<p/>
<br>
<br>
<p/>
<div class="front">
    <table><tr><th width="50%">Modeling</th>  <th>Scripting</th></tr>
        <tr><td><p style="margin-left:20px;">Model your Managed Classes.</p>
            <br>
            <div class="dialog" style="margin-left:20px;">
                <ul style="margin-left:25px;">
                    <li class="controller"><g:link controller="model">Model Managed Classes</g:link></li>
                    <li class="controller"><g:link controller="datasourceName">Datasource Names</g:link></li>
                </ul>
            </div>
        </td>
            <td><p style="margin-left:20px;width:80%">Define and Run Scripts.</p>
                <br>
                <div class="dialog" style="margin-left:20px;width:60%;">
                    <ul style="margin-left:25px;">
                        <li class="controller"><g:link controller="script">Scripts</g:link></li>
                    </ul>
                </div>
            </td>
    </table></div>
<br>
<br>

<p/>


<p/>
<div class="front">
    <table><tr><th width="50%">User Management</th>  <th>Application Management</th></tr>
        <tr><td><p style="margin-left:20px;">Manage Users and their Roles.</p>
            <br>
            <div class="dialog" style="margin-left:20px;">
                <ul style="margin-left:25px;">
                    <li class="controller"><g:link controller="rsUser">Users</g:link></li>
                </ul>
            </div>
        </td>
            <td><p style="margin-left:20px;width:80%">Reload Application, Export/Import configuration.</p>
                <br>
                <div class="dialog" style="margin-left:20px;width:60%;">
                    <ul style="margin-left:25px;">
                        <li class="controller"><g:link controller="application">Application Management</g:link></li>
                    </ul>
                </div>
            </td>
    </table></div>
<br>
<br>

</body>
</html>