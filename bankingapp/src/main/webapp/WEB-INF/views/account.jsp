<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form"  uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec"   uri="http://www.springframework.org/security/tags" %>
<c:set var="cxt" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Accounts</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
  <jsp:include page="/WEB-INF/views/header.jsp" />

  <div class="container my-4">

    <c:if test="${not empty success}">
      <div class="alert alert-success alert-dismissible fade show" role="alert">
        ${success}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
    </c:if>

    <!-- Open New Account (Admin/Manager only) -->
    <sec:authorize access="hasAnyRole('ADMIN','MANAGER')">
      <div class="card mb-4 shadow-sm">
        <div class="card-header fw-semibold">Open New Account</div>
        <div class="card-body">
          <form:form modelAttribute="account" method="post" action="${cxt}/accounts" cssClass="row g-3">

            <!-- Customer (Account Holder) -->
            <div class="col-md-4">
              <label class="form-label">Account Holder (Customer)</label>
              <form:select path="accountCustomer.customerId" cssClass="form-select">
                <form:option value="" label="-- Select Customer --"/>
                <form:options items="${customers}" itemValue="customerId" itemLabel="customerName"/>
              </form:select>
              <form:errors path="accountCustomer.customerId" cssClass="text-danger small"/>
            </div>

            <!-- Account Type -->
            <div class="col-md-4">
              <label class="form-label">Account Type</label>
              <form:select path="accountType" cssClass="form-select">
                <form:option value="" label="-- Select Type --"/>
                <form:options items="${accountTypes}" />
              </form:select>
              <form:errors path="accountType" cssClass="text-danger small"/>
            </div>

            <!-- Branch -->
            <div class="col-md-4">
              <label class="form-label">Branch</label>
              <form:select path="accountBranch.branchId" cssClass="form-select">
                <form:option value="" label="-- Select Branch --"/>
                <form:options items="${branches}" itemValue="branchId" itemLabel="branchName"/>
              </form:select>
              <form:errors path="accountBranch.branchId" cssClass="text-danger small"/>
            </div>

            <!-- Date Opened -->
            <div class="col-md-4">
              <label class="form-label">Date Opened</label>
              <form:input path="accountDateOpened" type="date" cssClass="form-control"/>
              <form:errors path="accountDateOpened" cssClass="text-danger small"/>
            </div>

            <!-- Opening Balance -->
            <div class="col-md-4">
              <label class="form-label">Opening Balance</label>
              <form:input path="accountBalance" type="number" step="0.01" cssClass="form-control"/>
              <form:errors path="accountBalance" cssClass="text-danger small"/>
            </div>

            <div class="col-12">
              <button class="btn btn-primary" type="submit">Create</button>
            </div>
          </form:form>
        </div>
      </div>
    </sec:authorize>

    <!-- Accounts List -->
    <div class="card shadow-sm">
      <div class="card-header d-flex align-items-center">
        <span class="fw-semibold">Accounts</span>
        <small class="text-muted ms-2">
          <sec:authorize access="hasAnyRole('ADMIN','MANAGER')">(all)</sec:authorize>
          <sec:authorize access="!hasAnyRole('ADMIN','MANAGER')">(yours)</sec:authorize>
        </small>
      </div>
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-striped table-hover mb-0">
            <thead class="table-light">
              <tr>
                <th>ID</th>
                <th>Holder</th>
                <th>Type</th>
                <th>Customer</th>
                <th>Branch</th>
                <th>Opened</th>
                <th>Balance</th>
                <th class="text-end">Actions</th>
              </tr>
            </thead>
            <tbody>
            <c:forEach items="${accounts}" var="a">
              <tr>
                <td>${a.accountId}</td>
                <td><c:out value="${a.accountHolder}"/></td>
                <td><c:out value="${a.accountType}"/></td>
                <td><c:out value="${a.accountCustomer != null ? a.accountCustomer.customerName : ''}"/></td>
                <td><c:out value="${a.accountBranch != null ? a.accountBranch.branchName : ''}"/></td>

                <!-- LocalDate: print safely without fmt:formatDate -->
                <td><c:out value="${a.accountDateOpened}"/></td>

                <td><fmt:formatNumber value="${a.accountBalance}" type="currency"/></td>
                <td class="text-end">
                  <form action="${cxt}/accounts/${a.accountId}/delete" method="post" class="d-inline">
                    <button class="btn btn-sm btn-outline-danger"
                            onclick="return confirm('Delete account #${a.accountId}?');">Delete</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty accounts}">
              <tr><td colspan="8" class="text-center py-4 text-muted">No accounts found.</td></tr>
            </c:if>
            </tbody>
          </table>
        </div>
      </div>
    </div>

  </div>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
