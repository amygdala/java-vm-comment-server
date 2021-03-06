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

    Message msg;
    // String userID = req.getParameter("userID");
    String pathInfo = req.getPathInfo();
    if (pathInfo == null) {
      msg = new Message(Message.MessageType.CREATE_COMMENT, Message.MessageStatus.ERROR,
        "No user id provided.");
      resp.setContentType("application/json");
      resp.getWriter().println(msg.toJson());
      return;
    }
    try {
      String[] pathParts = pathInfo.split("/");
      String userID = pathParts[1];
      LOG.info("got user id: " + userID);
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
      // TODO - truncate at 500 chars
      comment.setProperty("content", content);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(comment);
      msg = new Message(Message.MessageType.CREATE_COMMENT, Message.MessageStatus.OK,
        "OK");
    }
    catch (Exception e) {
      msg = new Message(Message.MessageType.CREATE_COMMENT, Message.MessageStatus.ERROR,
        e.getMessage());
    }
    resp.setContentType("application/json");
    resp.getWriter().println(msg.toJson());
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
              throws IOException {

    Message msg;
    String pathInfo = req.getPathInfo();
    if (pathInfo == null) {
      msg = new Message(Message.MessageType.LIST_COMMENTS, Message.MessageStatus.ERROR,
        "Unsupported: no user id.");
      resp.setContentType("application/json");
      resp.getWriter().println(msg.toJson());
      return;
    }
    String[] pathParts = pathInfo.split("/");
    if (pathParts.length < 2) {
      msg = new Message(Message.MessageType.LIST_COMMENTS, Message.MessageStatus.ERROR,
        "Unsupported: no user id.");
      resp.setContentType("application/json");
      resp.getWriter().println(msg.toJson());
      return;
    }
    String userID = pathParts[1];
    String whichComments;
    if (pathParts.length == 2) {   // default to 'all'
      whichComments = "all";
    }
    else {
      whichComments = pathParts[2];
    }
    LOG.info("got user id: " + userID + ", whichComments " + whichComments);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    if (whichComments.equalsIgnoreCase("all")) {  // return all comments for that user id.
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
        msg = new Message(Message.MessageType.LIST_COMMENTS, Message.MessageStatus.OK,
          commentsJson);
      }
      catch (Exception e) {
        msg = new Message(Message.MessageType.LIST_COMMENTS, Message.MessageStatus.ERROR,
          e.getMessage());
      }
      resp.setContentType("application/json");
      resp.getWriter().println(msg.toJson());
    }
    else if (! whichComments.trim().equalsIgnoreCase("")) {  // treat as indiv. ID request
      try {
        long cid = Long.parseLong(whichComments);
        LOG.info("cid is: " + cid);
        Key k = new KeyFactory.Builder("Comment", userID)
                              .addChild("Comment", cid)
                              .getKey();
        LOG.info("key is: " + k);
        Entity c = datastore.get(k);
        CommentMessage comment = CommentMessage.fromEntity(userID, c);
        String commentsJson = GSON.toJson(comment);
        LOG.info("Comment: " + commentsJson);
        msg = new Message(Message.MessageType.COMMENT, Message.MessageStatus.OK,
          commentsJson);
      }
      catch (EntityNotFoundException e) {
        msg = new Message(Message.MessageType.COMMENT, Message.MessageStatus.ERROR,
          "Entity not found.");
      }
      catch (Exception e2) {
        msg = new Message(Message.MessageType.COMMENT, Message.MessageStatus.ERROR,
          e2.getMessage());
      }
      resp.setContentType("application/json");
      resp.getWriter().println(msg.toJson());
    }
  }
}
