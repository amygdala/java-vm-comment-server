<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
  <head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
  </head>

  <body>

<%
    String userID = request.getParameter("userID");
    if (userID == null) {
        userID = "userbob";
    }
    pageContext.setAttribute("userID", userID);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key userIDKey = KeyFactory.createKey("Comment", userID);
    // Run an ancestor query to ensure we see the most up-to-date
    // view of the Greetings belonging to the selected Guestbook.
    Query query = new Query("Comment", userIDKey).addSort("date", Query.SortDirection.DESCENDING);
    List<Entity> comments = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
    if (comments.isEmpty()) {
        %>
        <p>No comments for user '${fn:escapeXml(userID)}'.</p>
        <%
    } else {
        %>
        <p>Comments for user '${fn:escapeXml(userID)}'.</p>
        <%
        for (Entity comment : comments) {
            pageContext.setAttribute("comment_content",
                                     comment.getProperty("content"));
            if (comment.getProperty("commenter_name") == null) {
                %>
                <p>An anonymous person wrote:</p>
                <%
            } else {
                pageContext.setAttribute("commenter_name",
                                         comment.getProperty("commenter_name"));
                %>
                <p><b>${fn:escapeXml(commenter_name)}</b> wrote:</p>
                <%
            }
            %>
            <blockquote>${fn:escapeXml(comment_content)}</blockquote>
            <%
        }
    }
%>

    <form action="/comment/${fn:escapeXml(userID)}" method="post">
      <div><textarea name="content" rows="3" cols="60"></textarea></div>
      <div>Commenter name: <input type="text" name="commenterName" /></div>
      <div><input type="submit" value="Post A Comment" /></div>
      <!-- <input type="hidden" name="userID" value="${fn:escapeXml(userID)}"/> -->
    </form>

    <form action="/listcomments.jsp" method="get">
      <div><input type="text" name="userID" value="${fn:escapeXml(userID)}"/></div>
      <div><input type="submit" value="Switch User ID" /></div>
    </form>

  </body>
</html>
