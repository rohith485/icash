<%@taglib uri = "http://www.springframework.org/tags/form" prefix = "form"%>
<html>
<body>
<h1>Icash Flow</h1>

<form:form method="POST" action="${pageContext.request.contextPath}/updatebuyerdata" modelAttribute="buyerIcashCommand" enctype="multipart/form-data">
     <table>
            <tr>
               <td><form:label path = "mroi">Minimum ROI</form:label></td>
               <td><form:input path = "mroi" /></td>
            </tr>
            <tr>
               <td><form:label path = "droi">Desired ROI</form:label></td>
               <td><form:input path = "droi" /></td>
            </tr>
            <tr>
               <td><form:label path = "file">File upload</form:label></td>
               <td><input type="file" name="file" /></td>
            </tr>
            <tr>
               <td colspan = "2">
                  <input type="submit" value="Submit" />
               </td>
            </tr>
         </table>  
</form:form>

</body>
</html>