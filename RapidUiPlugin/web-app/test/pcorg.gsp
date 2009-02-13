<%
    def componentId = params.componentId;
%>
<rui:formRemote method="POST" componentId="${componentId}" action="success.xml">
    <table>
        <tbody>
            <tr>
                <td>Name:</td>
                <td><input type="text"/></td>
            </tr>
        </tbody>
    </table>
</rui:formRemote>
