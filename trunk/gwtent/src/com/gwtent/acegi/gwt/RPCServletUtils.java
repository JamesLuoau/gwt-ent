/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.gwtent.acegi.gwt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Utility class containing helper methods used by servlets that integrate with
 * the RPC system.
 */
public class RPCServletUtils {
  private static final String ACCEPT_ENCODING = "Accept-Encoding";

  private static final String CHARSET_UTF8 = "UTF-8";

  private static final String CONTENT_ENCODING = "Content-Encoding";

  private static final String CONTENT_ENCODING_GZIP = "gzip";

  private static final String CONTENT_TYPE_TEXT_PLAIN_UTF8 = "text/plain; charset=utf-8";

  private static final String GENERIC_FAILURE_MSG = "The call failed on the server; see server log for details";

  /**
   * Controls the compression threshold at and below which no compression will
   * take place.
   */
  private static final int UNCOMPRESSED_BYTE_SIZE_LIMIT = 256;

  /**
   * Returns <code>true</code> if the {@link HttpServletRequest} accepts Gzip
   * encoding. This is done by checking that the accept-encoding header
   * specifies gzip as a supported encoding.
   * 
   * @param request the request instance to test for gzip encoding acceptance
   * @return <code>true</code> if the {@link HttpServletRequest} accepts Gzip
   *         encoding
   */
  public static boolean acceptsGzipEncoding(HttpServletRequest request) {
    assert (request != null);

    String acceptEncoding = request.getHeader(ACCEPT_ENCODING);
    if (null == acceptEncoding) {
      return false;
    }

    return (acceptEncoding.indexOf(CONTENT_ENCODING_GZIP) != -1);
  }

  /**
   * Returns <code>true</code> if the response content's estimated UTF-8 byte
   * length exceeds 256 bytes.
   * 
   * @param content the contents of the response
   * @return <code>true</code> if the response content's estimated UTF-8 byte
   *         length exceeds 256 bytes
   */
  public static boolean exceedsUncompressedContentLengthLimit(String content) {
    return (content.length() * 2) > UNCOMPRESSED_BYTE_SIZE_LIMIT;
  }

  /**
   * Returns the content of an {@link HttpServletRequest} by decoding it using
   * the UTF-8 charset.
   * 
   * @param request the servlet request whose content we want to read
   * @return the content of an {@link HttpServletRequest} by decoding it using
   *         the UTF-8 charset
   * @throws IOException if the requests input stream cannot be accessed, read
   *           from or closed
   * @throws ServletException if the content length of the request is not
   *           specified of if the request's content type is not 'text/plain' or
   *           'charset=utf-8'
   */
  public static String readContentAsUtf8(HttpServletRequest request)
      throws IOException, ServletException {
    int contentLength = request.getContentLength();
    if (contentLength == -1) {
      // Content length must be known.
      throw new ServletException("Content-Length must be specified");
    }

    String contentType = request.getContentType();
    boolean contentTypeIsOkay = false;
    // Content-Type must be specified.
    if (contentType != null) {
      contentType = contentType.toLowerCase();
      // The type must be plain text.
      if (contentType.startsWith("text/plain")) {
        // And it must be UTF-8 encoded (or unspecified, in which case we assume
        // that it's either UTF-8 or ASCII).
        if (contentType.indexOf("charset=") == -1) {
          contentTypeIsOkay = true;
        } else if (contentType.indexOf("charset=utf-8") != -1) {
          contentTypeIsOkay = true;
        }
      }
    }

    if (!contentTypeIsOkay) {
      throw new ServletException(
          "Content-Type must be 'text/plain' with 'charset=utf-8' (or unspecified charset)");
    }

    InputStream in = request.getInputStream();
    try {
      byte[] payload = new byte[contentLength];
      int offset = 0;
      int len = contentLength;
      int byteCount;
      while (offset < contentLength) {
        byteCount = in.read(payload, offset, len);
        if (byteCount == -1) {
          throw new ServletException("Client did not send " + contentLength
              + " bytes as expected");
        }
        offset += byteCount;
        len -= byteCount;
      }
      return new String(payload, CHARSET_UTF8);
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }

  /**
   * Returns <code>true</code> if the request accepts gzip encoding and the
   * the response content's estimated UTF-8 byte length exceeds 256 bytes.
   * 
   * @param request the request associated with the response content
   * @param responseContent a string that will be
   * @return <code>true</code> if the request accepts gzip encoding and the
   *         the response content's estimated UTF-8 byte length exceeds 256
   *         bytes
   */
  public static boolean shouldGzipResponseContent(HttpServletRequest request,
      String responseContent) {
    return acceptsGzipEncoding(request)
        && exceedsUncompressedContentLengthLimit(responseContent);
  }

  /**
   * Write the response content into the {@link HttpServletResponse}. If
   * <code>gzipResponse</code> is <code>true</code>, the response content
   * will be gzipped prior to being written into the response.
   * 
   * @param servletContext servlet context for this response
   * @param response response instance
   * @param responseContent a string containing the response content
   * @param gzipResponse if <code>true</code> the response content will be
   *          gzip encoded before being written into the response
   * @throws IOException if reading, writing, or closing the response's output
   *           stream fails
   */
  public static void writeResponse(ServletContext servletContext,
      HttpServletResponse response, String responseContent, boolean gzipResponse)
      throws IOException {

    byte[] responseBytes = responseContent.getBytes(CHARSET_UTF8);
    if (gzipResponse) {
      // Compress the reply and adjust headers.
      //
      ByteArrayOutputStream output = null;
      GZIPOutputStream gzipOutputStream = null;
      Throwable caught = null;
      try {
        output = new ByteArrayOutputStream(responseBytes.length);
        gzipOutputStream = new GZIPOutputStream(output);
        gzipOutputStream.write(responseBytes);
        gzipOutputStream.finish();
        gzipOutputStream.flush();
        response.setHeader(CONTENT_ENCODING, CONTENT_ENCODING_GZIP);
        responseBytes = output.toByteArray();
      } catch (IOException e) {
        caught = e;
      } finally {
        if (null != gzipOutputStream) {
          gzipOutputStream.close();
        }
        if (null != output) {
          output.close();
        }
      }

      if (caught != null) {
        servletContext.log("Unable to compress response", caught);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return;
      }
    }

    // Send the reply.
    //
    response.setContentLength(responseBytes.length);
    response.setContentType(CONTENT_TYPE_TEXT_PLAIN_UTF8);
    response.setStatus(HttpServletResponse.SC_OK);
    response.getOutputStream().write(responseBytes);
  }

  /**
   * Called when the servlet itself has a problem, rather than the invoked
   * third-party method. It writes a simple 500 message back to the client.
   * 
   * @param servletContext
   * @param response
   * @param failure
   */
  public static void writeResponseForUnexpectedFailure(
      ServletContext servletContext, HttpServletResponse response,
      Throwable failure) {
    servletContext.log("Exception while dispatching incoming RPC call", failure);

    // Send GENERIC_FAILURE_MSG with 500 status.
    //
    try {
      response.setContentType("text/plain");
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().write(GENERIC_FAILURE_MSG);
    } catch (IOException ex) {
      servletContext.log(
          "respondWithUnexpectedFailure failed while sending the previous failure to the client",
          ex);
    }
  }

  private RPCServletUtils() {
    // Not instantiable
  }
}