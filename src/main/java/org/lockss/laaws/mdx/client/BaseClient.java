/*

 Copyright (c) 2016-2017 Board of Trustees of Leland Stanford Jr. University,
 all rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 Except as contained in this notice, the name of Stanford University shall not
 be used in advertising or otherwise to promote the sale, use or other dealings
 in this Software without prior written authorization from Stanford University.

 */
package org.lockss.laaws.mdx.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import org.glassfish.jersey.client.ClientConfig;

/**
 * A base client for all of the LAAWS-MDX web service operations.
 */
public class BaseClient {
  private static final String userName = "lockss-u";
  private static final String password = "lockss-p";

  private static final String baseUri = "http://localhost:8888";

  protected static WebTarget getWebTarget() {
    ClientConfig config = new ClientConfig();
    config.register(JacksonJsonProvider.class);
    Client client = ClientBuilder.newClient(config);
    WebTarget webTarget = client.target(baseUri);
    webTarget.register(new BasicAuthenticator(userName, password));

    return webTarget;
  }

  public static class BasicAuthenticator implements ClientRequestFilter {
    private final String authenticationHeader;

    public BasicAuthenticator(String username, String password) {
      authenticationHeader = getAutheticationHeader(username, password);
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
      requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION,
	  authenticationHeader);
    }

    private String getAutheticationHeader(String username, String password) {
      StringBuffer sb = new StringBuffer(username);
      sb.append(':').append(password);

      try {
	return "Basic " + Base64.getEncoder()
	.encodeToString(sb.toString().getBytes("UTF-8"));
      } catch (UnsupportedEncodingException uee) {
	throw new RuntimeException(uee);
      }
    }
  }
}
