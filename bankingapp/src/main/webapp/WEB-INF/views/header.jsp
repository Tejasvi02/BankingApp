<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<nav class="navbar navbar-expand-lg navbar-light bg-light mb-3">
  <div class="container-fluid">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/home">BankApp</a>
    <div class="collapse navbar-collapse">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">

        <li class="nav-item">
          <a class="nav-link" href="${pageContext.request.contextPath}/users">Users</a>
        </li>

        <sec:authorize access="hasRole('ADMIN')">
          <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/roles">Roles</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/branches">Branches</a>
          </li>
        </sec:authorize>

        <sec:authorize access="!hasRole('ADMIN')">
          <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/branches">Branch Listing</a>
          </li>
        </sec:authorize>

        <!-- NEW: Customers link (for ADMIN or MANAGER) -->
        <sec:authorize access="hasAnyRole('ADMIN','MANAGER')">
          <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/customers">Customers</a>
          </li>
        </sec:authorize>

      </ul>

      <ul class="navbar-nav">
        <li class="nav-item">
          <form action="${pageContext.request.contextPath}/logout" method="post" class="d-inline">
            <button class="btn btn-outline-secondary btn-sm" type="submit">Logout</button>
          </form>
        </li>
      </ul>
    </div>
  </div>
</nav>
