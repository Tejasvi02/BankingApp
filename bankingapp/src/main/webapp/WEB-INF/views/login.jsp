<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
  <title>Login</title>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="container py-5">
  <div class="row justify-content-center">
    <div class="col-md-4">
      <div class="card shadow-sm">
        <div class="card-header">Login</div>
        <div class="card-body">
          <!-- IMPORTANT: form action matches loginProcessingUrl -->
          <form method="post" action="${pageContext.request.contextPath}/auth/login">
            <div class="mb-3">
              <label class="form-label">Username</label>
              <input name="username" class="form-control" required />
            </div>
            <div class="mb-3">
              <label class="form-label">Password</label>
              <input type="password" name="password" class="form-control" required />
            </div>
            <button class="btn btn-primary w-100">Login</button>
          </form>
        </div>
      </div>

      <c:if test="${param.error != null}">
        <div class="alert alert-danger mt-3">Invalid credentials.</div>
      </c:if>
      <c:if test="${param.logout != null}">
        <div class="alert alert-info mt-3">You have been logged out.</div>
      </c:if>
    </div>
  </div>
</body>
</html>
