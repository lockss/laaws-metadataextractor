/*

 Copyright (c) 2017 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.laaws.mdx.api;

import static org.junit.Assert.*;
import static org.lockss.laaws.mdx.api.MdupdatesApi.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lockss.laaws.mdx.model.Job;
import org.lockss.laaws.mdx.model.JobPageInfo;
import org.lockss.laaws.mdx.model.MetadataUpdateSpec;
import org.lockss.laaws.mdx.model.Status;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test class for org.lockss.laaws.mdx.api.MdupdatesApiController.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MdupdatesApiControllerTest {
  private static final Logger logger =
      LoggerFactory.getLogger(MdupdatesApiControllerTest.class);

  // The port that Tomcat is using during this test.
  @LocalServerPort
  private int port;

  // The application Context used to specify the command line arguments to be
  // used for the tests.
  @Autowired
  ApplicationContext appCtx;

  String goodAuid = "org|lockss|plugin|taylorandfrancis|"
      + "TaylorAndFrancisPlugin&base_url~http%3A%2F%2Fwww%2Etandfonline"
      + "%2Ecom%2F&journal_id~rafr20&volume_name~8";

  String goodAuName = "Africa Review Volume 8";
  /**
   * Runs the tests with authentication turned off.
   * 
   * @throws Exception
   *           if there are problems.
   */
  @Test
  public void runUnAuthenticatedTests() throws Exception {
    if (logger.isDebugEnabled()) logger.debug("port = " + port);

    // Specify the command line parameters to be used for the tests.
    List<String> cmdLineArgs = getCommandLineArguments();
    cmdLineArgs.add("-p");
    cmdLineArgs.add("config/mdupdatesApiControllerTestAuthOff.opt");

    CommandLineRunner runner = appCtx.getBean(CommandLineRunner.class);
    runner.run(cmdLineArgs.toArray(new String[cmdLineArgs.size()]));

    getSwaggerDocsTest();
    postMdupdatesUnAuthenticatedTest();
    getMdupdatesUnAuthenticatedTest();
    deleteMdupdatesUnAuthenticatedTest();

    if (logger.isDebugEnabled()) logger.debug("Done.");
  }

  /**
   * Runs the tests with authentication turned on.
   * 
   * @throws Exception
   *           if there are problems.
   */
  @Test
  public void runAuthenticatedTests() throws Exception {
    if (logger.isDebugEnabled()) logger.debug("port = " + port);

    // Specify the command line parameters to be used for the tests.
    List<String> cmdLineArgs = getCommandLineArguments();
    cmdLineArgs.add("-p");
    cmdLineArgs.add("config/mdupdatesApiControllerTestAuthOn.opt");

    CommandLineRunner runner = appCtx.getBean(CommandLineRunner.class);
    runner.run(cmdLineArgs.toArray(new String[cmdLineArgs.size()]));

    getSwaggerDocsTest();
    postMdupdatesAuthenticatedTest();
    getMdupdatesAuthenticatedTest();
    deleteMdupdatesAuthenticatedTest();

    if (logger.isDebugEnabled()) logger.debug("Done.");
  }

  /**
   * Provides the standard command line arguments to start the server.
   * 
   * @return a List<String> with the command line arguments.
   */
  private List<String> getCommandLineArguments() {
    List<String> cmdLineArgs = new ArrayList<String>();
    cmdLineArgs.add("-p");
    cmdLineArgs.add("config/common.xml");

    File folder = new File("tdbxml/prod");
    File[] listOfFiles = folder.listFiles();

    for (File file : listOfFiles) {
      String fileName = file.toString();

      if (fileName.endsWith(".xml")) {
	cmdLineArgs.add("-p");
	cmdLineArgs.add(fileName);
      }
    }

    cmdLineArgs.add("-p");
    cmdLineArgs.add("config/lockss.txt");

    return cmdLineArgs;
  }

  /**
   * Runs the Swagger-related tests.
   * 
   * @throws Exception
   *           if there are problems.
   */
  private void getSwaggerDocsTest() throws Exception {
    ResponseEntity<String> successResponse = new TestRestTemplate().exchange(
	getTestUrl("/v2/api-docs"), HttpMethod.GET, null, String.class);

    HttpStatus statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    String expectedBody = "{'swagger':'2.0',"
	+ "'info':{'description':'API of Metadata Extraction Service for LAAWS'"
        + "}}";

    JSONAssert.assertEquals(expectedBody, successResponse.getBody(), false);
  }

  /**
   * Runs the postMetadataAusItem()-related un-authenticated-specific tests.
   */
  private void postMdupdatesUnAuthenticatedTest() {
    String uri = "/mdupdates";

    ResponseEntity<String> errorResponse = new TestRestTemplate()
	.exchange(getTestUrl(uri), HttpMethod.POST, null, String.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusCode);

    errorResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(getTestUrl(uri), HttpMethod.POST, null, String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusCode);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate().exchange(getTestUrl(uri),
	HttpMethod.POST, new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(getTestUrl(uri), HttpMethod.POST,
	    new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);

    postMdupdatesCommonTest();
  }

  /**
   * Runs the postMetadataAusItem()-related authenticated-specific tests.
   */
  private void postMdupdatesAuthenticatedTest() {
    String uri = "/mdupdates";

    ResponseEntity<String> errorResponse = new TestRestTemplate()
	.exchange(getTestUrl(uri), HttpMethod.POST, null, String.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    errorResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(getTestUrl(uri), HttpMethod.POST, null, String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate().exchange(getTestUrl(uri),
	HttpMethod.POST, new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(getTestUrl(uri), HttpMethod.POST,
	    new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    postMdupdatesCommonTest();
  }

  /**
   * Runs the postMetadataAusItem()-related authentication-independent tests.
   */
  private void postMdupdatesCommonTest() {
    String uri = "/mdupdates";

    ResponseEntity<String> errorResponse =
	new TestRestTemplate("lockss-u", "lockss-p")
	.exchange(getTestUrl(uri), HttpMethod.POST, null, String.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusCode);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("lockss-u", "lockss-p")
	.exchange(getTestUrl(uri), HttpMethod.POST,
	    new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);

    Job job = postMdupdates(goodAuid, MD_UPDATE_FULL_EXTRACTION,
	HttpStatus.ACCEPTED);

    assertEquals(goodAuid, job.getAu().getId());
    assertEquals(goodAuName, job.getAu().getName());
    assertEquals(1, job.getStatus().getCode().intValue());
    assertNotNull(job.getCreationDate());
    assertNull(job.getStartDate());
    assertNull(job.getEndDate());

    String jobId = job.getId();
    assertNotNull(jobId);

    waitForJobStatus(jobId, "Success");
  }

  /**
   * Performs a POST operation.
   * 
   * @param auId
   *          A String with the identifier of the Archival Unit.
   * @param updateType
   *          A String with the type of POST operation to be performed.
   * @param expectedStatus
   *          An HttpStatus with the HTTP status of the result.
   * @return a Job with the details of the scheduled job.
   */
  private Job postMdupdates(String auId, String updateType,
      HttpStatus expectedStatus) {
    MetadataUpdateSpec metadataUpdateSpec = new MetadataUpdateSpec();
    metadataUpdateSpec.setAuid(auId);
    metadataUpdateSpec.setUpdateType(updateType);

    String uri = "/mdupdates";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<Job> response = new TestRestTemplate("lockss-u", "lockss-p")
	.exchange(getTestUrl(uri), HttpMethod.POST,
	    new HttpEntity<MetadataUpdateSpec>(metadataUpdateSpec, headers),
	    Job.class);

    HttpStatus statusCode = response.getStatusCode();
    assertEquals(expectedStatus, statusCode);

    return response.getBody();
  }

  /**
   * Waits for a job to reach an expected status.
   * 
   * @param jobId
   *          A String with the identifier of the job.
   * @param expectedJobStatus
   *          A String with the expected job status.
   */
  private void waitForJobStatus(String jobId, String expectedJobStatus) {
    Status jobStatus = null;
    int tries = 0;

    while (tries < 10) {
      jobStatus =  getJobStatus(jobId);
      if (expectedJobStatus.equals(jobStatus.getMsg())) {
	break;
      }

      try {
	Thread.sleep(10000);
      } catch (InterruptedException ie) {}

      tries++;
    }

    assertEquals(expectedJobStatus, jobStatus.getMsg());
  }

  /**
   * Provides the status of a job.
   * 
   * @param jobId
   *          A String with the identifier of the job.
   * @return a Status with the job status.
   */
  private Status getJobStatus(String jobId) {
    String uri = "/mdupdates/" + jobId;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<Status> response = new TestRestTemplate("lockss-u",
	"lockss-p").exchange(getTestUrl(uri), HttpMethod.GET,
	    new HttpEntity<String>(null, headers), Status.class);

    HttpStatus statusCode = response.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    return response.getBody();
  }

  /**
   * Runs the getMdupdates()-related un-authenticated-specific tests.
   */
  private void getMdupdatesUnAuthenticatedTest() {
    String uri = "/mdupdates";

    ResponseEntity<JobPageInfo> errorResponse = new TestRestTemplate()
	.exchange(getTestUrl(uri), HttpMethod.GET, null, JobPageInfo.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusCode);

    errorResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(getTestUrl(uri), HttpMethod.GET, null, JobPageInfo.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusCode);

    getMdupdatesCommonTest();
  }

  /**
   * Runs the getMdupdates()-related authenticated-specific tests.
   */
  private void getMdupdatesAuthenticatedTest() {
    String uri = "/mdupdates";

    ResponseEntity<JobPageInfo> errorResponse = new TestRestTemplate()
	.exchange(getTestUrl(uri), HttpMethod.GET, null, JobPageInfo.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    errorResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(getTestUrl(uri), HttpMethod.GET, null, JobPageInfo.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    getMdupdatesCommonTest();
  }

  /**
   * Runs the getMdupdates()-related authentication-independent tests.
   */
  private void getMdupdatesCommonTest() {
    String uri = "/mdupdates";

    ResponseEntity<JobPageInfo> errorResponse =
	new TestRestTemplate("lockss-u", "lockss-p").exchange(getTestUrl(uri),
	    HttpMethod.GET, null, JobPageInfo.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusCode);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    List<Job> jobs = getJobs().getJobs();
    assertEquals(1, jobs.size());

    Job firstJob = jobs.get(0);
    assertNotNull(firstJob.getId());
    assertNotNull(firstJob.getDescription());
    assertNotNull(firstJob.getCreationDate());
    assertNotNull(firstJob.getStartDate());
    assertNotNull(firstJob.getEndDate());
    assertEquals(goodAuid, firstJob.getAu().getId());
    assertEquals(goodAuName, firstJob.getAu().getName());
    assertEquals(5, firstJob.getStatus().getCode().intValue());

    uri = "/mdupdates/1234567";

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("lockss-u", "lockss-p")
	.exchange(getTestUrl(uri), HttpMethod.GET,
	    new HttpEntity<String>(null, headers), JobPageInfo.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.NOT_FOUND, statusCode);

    uri = "/mdupdates/non-numeric";

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("lockss-u", "lockss-p")
	.exchange(getTestUrl(uri), HttpMethod.GET,
	    new HttpEntity<String>(null, headers), JobPageInfo.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.NOT_FOUND, statusCode);
  }

  /**
   * Provides the list of existing jobs.
   * 
   * @return a JobPageInfo with the existing jobs.
   */
  private JobPageInfo getJobs() {
    String uri = "/mdupdates/";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<JobPageInfo> response = new TestRestTemplate("lockss-u",
	"lockss-p").exchange(getTestUrl(uri), HttpMethod.GET,
	    new HttpEntity<String>(null, headers), JobPageInfo.class);

    HttpStatus statusCode = response.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    JobPageInfo result = response.getBody();
    assertEquals(new Integer(50), result.getPageInfo().getResultsPerPage());
    assertEquals(new Integer(1), result.getPageInfo().getCurrentPage());
    assertNull(result.getPageInfo().getTotalCount());

    return result;
  }

  /**
   * Runs the deleteMdupdates()-related un-authenticated-specific tests.
   */
  private void deleteMdupdatesUnAuthenticatedTest() {
    String uri = "/mdupdates";

    ResponseEntity<String> errorResponse = new TestRestTemplate()
	.exchange(getTestUrl(uri), HttpMethod.DELETE, null, String.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusCode);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<Integer> successResponse =
	new TestRestTemplate().exchange(getTestUrl(uri), HttpMethod.DELETE,
	    new HttpEntity<String>(null, headers), Integer.class);

    statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    Integer result = successResponse.getBody();
    assertEquals(1, result.intValue());

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    successResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(getTestUrl(uri), HttpMethod.DELETE,
	    new HttpEntity<String>(null, headers), Integer.class);

    statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    result = successResponse.getBody();
    assertEquals(0, result.intValue());

    deleteMdupdatesCommonTest();
  }

  /**
   * Runs the deleteMdupdates()-related authenticated-specific tests.
   */
  private void deleteMdupdatesAuthenticatedTest() {
    String uri = "/mdupdates";

    ResponseEntity<String> errorResponse = new TestRestTemplate()
	.exchange(getTestUrl(uri), HttpMethod.DELETE, null, String.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse =
	new TestRestTemplate().exchange(getTestUrl(uri), HttpMethod.DELETE,
	    new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(getTestUrl(uri), HttpMethod.DELETE,
	    new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    ResponseEntity<Integer> successResponse = new TestRestTemplate("lockss-u",
	"lockss-p").exchange(getTestUrl(uri), HttpMethod.DELETE,
	    new HttpEntity<String>(null, headers), Integer.class);

    statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    Integer result = successResponse.getBody();
    assertEquals(1, result.intValue());

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    successResponse = new TestRestTemplate("lockss-u", "lockss-p")
	.exchange(getTestUrl(uri), HttpMethod.DELETE,
	    new HttpEntity<String>(null, headers), Integer.class);

    statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    result = successResponse.getBody();
    assertEquals(0, result.intValue());

    deleteMdupdatesCommonTest();
  }

  /**
   * Runs the deleteMdupdates()-related authenticated-independent tests.
   */
  private void deleteMdupdatesCommonTest() {
    String uri = "/mdupdates/1234567";

    ResponseEntity<String> errorResponse =
	new TestRestTemplate("lockss-u", "lockss-p").exchange(getTestUrl(uri),
	    HttpMethod.DELETE, null, String.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusCode);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("lockss-u", "lockss-p")
	.exchange(getTestUrl(uri), HttpMethod.DELETE,
	    new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.NOT_FOUND, statusCode);

    uri = "/mdupdates/non-numeric";

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("lockss-u", "lockss-p")
	.exchange(getTestUrl(uri), HttpMethod.DELETE,
	    new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.NOT_FOUND, statusCode);
  }

  /**
   * Provides the URL to be tested.
   * 
   * @param uri
   *          A String with the URI of the URL to be tested.
   * @return a String with the URL to be tested.
   */
  private String getTestUrl(String uri) {
    return "http://localhost:" + port + uri;
  }
}
