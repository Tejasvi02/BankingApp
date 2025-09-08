<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Branch Management</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="container py-4">
<jsp:include page="/WEB-INF/views/header.jsp"/>
<c:set var="cxt" value="${pageContext.request.contextPath}" />

<h2 class="mb-4 text-primary">Branches</h2>

<!-- ===== Top: Form Card (full width) ===== -->
<sec:authorize access="hasRole('ADMIN')">
<div class="card shadow-sm mb-4">
  <div class="card-header">
    <strong>${branch.branchId == null ? "Add New Branch" : "Edit Branch"}</strong>
  </div>
  <div class="card-body">
    <form:form action="${cxt}/branches" method="post" modelAttribute="branch" class="row g-3">
      <form:hidden path="branchId"/>

      <div class="col-12 col-md-6">
        <label class="form-label">Branch Name</label>
        <form:input path="branchName" class="form-control" required="true"/>
      </div>

      <div class="col-12"><h6 class="text-secondary mt-2 mb-1">Address</h6></div>

      <div class="col-12 col-md-6">
        <label class="form-label">Address Line 1</label>
        <form:input path="branchAddress.addressLine1" class="form-control" required="true"/>
      </div>
      <div class="col-12 col-md-6">
        <label class="form-label">Address Line 2</label>
        <form:input path="branchAddress.addressLine2" class="form-control"/>
      </div>

      <div class="col-12 col-md-4">
        <label class="form-label">City</label>
        <form:input path="branchAddress.city" class="form-control" required="true"/>
      </div>
      <div class="col-12 col-md-4">
        <label class="form-label">State</label>
        <form:input path="branchAddress.state" class="form-control" required="true"/>
      </div>
      <div class="col-12 col-md-4">
        <label class="form-label">Country</label>
        <form:input path="branchAddress.country" class="form-control" required="true"/>
      </div>

      <div class="col-12 col-md-3">
        <label class="form-label">Zip</label>
        <form:input path="branchAddress.zip" class="form-control" required="true"/>
      </div>

      <div class="col-12">
        <button type="submit" class="btn btn-primary">
          ${branch.branchId == null ? 'Add Branch' : 'Update Branch'}
        </button>
      </div>
    </form:form>
  </div>
</div>
</sec:authorize>

<!-- ===== Bottom: List + Pagination ===== -->
<div class="card shadow-sm">
  <div class="card-header d-flex flex-wrap gap-2 justify-content-between align-items-center">
    <strong>Branches</strong>
	   <form class="d-inline-flex align-items-center gap-2" method="get" action="${cxt}/branches">
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
          <th>
            <a href="${cxt}/branches?page=${currentPage}&size=${size}&sortField=branchId&sortDir=${reverseSortDir}" class="text-decoration-none">
              ID <c:if test="${sortField=='branchId'}"><small class="text-muted">(${sortDir})</small></c:if>
            </a>
          </th>
          <th>
            <a href="${cxt}/branches?page=${currentPage}&size=${size}&sortField=branchName&sortDir=${reverseSortDir}" class="text-decoration-none">
              Branch Name <c:if test="${sortField=='branchName'}"><small class="text-muted">(${sortDir})</small></c:if>
            </a>
          </th>
          <th>Address</th>
          <sec:authorize access="hasRole('ADMIN')">
      		<th>Actions</th>
    	  </sec:authorize>
        </tr>
      </thead>
      <tbody>
      <c:forEach var="b" items="${branchPage.content}">
        <tr>
          <td>${b.branchId}</td>
          <td>${b.branchName}</td>
          <td>
            <c:if test="${b.branchAddress != null}">
              ${b.branchAddress.addressLine1}
              <c:if test="${not empty b.branchAddress.addressLine2}">, ${b.branchAddress.addressLine2}</c:if>,
              ${b.branchAddress.city}, ${b.branchAddress.state},
              ${b.branchAddress.country} - ${b.branchAddress.zip}
            </c:if>
          </td> 
		  <sec:authorize access="hasRole('ADMIN')">
		    <td>
		      <a class="btn btn-sm btn-warning"
		         href="${pageContext.request.contextPath}/branches/edit/${branch.branchId}">Edit</a>
		      <a class="btn btn-sm btn-danger"
		         href="${pageContext.request.contextPath}/branches/delete/${branch.branchId}"
		         onclick="return confirm('Delete this branch?');">Delete</a>
		    </td>
		  </sec:authorize>
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
             href="${cxt}/branches?page=${currentPage-1}&size=${size}&sortField=${sortField}&sortDir=${sortDir}">Previous</a>
        </li>

        <c:forEach var="i" begin="0" end="${totalPages-1}">
          <li class="page-item ${i == currentPage ? 'active' : ''}">
            <a class="page-link"
               href="${cxt}/branches?page=${i}&size=${size}&sortField=${sortField}&sortDir=${sortDir}">
              ${i + 1}
            </a>
          </li>
        </c:forEach>

        <li class="page-item ${currentPage + 1 >= totalPages ? 'disabled' : ''}">
          <a class="page-link"
             href="${cxt}/branches?page=${currentPage+1}&size=${size}&sortField=${sortField}&sortDir=${sortDir}">Next</a>
        </li>
      </ul>
    </nav>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
