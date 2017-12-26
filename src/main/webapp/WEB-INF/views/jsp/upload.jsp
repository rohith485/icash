<%@taglib uri = "http://www.springframework.org/tags/form" prefix = "form"%>
<html>
<body>
<h1>Icash Flow</h1>

<form:form method="POST" action="${pageContext.request.contextPath}/updatebuyerdata" autocomplete="false"
	modelAttribute="buyerIcashCommand" enctype="multipart/form-data">
     <table>
            <tr>
               <td><form:label path = "mroi">Minimum ROI(%)</form:label></td>
               <td><form:input path = "mroi" /></td>
            </tr>
            <tr>
               <td><form:label path = "droi">Desired ROI(%)</form:label></td>
               <td><form:input path = "droi" /></td>
            </tr>
            <tr>
               <td><form:label path = "minReserveAmount">Minimum Reserve Amount $</form:label></td>
               <td><form:input path = "minReserveAmount" /></td>
            </tr>
            <tr>
               <td><form:label path = "maxReserveAmount">Maximum Reserve Amount $</form:label></td>
               <td><form:input path = "maxReserveAmount" /></td>
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



<form:form method="POST" action="${pageContext.request.contextPath}/updatesellerdata" autocomplete="false"	modelAttribute="sellerIcashCommand">
     <table>
            <tr>
               <td><form:label path = "sellerId">Seller Id</form:label></td>
               <td><form:input path = "sellerId" /></td>
            </tr>
            <tr>
               <td><form:label path = "minDiscount">Minimum Discount(%)</form:label></td>
               <td><form:input path = "minDiscount" /></td>
            </tr>
            <tr>
               <td><form:label path = "maxDiscount">Maximum Discount(%)</form:label></td>
               <td><form:input path = "maxDiscount" /></td>
            </tr>
            <tr>
               <td><form:label path = "desiredDateOfPayment">Desired Date of Payment</form:label></td>
               <td><form:input path = "desiredDateOfPayment" /></td>
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