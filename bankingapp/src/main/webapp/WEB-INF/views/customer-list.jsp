<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<html>
<head>
  <title>Customers</title>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="container py-4">
  <jsp:include page="/WEB-INF/views/header.jsp"/>
  <c:set var="cxt" value="${pageContext.request.contextPath}"/>

  <sec:authorize access="hasAnyRole('ADMIN','MANAGER')">
    <h2 class="mb-3 text-primary">Customers</h2>

    <!-- ===== Top: Form ===== -->
    <div class="card shadow-sm mb-4">
      <div class="card-header">
        <strong>${customer.customerId == null ? "Add Customer" : "Edit Customer"}</strong>
      </div>
      <div class="card-body">
        <form:form action="${cxt}/customers" method="post" modelAttribute="customer" class="row g-3">
          <form:hidden path="customerId"/>

          <c:if test="${submitted}">
            <form:errors path="*" element="div" cssClass="alert alert-danger"/>
          </c:if>

          <div class="col-12 col-md-6">
            <label class="form-label">Customer Name</label>
            <form:input path="customerName" cssClass="form-control"/>
            <c:if test="${submitted}"><form:errors path="customerName" cssClass="text-danger small"/></c:if>
          </div>

          <div class="col-12 col-md-3">
            <label class="form-label">Gender</label>
            <form:select path="customerGender" cssClass="form-select">
              <form:option value="" label="-- Select --"/>
              <form:options items="${genders}"/>
            </form:select>
            <c:if test="${submitted}"><form:errors path="customerGender" cssClass="text-danger small"/></c:if>
          </div>

          <div class="col-12 col-md-3">
            <label class="form-label">Date of Birth</label>
            <form:input path="customerDOB" type="date" cssClass="form-control"/>
            <c:if test="${submitted}"><form:errors path="customerDOB" cssClass="text-danger small"/></c:if>
          </div>

          <div class="col-12"><h6 class="text-secondary mt-2 mb-1">Address</h6></div>

          <div class="col-12 col-md-6">
            <label class="form-label">Address Line 1</label>
            <form:input path="customerAddress.addressLine1" cssClass="form-control"/>
            <c:if test="${submitted}"><form:errors path="customerAddress.addressLine1" cssClass="text-danger small"/></c:if>
          </div>
          <div class="col-12 col-md-6">
            <label class="form-label">Address Line 2</label>
            <form:input path="customerAddress.addressLine2" cssClass="form-control"/>
          </div>

          <div class="col-12 col-md-3">
            <label class="form-label">City</label>
            <form:input path="customerAddress.city" cssClass="form-control"/>
            <c:if test="${submitted}"><form:errors path="customerAddress.city" cssClass="text-danger small"/></c:if>
          </div>
          <div class="col-12 col-md-3">
            <label class="form-label">State</label>
            <form:input path="customerAddress.state" cssClass="form-control"/>
            <c:if test="${submitted}"><form:errors path="customerAddress.state" cssClass="text-danger small"/></c:if>
          </div>
          <div class="col-12 col-md-3">
            <label class="form-label">Country</label>
            <form:input path="customerAddress.country" cssClass="form-control"/>
            <c:if test="${submitted}"><form:errors path="customerAddress.country" cssClass="text-danger small"/></c:if>
          </div>
          <div class="col-12 col-md-3">
            <label class="form-label">Zip</label>
            <form:input path="customerAddress.zip" cssClass="form-control"/>
            <c:if test="${submitted}"><form:errors path="customerAddress.zip" cssClass="text-danger small"/></c:if>
          </div>

          <div class="col-12 col-md-4">
            <label class="form-label">SSN</label>
            <form:input path="customerSSN" cssClass="form-control" placeholder="123-45-6789 or 123456789"/>
            <c:if test="${submitted}"><form:errors path="customerSSN" cssClass="text-danger small"/></c:if>
          </div>

          <div class="col-12 col-md-8">
            <label class="form-label">User (one user → one customer)</label>
            <form:select path="user.userId" cssClass="form-select">
              <form:option value="0" label="-- Select user --"/>
              <c:forEach var="u" items="${availableUsers}">
                <option value="${u.userId}" <c:if test="${customer.user != null && customer.user.userId == u.userId}">selected</c:if>>
                  ${u.username} (${u.email})
                </option>
              </c:forEach>
            </form:select>
            <c:if test="${submitted}"><form:errors path="user.userId" cssClass="text-danger small"/></c:if>
          </div>

          <div class="col-12">
            <button type="submit" class="btn btn-primary">
              ${customer.customerId == null ? 'Add Customer' : 'Update Customer'}
            </button>
          </div>
        </form:form>
      </div>
    </div>

    <!-- ===== Bottom: List + Pagination ===== -->
    <div class="card shadow-sm">
      <div class="card-header d-flex flex-wrap gap-2 justify-content-between align-items-center">
        <div><strong>Customer List</strong> <span class="text-muted">(${customerCount})</span></div>
        <form class="d-inline-flex align-items-center gap-2" method="get" action="${cxt}/customers">
          <input type="hidden" name="page" value="${currentPage}" />
          <input type="hidden" name="sortField" value="${sortField}" />
          <input type="hidden" name="sortDir" value="${sortDir}" />
          <label class="form-label mb-0 me-2">Page size</label>
          <select class="form-select form-select-sm w-auto" name="size" onchange="this.form.submit()">
            <option ${size==5 ? 'selected' : ''}>5</option>
            <option ${size==10 ? 'selected' : ''}>10</option>
            <option ${size==20 ? 'selected' : ''}>20</option>
            <option ${size==50 ? 'selected' : ''}>50</option>
          </select>
        </form>
      </div>

      <div class="table-responsive">
        <table class="table table-striped mb-0">
          <thead class="table-light">
            <tr>
              <th><a href="${cxt}/customers?page=${currentPage}&size=${size}&sortField=customerId&sortDir=${reverseSortDir}" class="text-decoration-none">
                ID <c:if test="${sortField=='customerId'}"><small class="text-muted">(${sortDir})</small></c:if>
              </a></th>
              <th>Name</th>
              <th>Gender</th>
              <th>DOB</th>
              <th>User</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
          <c:forEach var="cst" items="${customerPage.content}">
            <tr>
              <td>${cst.customerId}</td>
              <td>${cst.customerName}</td>
              <td>${cst.customerGender}</td>
              <td>${cst.customerDOB}</td>
              <td><c:if test="${cst.user != null}">${cst.user.username}</c:if></td>
              <td>
                <a class="btn btn-sm btn-warning" href="${cxt}/customers/edit/${cst.customerId}">Edit</a>
                <a class="btn btn-sm btn-danger" href="${cxt}/customers/delete/${cst.customerId}"
                   onclick="return confirm('Delete this customer?');">Delete</a>
                <!-- Example link to create account for this customer -->
                <a class="btn btn-sm btn-outline-primary" href="${cxt}/accounts/new?customerId=${cst.customerId}">Create Account</a>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>

      <div class="card-footer">
        <nav aria-label="Page navigation">
          <ul class="pagination mb-0 justify-content-end">
            <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
			  <a class="page-link"
			     href="${cxt}/customers?page=${currentPage-1}&size=${size}&sortField=${sortField}&sortDir=${sortDir}">
			    Previous
			  </a>
			</li>	
			<c:if test="${totalPages > 0}">
			  <c:forEach var="i" begin="0" end="${totalPages - 1}">
			    <li class="page-item ${i == currentPage ? 'active' : ''}">
			      <a class="page-link"
			         href="${cxt}/customers?page=${i}&size=${size}&sortField=${sortField}&sortDir=${sortDir}">
			        ${i + 1}
			      </a>
			    </li>
			  </c:forEach>
			</c:if>
			<li class="page-item ${currentPage + 1 >= totalPages ? 'disabled' : ''}">
			  <a class="page-link"
			     href="${cxt}/customers?page=${currentPage+1}&size=${size}&sortField=${sortField}&sortDir=${sortDir}">
			    Next
			  </a>
			</li>
          </ul>
        </nav>
      </div>
    </div>
  </sec:authorize>
</body>
</html>
