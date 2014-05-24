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

public class TrackServlet extends HttpServlet {

  private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
  private static final Logger LOG = Logger.getLogger(TrackServlet.class.getName());

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

    if (which.equalsIgnoreCase("all")) {  // return all tracks for that user id.
      // TODO - fetch the kratos-inserted info
      try {
        msg = new Message("list_tracks", "OK", "tracks info goes here");
      }
      catch (Exception e) {
        msg = new Message("list_comments", "ERROR", e.getMessage());
      }
      resp.setContentType("application/json");
      resp.getWriter().println(msg.toJson());
    }
    else if (! which.trim().equalsIgnoreCase("")) {  // treat as indiv. track ID
      // TODO - fetch the kratos-inserted info
      try {
        long tid = Long.parseLong(which);
        LOG.info("tid is: " + tid);
        msg = new Message("track", "OK", "track info goes here");
      }
      // catch (EntityNotFoundException e) {
      //   msg = new Message("comment", "ERROR", "Entity not found.");
      // }
      catch (Exception e2) {
        msg = new Message("comment", "ERROR", e2.getMessage());
      }
      resp.setContentType("application/json");
      resp.getWriter().println(msg.toJson());
    }
  }
}
