/*
 * Copyright (c) 2011 Google Inc.
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

package com.google.api.client.http;

import com.google.common.base.Charsets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Abstract implementation of an HTTP content with typical options.
 *
 * <p>
 * Implementation is not thread-safe.
 * </p>
 *
 * @since 1.5
 * @author Yaniv Inbar
 */
public abstract class AbstractHttpContent implements HttpContent {

  /** Media type used for the Content-Type header or {@code null} for none. */
  private HttpMediaType mediaType;

  /** Cached value for the computed length from {@link #computeLength()}. */
  private long computedLength = -1;

  /**
   * @deprecated (scheduled to be removed in 1.11) Use {@link #AbstractHttpContent(String)} instead.
   */
  @Deprecated
  protected AbstractHttpContent() {}

  /**
   * @param mediaType Media type string (for example "type/subtype") this content represents or
   *        {@code null} to leave out. Can also contain parameters like {@code "charset=utf-8"}
   * @since 1.10
   */
  protected AbstractHttpContent(String mediaType) {
    this(mediaType == null ? null : new HttpMediaType(mediaType));
  }

  /**
   * @param mediaType Media type this content represents or {@code null} to leave out
   * @since 1.10
   */
  protected AbstractHttpContent(HttpMediaType mediaType) {
    this.mediaType = mediaType;
  }

  /** Default implementation returns {@code null}, but subclasses may override. */
  public String getEncoding() {
    return null;
  }

  /**
   * Default implementation calls {@link #computeLength()} once and caches it for future
   * invocations, but subclasses may override.
   */
  public long getLength() throws IOException {
    if (computedLength == -1) {
      computedLength = computeLength();
    }
    return computedLength;
  }

  /**
   * Returns the media type to use for the Content-Type header, or {@code null} if unspecified.
   *
   * @since 1.10
   */
  public final HttpMediaType getMediaType() {
    return mediaType;
  }

  /**
   * Sets the media type to use for the Content-Type header, or {@code null} if unspecified.
   *
   * <p>
   * This will also overwrite any previously set parameter of the media type (for example
   * {@code "charset"}), and therefore might change other properties as well.
   * </p>
   *
   * @since 1.10
   */
  public AbstractHttpContent setMediaType(HttpMediaType mediaType) {
    this.mediaType = mediaType;
    return this;
  }

  /**
   * Returns the charset specified in the media type or {@code Charsets#UTF_8} if not specified.
   *
   * @since 1.10
   */
  protected final Charset getCharset() {
    return mediaType == null || mediaType.getCharsetParameter() == null ? Charsets.UTF_8 : mediaType
        .getCharsetParameter();
  }

  public String getType() {
    return mediaType == null ? null : mediaType.build();
  }

  /**
   * Computes and returns the content length or less than zero if not known.
   *
   * <p>
   * Subclasses may override, but by default this computes the length by calling
   * {@link #writeTo(OutputStream)} with an output stream that does not process the bytes written,
   * but only retains the count of bytes. If {@link #retrySupported()} is {@code false}, it will
   * instead return {@code -1}.
   * </p>
   */
  protected long computeLength() throws IOException {
    if (!retrySupported()) {
      return -1;
    }
    ByteCountingOutputStream countingStream = new ByteCountingOutputStream();
    try {
      writeTo(countingStream);
    } finally {
      countingStream.close();
    }
    return countingStream.count;
  }

  /** Default implementation returns {@code true}, but subclasses may override. */
  public boolean retrySupported() {
    return true;
  }
}
