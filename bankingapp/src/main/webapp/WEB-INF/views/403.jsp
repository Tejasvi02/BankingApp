<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Access Denied</title>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="container py-5">
  <div class="alert alert-danger">
    <h4 class="alert-heading">Access Denied (403)</h4>
    <p>You don't have permission to access this page.</p>
    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/home">Back to Home</a>
  </div>
</body>
</html>
