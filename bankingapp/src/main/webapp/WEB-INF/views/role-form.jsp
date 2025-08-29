<!-- /WEB-INF/views/role-form.jsp -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
  <title>Roles</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="container py-3">
  <jsp:include page="/WEB-INF/views/header.jsp"/>
  <div class="row g-4">
    <div class="col-md-4">
      <div class="card">
        <div class="card-header">Role</div>
        <div class="card-body">
          <form:form method="post" action="${pageContext.request.contextPath}/roles/save" modelAttribute="role">
            <form:hidden path="roleId"/>
            <div class="mb-3">
              <label class="form-label">Role Name</label>
              <form:input path="roleName" class="form-control" required="true"/>
            </div>
            <button class="btn btn-primary w-100">Save</button>
          </form:form>
        </div>
      </div>
    </div>
    <div class="col-md-8">
      <table class="table table-striped">
        <thead><tr><th>ID</th><th>Name</th><th>Actions</th></tr></thead>
        <tbody>
          <c:forEach var="r" items="${roles}">
            <tr>
              <td>${r.roleId}</td>
              <td>${r.roleName}</td>
              <td>
                <a class="btn btn-sm btn-warning" href="${pageContext.request.contextPath}/roles/edit/${r.roleId}">Edit</a>
                <a class="btn btn-sm btn-danger" href="${pageContext.request.contextPath}/roles/delete/${r.roleId}"
                   onclick="return confirm('Delete role?');">Delete</a>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</body>
</html>
