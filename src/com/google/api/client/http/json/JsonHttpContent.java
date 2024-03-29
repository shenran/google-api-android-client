/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client.http.json;

import com.google.api.client.http.AbstractHttpContent;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Serializes JSON HTTP content based on the data key/value mapping object for an item.
 *
 * <p>
 * Sample usage:
 * </p>
 *
 * <pre>
 * <code>
  static void setContent(HttpRequest request, Object data) {
    request.setContent(new JsonHttpContent(new JacksonFactory(), data));
  }
 * </code>
 * </pre>
 *
 * <p>
 * Implementation is not thread-safe.
 * </p>
 *
 * @since 1.0
 * @author Yaniv Inbar
 */
public class JsonHttpContent extends AbstractHttpContent {
  // TODO(yanivi): ability to annotate fields as only needed for POST?

  /** JSON key name/value data. */
  private final Object data;

  /** JSON factory. */
  private final JsonFactory jsonFactory;

  /**
   * @param jsonFactory JSON factory to use
   * @param data JSON key name/value data
   * @since 1.5
   */
  public JsonHttpContent(JsonFactory jsonFactory, Object data) {
    super(Json.MEDIA_TYPE);
    this.jsonFactory = Preconditions.checkNotNull(jsonFactory);
    this.data = Preconditions.checkNotNull(data);
  }

  public void writeTo(OutputStream out) throws IOException {
    JsonGenerator generator = jsonFactory.createJsonGenerator(out, getCharset());
    generator.serialize(data);
    generator.flush();
  }

  /**
   * Sets the content type or {@code null} for none.
   *
   * <p>
   * Defaults to {@link Json#CONTENT_TYPE}.
   * </p>
   *
   * @since 1.5
   * @deprecated (scheduled to be removed in 1.11) Use {@link #setMediaType(HttpMediaType)} instead.
   */
  @Deprecated
  public JsonHttpContent setType(String type) {
    setMediaType(new HttpMediaType(type));
    return this;
  }

  @Override
  public JsonHttpContent setMediaType(HttpMediaType mediaType) {
    super.setMediaType(mediaType);
    return this;
  }

  /**
   * Returns the JSON key name/value data.
   *
   * @since 1.5
   */
  public final Object getData() {
    return data;
  }

  /**
   * Returns the JSON factory.
   *
   * @since 1.5
   */
  public final JsonFactory getJsonFactory() {
    return jsonFactory;
  }
}
