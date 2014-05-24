/*
 * Copyright (c) 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.google.appengine.demos.comments.messages;

import com.google.appengine.api.datastore.Entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;
import java.util.logging.Logger;


/**
 *
 */
public class CommentMessage {

  private static final Logger LOG = Logger.getLogger(CommentMessage.class.getName());

  private String userID;
  private String commenterName;
  private Date date;
  private String content;
  private long commentID;

  private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

  public CommentMessage(String userID, long commentID, String commenterName, Date date, String content) {
    this.userID = userID;
    this.commenterName = commenterName;
    this.date = date;
    this.content = content;
    this.commentID = commentID;
  }

  public static CommentMessage fromEntity(String userID, Entity c) {
    if (c == null) {
      return null;
    }
    else {
      long kid = c.getKey().getId();
      LOG.info("got key id " + kid);
      CommentMessage comment = new CommentMessage(userID, kid, (String)c.getProperty("commenter_name"),
        (Date)c.getProperty("date"), (String)c.getProperty("content"));
      return comment;
    }
  }

  // do I need these?

  public String getUserID() {
    return userID;
  }

  public String getContent() {
    return content;
  }

  public String getCommenterName() {
    return commenterName;
  }

  public String toJson() {
    return GSON.toJson(this);
  }

}
