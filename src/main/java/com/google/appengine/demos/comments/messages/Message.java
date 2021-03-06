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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 */
public class Message {

  public enum MessageType {
    CREATE_COMMENT,
    LIST_COMMENTS,
    COMMENT
  }

  public enum MessageStatus {
    OK,
    ERROR
  }

  private MessageType type;
  private MessageStatus status;
  private String message;

  private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

  public Message(MessageType type, MessageStatus status, String message) {
    this.type = type;
    this.status = status;
    this.message = message;
  }

  public MessageType getType() {
    return type;
  }

  public MessageStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public String toJson() {
    return GSON.toJson(this);
  }

}
