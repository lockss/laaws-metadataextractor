/*

 Copyright (c) 2016-2020 Board of Trustees of Leland Stanford Jr. University,
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

import java.net.URI;
import org.lockss.metadata.extractor.job.Job;
import org.lockss.util.rest.mdx.MetadataUpdateSpec;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Client for the postMdupdates() operation.
 */
public class PostMdupdatesClient extends BaseClient {

  public static void main(String[] args) throws Exception {
    for (int i = 0; i < args.length; i++) {
      System.out.println("args[" + i + "] = " + args[i]);
    }

    if (args.length < 1) {
      System.err.println("ERROR: Missing command line arguments with the "
	  + "identifier of the Archival Unit and the metadata update "
	  + "operation to be performed.");
    } else if (args.length < 2) {
      System.err.println("ERROR: Missing command line arguments with the "
	  + "metadata update operation to be performed.");
    }

    MetadataUpdateSpec params = new MetadataUpdateSpec();
    params.setAuid(args[0]);
    params.setUpdateType(args[1]);
    System.out.println("params = '" + params + "'");

    String template = baseUri + "/mdupdates";

    // Create the URI of the request to the REST service.
    URI uri = UriComponentsBuilder.newInstance().uriComponents(
	UriComponentsBuilder.fromUriString(template).build())
	.build().encode().toUri();
    System.out.println("uri = " + uri);

    ResponseEntity<Job> response = getRestTemplate().exchange(uri,
	HttpMethod.POST, new HttpEntity<MetadataUpdateSpec>(params,
	    getHttpHeaders()), Job.class);

    int status = response.getStatusCodeValue();
    System.out.println("status = " + status);
    Job result = response.getBody();
    System.out.println("result = " + result);
  }
}
