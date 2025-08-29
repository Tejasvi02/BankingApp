<!-- /WEB-INF/views/user-form.jsp -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
  <title>Users</title>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="container py-3">
  <jsp:include page="/WEB-INF/views/header.jsp"/>

  <c:choose>
    <c:when test="${isAdmin}">
      <%-- ADMIN VIEW --%>
      <div class="row g-4">
        <div class="col-md-4">
          <div class="card">
            <div class="card-header">Add / Edit User</div>
            <div class="card-body">
              <form:form method="post" action="${pageContext.request.contextPath}/users/save"
                         modelAttribute="user" class="row g-3">
                <form:hidden path="userId"/>

                <div class="col-12">
                  <label class="form-label">Username</label>
                  <form:input path="username" class="form-control" required="true"/>
                </div>

                <div class="col-12">
                  <label class="form-label">Password (leave blank to keep)</label>
                  <form:password path="password" class="form-control"/>
                </div>

                <div class="col-12">
                  <label class="form-label">Email</label>
                  <form:input path="email" class="form-control" required="true"/>
                </div>

                <div class="col-12">
                  <label class="form-label d-block">Roles</label>
                  <c:forEach var="r" items="${allRoles}">
                    <div class="form-check form-check-inline">
                      <input class="form-check-input" type="checkbox" name="roleIds" value="${r.roleId}"
                             <c:if test="${user.roles.contains(r)}">checked</c:if> />
                      <label class="form-check-label">${r.roleName}</label>
                    </div>
                  </c:forEach>
                </div>

                <!-- If CSRF is enabled, include token:
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                -->

                <div class="col-12">
                  <button class="btn btn-primary w-100">Save</button>
                </div>
              </form:form>
            </div>
          </div>
        </div>

        <div class="col-md-8">
          <table class="table table-striped">
            <thead>
              <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Roles</th>
                <th style="width: 150px;">Actions</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="u" items="${users}">
                <tr>
                  <td>${u.userId}</td>
                  <td>${u.username}</td>
                  <td>${u.email}</td>
                  <td>
                    <c:forEach var="r" items="${u.roles}">
                      <span class="badge text-bg-secondary me-1">${r.roleName}</span>
                    </c:forEach>
                  </td>
                  <td>
                    <a class="btn btn-sm btn-warning"
                       href="${pageContext.request.contextPath}/users/edit/${u.userId}">Edit</a>
                    <a class="btn btn-sm btn-danger"
                       href="${pageContext.request.contextPath}/users/delete/${u.userId}"
                       onclick="return confirm('Delete user?');">Delete</a>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </c:when>

    <c:otherwise>
      <%-- NON-ADMIN VIEW --%>
      <div class="card">
        <div class="card-header">My Profile</div>
        <div class="card-body">
          <form:form method="post" action="${pageContext.request.contextPath}/users/save"
                     modelAttribute="user" class="row g-3">
            <form:hidden path="userId"/>

            <div class="col-12">
              <label class="form-label">Username</label>
              <form:input path="username" class="form-control" readonly="true"/>
            </div>

            <div class="col-12">
              <label class="form-label">Password (leave blank to keep)</label>
              <form:password path="password" class="form-control"/>
            </div>

            <div class="col-12">
              <label class="form-label">Email</label>
              <form:input path="email" class="form-control" required="true"/>
            </div>

            <!-- If CSRF is enabled:
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            -->

            <div class="col-12">
              <button class="btn btn-primary">Save</button>
            </div>
          </form:form>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
</body>
</html>
