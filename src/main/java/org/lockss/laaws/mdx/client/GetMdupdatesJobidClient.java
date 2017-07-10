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

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.lockss.laaws.mdx.model.Status;

/**
 * Client for the getMdupdatesJobid() operation.
 */
public class GetMdupdatesJobidClient extends BaseClient {

  public static void main(String[] args) {
    for (int i = 0; i < args.length; i++) {
      System.out.println("args[" + i + "] = " + args[i]);
    }

    if (args.length < 1) {
      System.err.println("ERROR: Missing command line argument with the "
	  + "identifier of the job for which the status is requested.");
    }

    WebTarget webTarget = getWebTarget().path("mdupdates").path(args[0]);
    System.out.println("webTarget.getUri() = " + webTarget.getUri());

    Response response = webTarget.request().get();
    int status = response.getStatus();
    System.out.println("status = " + status);
    System.out.println("statusInfo = " + response.getStatusInfo());

    if (status == 200) {
      Status result = response.readEntity(Status.class);
      System.out.println("result = " + result);
    } else {
      Object result = response.readEntity(Object.class);
      System.out.println("result = " + result);
    }
  }
}