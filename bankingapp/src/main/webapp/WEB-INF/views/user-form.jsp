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
  <c:set var="cxt" value="${pageContext.request.contextPath}"/>

  <c:choose>
    <c:when test="${isAdmin}">
      <%-- ===================== ADMIN VIEW ===================== --%>
      <div class="row g-4">
        <div class="col-md-4">
          <div class="card shadow-sm">
            <div class="card-header">Add / Edit User</div>
            <div class="card-body">
              <form:form method="post" action="${cxt}/users/save"
                         modelAttribute="user" class="row g-3" id="userFormAdmin">
                <form:hidden path="userId"/>

                <%-- Global summary only after submit --%>
                <c:if test="${submitted}">
                  <form:errors path="*" element="div" cssClass="alert alert-danger" id="userFormSummary"/>
                </c:if>

                <div class="col-12">
                  <label class="form-label">Username</label>
                  <form:input path="username" cssClass="form-control uf-field"/>
                  <c:if test="${submitted}">
                    <form:errors path="username" cssClass="text-danger small uf-error"/>
                  </c:if>
                </div>

                <div class="col-12">
                  <label class="form-label">Password (leave blank to keep)</label>
                  <form:password path="password" cssClass="form-control uf-field"/>
                  <c:if test="${submitted}">
                    <form:errors path="password" cssClass="text-danger small uf-error"/>
                  </c:if>
                </div>

                <div class="col-12">
                  <label class="form-label">Email</label>
                  <form:input path="email" cssClass="form-control uf-field"/>
                  <c:if test="${submitted}">
                    <form:errors path="email" cssClass="text-danger small uf-error"/>
                  </c:if>
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
                  <c:if test="${submitted}">
                    <form:errors path="roles" cssClass="text-danger small d-block uf-error"/>
                  </c:if>
                </div>

                <div class="col-12">
                  <button class="btn btn-primary w-100">Save</button>
                </div>
              </form:form>
            </div>
          </div>
        </div>

        <div class="col-md-8">
          <div class="card shadow-sm">
            <div class="card-header">All Users</div>
            <div class="card-body p-0">
              <table class="table table-striped mb-0">
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
                           href="${cxt}/users/edit/${u.userId}">Edit</a>
                        <a class="btn btn-sm btn-danger"
                           href="${cxt}/users/delete/${u.userId}"
                           onclick="return confirm('Delete user?');">Delete</a>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </c:when>

    <c:otherwise>
      <%-- ===================== NON-ADMIN VIEW (My Profile) ===================== --%>
      <div class="card shadow-sm">
        <div class="card-header">My Profile</div>
        <div class="card-body">
          <form:form method="post" action="${cxt}/users/save"
                     modelAttribute="user" class="row g-3" id="userFormSelf">
            <form:hidden path="userId"/>

            <%-- Summary only after submit --%>
            <c:if test="${submitted}">
              <form:errors path="*" element="div" cssClass="alert alert-danger" id="userFormSummarySelf"/>
            </c:if>

            <div class="col-12">
              <label class="form-label">Username</label>
              <form:input path="username" cssClass="form-control" readonly="true"/>
              <c:if test="${submitted}">
                <form:errors path="username" cssClass="text-danger small"/>
              </c:if>
            </div>

            <div class="col-12">
              <label class="form-label">Email</label>
              <form:input path="email" cssClass="form-control" readonly="true"/>
              <c:if test="${submitted}">
                <form:errors path="email" cssClass="text-danger small"/>
              </c:if>
            </div>

            <div class="col-12">
              <label class="form-label">New Password (leave blank to keep)</label>
              <form:password path="password" cssClass="form-control uf-field"/>
              <c:if test="${submitted}">
                <form:errors path="password" cssClass="text-danger small uf-error"/>
              </c:if>
            </div>

            <div class="col-12">
              <button class="btn btn-primary">Save</button>
            </div>
          </form:form>
        </div>
      </div>
    </c:otherwise>
  </c:choose>

  <script>
    // Hide inline errors as soon as user types; hide summary if no inline errors remain
    (function () {
      const wireForm = (formId, fieldSelector, errorSelector, summaryId) => {
        const form = document.getElementById(formId);
        if (!form) return;
        const hideSummaryIfNoErrors = () => {
          const anyVisible = Array.from(form.querySelectorAll(errorSelector))
            .some(el => el.offsetParent !== null);
          const summary = document.getElementById(summaryId);
          if (summary && !anyVisible) summary.style.display = 'none';
        };
        form.querySelectorAll(fieldSelector).forEach(inp => {
          inp.addEventListener('input', () => {
            const err = inp.parentElement.querySelector(errorSelector);
            if (err) err.style.display = 'none';
            hideSummaryIfNoErrors();
          });
        });
      };
      // Admin form
      wireForm('userFormAdmin', '.uf-field', '.uf-error', 'userFormSummary');
      // Self profile form
      wireForm('userFormSelf', '.uf-field', '.uf-error', 'userFormSummarySelf');
    })();
  </script>
</body>
</html>
