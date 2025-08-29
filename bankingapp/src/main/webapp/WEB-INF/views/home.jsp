<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
  <title>Home</title>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="container py-3">
  <jsp:include page="/WEB-INF/views/header.jsp"/>
  <h3>Welcome to Banking App</h3>
  <sec:authorize access="hasRole('ADMIN')">
    <div class="alert alert-warning mt-3">You are logged in as ADMIN.</div>
  </sec:authorize>
</body>
</html>