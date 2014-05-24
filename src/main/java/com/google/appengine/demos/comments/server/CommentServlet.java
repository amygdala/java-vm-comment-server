package com.google.appengine.demos.comments.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.FetchOptions;

// import com.google.appengine.api.users.User;
// import com.google.appengine.api.users.UserService;
// import com.google.appengine.api.users.UserServiceFactory;

import com.google.appengine.demos.comments.messages.Message;
import com.google.appengine.demos.comments.messages.CommentMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CommentServlet extends HttpServlet {

  private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
  private static final Logger LOG = Logger.getLogger(CommentServlet.class.getName());
  private static final int LIMIT = 20;


  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
              throws IOException {

    String userID = req.getParameter("userID");
    String commenterName = req.getParameter("commenterName");
    if (commenterName.equals("")) {
      commenterName = "Anonymous";
    }
    Key userCommentsKey = KeyFactory.createKey("Comment", userID);
    String content = req.getParameter("content");
    Date date = new Date();
    Entity comment = new Entity("Comment", userCommentsKey);
    comment.setProperty("commenter_name", commenterName);
    comment.setProperty("date", date);
    comment.setProperty("content", content);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(comment);
    Message msg = new Message("create_comment", "OK", "OK");
    resp.setContentType("application/json");
    resp.getWriter().println(msg.toJson());
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
              throws IOException {

    String pathInfo = req.getPathInfo();
    // TODO: error checking
    String[] pathParts = pathInfo.split("/");
    String userID = pathParts[1];
    String which = pathParts[2];
    LOG.info("got user id: " + userID + ", which " + which);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Message msg;

    if (which.equalsIgnoreCase("all")) {  // return all comments for that user id.
      try {
        Key userIDKey = KeyFactory.createKey("Comment", userID);
        Query query = new Query("Comment", userIDKey).addSort("date", Query.SortDirection.DESCENDING);
        List<Entity> comments = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(LIMIT));
        List<CommentMessage> commentlist = new ArrayList<CommentMessage>();
        CommentMessage comment;
        for (Entity c : comments) {
          comment = CommentMessage.fromEntity(userID, c);
          commentlist.add(comment);
        }
        String commentsJson = GSON.toJson(commentlist);
        LOG.info("Comments: " + commentsJson);
        msg = new Message("list_comments", "OK", commentsJson);
      }
      catch (Exception e) {
        msg = new Message("list_comments", "ERROR", e.getMessage());
      }
      resp.setContentType("application/json");
      resp.getWriter().println(msg.toJson());
    }
    else if (! which.trim().equalsIgnoreCase("")) {  // treat as indiv. ID
      try {
        long cid = Long.parseLong(which);
        LOG.info("cid is: " + cid);
        Key k = new KeyFactory.Builder("Comment", userID)
                              .addChild("Comment", cid)
                              .getKey();
        LOG.info("key is: " + k);
        Entity c = datastore.get(k);
        CommentMessage comment = CommentMessage.fromEntity(userID, c);
        String commentsJson = GSON.toJson(comment);
        LOG.info("Comment: " + commentsJson);
        msg = new Message("comment", "OK", commentsJson);
      }
      catch (EntityNotFoundException e) {
        msg = new Message("comment", "ERROR", "Entity not found.");
      }
      catch (Exception e2) {
        msg = new Message("comment", "ERROR", e2.getMessage());
      }
      resp.setContentType("application/json");
      resp.getWriter().println(msg.toJson());
    }
  }
}
