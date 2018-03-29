/*

 Copyright (c) 2017-2018 Board of Trustees of Leland Stanford Jr. University,
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

import static org.lockss.laaws.mdx.api.MdupdatesApi.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lockss.laaws.mdx.model.Job;
import org.lockss.laaws.mdx.model.JobPageInfo;
import org.lockss.laaws.mdx.model.MetadataUpdateSpec;
import org.lockss.laaws.mdx.model.Status;
import org.lockss.test.SpringLockssTestCase;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test class for org.lockss.laaws.mdx.api.MdupdatesApiController.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestMdupdatesApiController extends SpringLockssTestCase {
  private static final Logger logger =
      LoggerFactory.getLogger(TestMdupdatesApiController.class);

  // The port that Tomcat is using during this test.
  @LocalServerPort
  private int port;

  // The application Context used to specify the command line arguments to be
  // used for the tests.
  @Autowired
  ApplicationContext appCtx;

//  // The indication of whether the external REST Repository service is
//  // available.
//  private static boolean isRestRepositoryServiceAvailable = false;

  // The identifier of an AU that exists in the test system.
  String goodAuid = "org|lockss|plugin|pensoft|oai|PensoftOaiPlugin"
      + "&au_oai_date~2014&au_oai_set~biorisk"
      + "&base_url~http%3A%2F%2Fbiorisk%2Epensoft%2Enet%2F";

  // The name of an AU that exists in the test system.
  String goodAuName = "BioRisk Volume 2014";

//  /**
//   * Set up code to be run before all tests.
//   */
//  @BeforeClass
//  public static void setUpBeforeAllTests() throws IOException {
//    // Get the external REST Repository service location. 
//    String restServiceLocation = getPropertyValueFromFile(
//	"org.lockss.plugin.auContentFromWs.urlListWs.restServiceLocation",
//	new File("config/lockss.txt"));
//    if (logger.isDebugEnabled())
//      logger.debug("restServiceLocation = " + restServiceLocation);
//
//    assertNotNull("REST Repository service location not found",
//	restServiceLocation);
//
//    // Populate the indication of whether the external REST Repository service
//    // is available.
//    isRestRepositoryServiceAvailable =
//	checkExternalRestService(restServiceLocation,
//	    Collections.singletonMap("auid", "someAuId"),
//	    HttpStatus.OK.value());
//    if (logger.isDebugEnabled())
//      logger.debug("isRestRepositoryServiceAvailable = "
//	  + isRestRepositoryServiceAvailable);
//  }

  /**
   * Set up code to be run before each test.
   * 
   * @throws IOException if there are problems.
   */
  @Before
  public void setUpBeforeEachTest() throws IOException {
    if (logger.isDebugEnabled()) logger.debug("port = " + port);

    // Set up the temporary directory where the test data will reside.
    setUpTempDirectory(TestMdupdatesApiController.class.getCanonicalName());

    // Copy the necessary files to the test temporary directory.
    File srcTree = new File(new File("test"), "cache");
    if (logger.isDebugEnabled())
      logger.debug("srcTree = " + srcTree.getAbsolutePath());

    copyToTempDir(srcTree);

    srcTree = new File(new File("test"), "tdbxml");
    if (logger.isDebugEnabled())
      logger.debug("srcTree = " + srcTree.getAbsolutePath());

    copyToTempDir(srcTree);

    File srcFile = new File(new File("test"), "lockss-plugins.jar");
    if (logger.isDebugEnabled())
      logger.debug("srcFile = " + srcFile.getAbsolutePath());

    copyToTempDir(srcFile);
  }

  /**
   * Runs the tests with authentication turned off.
   * 
   * @throws Exception
   *           if there are problems.
   */
  @Test
  public void runUnAuthenticatedTests() throws Exception {
    // Specify the command line parameters to be used for the tests.
    List<String> cmdLineArgs = getCommandLineArguments();
    cmdLineArgs.add("-p");
    cmdLineArgs.add("test/config/mdupdatesApiControllerTestAuthOff.opt");

    CommandLineRunner runner = appCtx.getBean(CommandLineRunner.class);
    runner.run(cmdLineArgs.toArray(new String[cmdLineArgs.size()]));

    getSwaggerDocsTest();
    getStatusTest();
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
    // Specify the command line parameters to be used for the tests.
    List<String> cmdLineArgs = getCommandLineArguments();
    cmdLineArgs.add("-p");
    cmdLineArgs.add("test/config/mdupdatesApiControllerTestAuthOn.opt");

    CommandLineRunner runner = appCtx.getBean(CommandLineRunner.class);
    runner.run(cmdLineArgs.toArray(new String[cmdLineArgs.size()]));

    getSwaggerDocsTest();
    getStatusTest();
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

    File folder =
	new File(new File(new File(getTempDirPath()), "tdbxml"), "prod");
    if (logger.isDebugEnabled()) logger.debug("folder = " + folder);

    cmdLineArgs.add("-x");
    cmdLineArgs.add(folder.getAbsolutePath());
    cmdLineArgs.add("-p");
    cmdLineArgs.add("config/lockss.txt");
    cmdLineArgs.add("-p");
    cmdLineArgs.add("test/config/lockss.txt");
    cmdLineArgs.add("-p");
    cmdLineArgs.add("test/config/lockss.opt");
    cmdLineArgs.add("-b");
    cmdLineArgs.add(getPlatformDiskSpaceConfigPath());

    return cmdLineArgs;
  }

  /**
   * Runs the Swagger-related tests.
   * 
   * @throws Exception
   *           if there are problems.
   */
  private void getSwaggerDocsTest() throws Exception {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    ResponseEntity<String> successResponse = new TestRestTemplate().exchange(
	getTestUrlTemplate("/v2/api-docs"), HttpMethod.GET, null, String.class);

    HttpStatus statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    String expectedBody = "{'swagger':'2.0',"
	+ "'info':{'description':'API of Metadata Extraction Service for LAAWS'"
        + "}}";

    JSONAssert.assertEquals(expectedBody, successResponse.getBody(), false);
    if (logger.isDebugEnabled()) logger.debug("Done.");
  }

  /**
   * Runs the status-related tests.
   * 
   * @throws Exception
   *           if there are problems.
   */
  private void getStatusTest() throws Exception {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    ResponseEntity<String> successResponse = new TestRestTemplate().exchange(
	getTestUrlTemplate("/status"), HttpMethod.GET, null, String.class);

    HttpStatus statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    String expectedBody = "{\"version\":\"1.0.0\",\"ready\":true}}";

    JSONAssert.assertEquals(expectedBody, successResponse.getBody(), false);
    if (logger.isDebugEnabled()) logger.debug("Done.");
  }

  /**
   * Runs the postMetadataAusItem()-related un-authenticated-specific tests.
   */
  private void postMdupdatesUnAuthenticatedTest() throws Exception {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    String url = getTestUrlTemplate("/mdupdates");

    ResponseEntity<String> errorResponse = new TestRestTemplate()
	.exchange(url, HttpMethod.POST, null, String.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusCode);

    errorResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(url, HttpMethod.POST, null, String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusCode);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate().exchange(url, HttpMethod.POST,
	new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(url, HttpMethod.POST, new HttpEntity<String>(null, headers),
	    String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);

    postMdupdatesCommonTest();

    if (logger.isDebugEnabled()) logger.debug("Done.");
  }

  /**
   * Runs the postMetadataAusItem()-related authenticated-specific tests.
   */
  private void postMdupdatesAuthenticatedTest() throws Exception {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    String url = getTestUrlTemplate("/mdupdates");

    ResponseEntity<String> errorResponse = new TestRestTemplate()
	.exchange(url, HttpMethod.POST, null, String.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    errorResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(url, HttpMethod.POST, null, String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate().exchange(url, HttpMethod.POST,
	new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(url, HttpMethod.POST, new HttpEntity<String>(null, headers),
	    String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    postMdupdatesCommonTest();

    if (logger.isDebugEnabled()) logger.debug("Done.");
  }

  /**
   * Runs the postMetadataAusItem()-related authentication-independent tests.
   */
  private void postMdupdatesCommonTest() throws Exception {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    String url = getTestUrlTemplate("/mdupdates");

    ResponseEntity<String> errorResponse = new TestRestTemplate("lockss-u",
	"lockss-p").exchange(url, HttpMethod.POST, null, String.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusCode);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("lockss-u", "lockss-p").exchange(url,
	HttpMethod.POST, new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);

    Job job = postMdupdates(goodAuid, MD_UPDATE_FULL_EXTRACTION,
	HttpStatus.ACCEPTED);

    assertEquals(goodAuid, job.getAu().getId());
    assertEquals(goodAuName, job.getAu().getName());
    assertNotNull(job.getCreationDate());
    assertNull(job.getEndDate());

    String jobId = job.getId();
    assertNotNull(jobId);

//    if (isRestRepositoryServiceAvailable) {
      waitForJobStatus(jobId, "Success");
//    } else {
//      waitForJobStatus(jobId, "Failure");
//    }

    if (logger.isDebugEnabled()) logger.debug("Done.");
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

    String url = getTestUrlTemplate("/mdupdates");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<Job> response = new TestRestTemplate("lockss-u", "lockss-p")
	.exchange(url, HttpMethod.POST,
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
   * @param expectedJobStatusPrefix
   *          A String with the expected job status first characters.
   */
  private void waitForJobStatus(String jobId, String expectedJobStatusPrefix)
      throws Exception {
    int tries = 0;
    String jobStatusMessage = "";

    while (tries < 10) {
      Status jobStatus =  getJobStatus(jobId);
      if (logger.isDebugEnabled()) logger.debug("jobStatus = " + jobStatus);

      jobStatusMessage = jobStatus.getMsg();

      if (jobStatusMessage != null
	  && jobStatusMessage.startsWith(expectedJobStatusPrefix)) {
	break;
      }

      try {
	Thread.sleep(10000);
      } catch (InterruptedException ie) {}

      tries++;
    }

    assertTrue(jobStatusMessage != null
	&& jobStatusMessage.startsWith(expectedJobStatusPrefix));
  }

  /**
   * Provides the status of a job.
   * 
   * @param jobId
   *          A String with the identifier of the job.
   * @return a Status with the job status.
   */
  private Status getJobStatus(String jobId) throws Exception {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    String template = getTestUrlTemplate("/mdupdates/{jobid}");

    // Create the URI of the request to the REST service.
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(template)
	.build().expand(Collections.singletonMap("jobid", jobId));

    URI uri = UriComponentsBuilder.newInstance().uriComponents(uriComponents)
	.build().encode().toUri();
    if (logger.isDebugEnabled()) logger.debug("uri = " + uri);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<Status> response = new TestRestTemplate("lockss-u",
	"lockss-p").exchange(uri, HttpMethod.GET,
	    new HttpEntity<String>(null, headers), Status.class);

    HttpStatus statusCode = response.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    if (logger.isDebugEnabled()) logger.debug("Done.");
    return response.getBody();
  }

  /**
   * Runs the getMdupdates()-related un-authenticated-specific tests.
   */
  private void getMdupdatesUnAuthenticatedTest() {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    String url = getTestUrlTemplate("/mdupdates");

    ResponseEntity<JobPageInfo> successResponse = new TestRestTemplate()
	.exchange(url, HttpMethod.GET, null, JobPageInfo.class);

    HttpStatus statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    JobPageInfo result = successResponse.getBody();
    assertEquals(new Integer(50), result.getPageInfo().getResultsPerPage());
    assertEquals(new Integer(1), result.getPageInfo().getCurrentPage());
    assertNull(result.getPageInfo().getTotalCount());

    List<Job> jobs = result.getJobs();
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

    successResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(url, HttpMethod.GET, null, JobPageInfo.class);

    statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    result = successResponse.getBody();
    assertEquals(new Integer(50), result.getPageInfo().getResultsPerPage());
    assertEquals(new Integer(1), result.getPageInfo().getCurrentPage());
    assertNull(result.getPageInfo().getTotalCount());

    jobs = result.getJobs();
    assertEquals(1, jobs.size());

    firstJob = jobs.get(0);
    assertNotNull(firstJob.getId());
    assertNotNull(firstJob.getDescription());
    assertNotNull(firstJob.getCreationDate());
    assertNotNull(firstJob.getStartDate());
    assertNotNull(firstJob.getEndDate());
    assertEquals(goodAuid, firstJob.getAu().getId());
    assertEquals(goodAuName, firstJob.getAu().getName());
    assertEquals(5, firstJob.getStatus().getCode().intValue());

    getMdupdatesCommonTest();

    if (logger.isDebugEnabled()) logger.debug("Done.");
  }

  /**
   * Runs the getMdupdates()-related authenticated-specific tests.
   */
  private void getMdupdatesAuthenticatedTest() {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    String url = getTestUrlTemplate("/mdupdates");

    ResponseEntity<JobPageInfo> errorResponse = new TestRestTemplate()
	.exchange(url, HttpMethod.GET, null, JobPageInfo.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    errorResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(url, HttpMethod.GET, null, JobPageInfo.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    getMdupdatesCommonTest();

    if (logger.isDebugEnabled()) logger.debug("Done.");
  }

  /**
   * Runs the getMdupdates()-related authentication-independent tests.
   */
  private void getMdupdatesCommonTest() {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    String url = getTestUrlTemplate("/mdupdates");

    ResponseEntity<JobPageInfo> successResponse =
	new TestRestTemplate("lockss-u", "lockss-p").exchange(url,
	    HttpMethod.GET, null, JobPageInfo.class);

    HttpStatus statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    JobPageInfo result = successResponse.getBody();
    assertEquals(new Integer(50), result.getPageInfo().getResultsPerPage());
    assertEquals(new Integer(1), result.getPageInfo().getCurrentPage());
    assertNull(result.getPageInfo().getTotalCount());

    List<Job> jobs = result.getJobs();
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

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    successResponse = new TestRestTemplate("lockss-u", "lockss-p")
	.exchange(url, HttpMethod.GET,new HttpEntity<String>(null, headers),
	    JobPageInfo.class);

    statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    result = successResponse.getBody();
    assertEquals(new Integer(50), result.getPageInfo().getResultsPerPage());
    assertEquals(new Integer(1), result.getPageInfo().getCurrentPage());
    assertNull(result.getPageInfo().getTotalCount());

    jobs = result.getJobs();
    assertEquals(1, jobs.size());

    firstJob = jobs.get(0);
    assertNotNull(firstJob.getId());
    assertNotNull(firstJob.getDescription());
    assertNotNull(firstJob.getCreationDate());
    assertNotNull(firstJob.getStartDate());
    assertNotNull(firstJob.getEndDate());
    assertEquals(goodAuid, firstJob.getAu().getId());
    assertEquals(goodAuName, firstJob.getAu().getName());
    assertEquals(5, firstJob.getStatus().getCode().intValue());

    String template = getTestUrlTemplate("/mdupdates/{jobid}");

    // Create the URI of the request to the REST service.
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(template)
	.build().expand(Collections.singletonMap("jobid", "1234567"));

    URI uri = UriComponentsBuilder.newInstance().uriComponents(uriComponents)
	.build().encode().toUri();
    if (logger.isDebugEnabled()) logger.debug("uri = " + uri);

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<JobPageInfo> errorResponse = new TestRestTemplate("lockss-u",
	"lockss-p").exchange(uri, HttpMethod.GET, new HttpEntity<String>(null,
	    headers), JobPageInfo.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.NOT_FOUND, statusCode);

    // Create the URI of the request to the REST service.
    uriComponents = UriComponentsBuilder.fromUriString(template).build()
	.expand(Collections.singletonMap("jobid", "non-numeric"));

    uri = UriComponentsBuilder.newInstance().uriComponents(uriComponents)
	.build().encode().toUri();
    if (logger.isDebugEnabled()) logger.debug("uri = " + uri);

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("lockss-u", "lockss-p").exchange(uri,
	HttpMethod.GET, new HttpEntity<String>(null, headers),
	JobPageInfo.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.NOT_FOUND, statusCode);

    if (logger.isDebugEnabled()) logger.debug("Done.");
  }

  /**
   * Runs the deleteMdupdates()-related un-authenticated-specific tests.
   */
  private void deleteMdupdatesUnAuthenticatedTest() {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    String url = getTestUrlTemplate("/mdupdates");

    ResponseEntity<String> errorResponse = new TestRestTemplate().exchange(url,
	HttpMethod.DELETE, null, String.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusCode);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<Integer> successResponse = new TestRestTemplate()
	.exchange(url, HttpMethod.DELETE, new HttpEntity<String>(null, headers),
	    Integer.class);

    statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    Integer result = successResponse.getBody();
    assertEquals(1, result.intValue());

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    successResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(url, HttpMethod.DELETE, new HttpEntity<String>(null, headers),
	    Integer.class);

    statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    result = successResponse.getBody();
    assertEquals(0, result.intValue());

    deleteMdupdatesCommonTest();

    if (logger.isDebugEnabled()) logger.debug("Done.");
  }

  /**
   * Runs the deleteMdupdates()-related authenticated-specific tests.
   */
  private void deleteMdupdatesAuthenticatedTest() {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    String url = getTestUrlTemplate("/mdupdates");

    ResponseEntity<String> errorResponse = new TestRestTemplate().exchange(url,
	HttpMethod.DELETE, null, String.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate().exchange(url, HttpMethod.DELETE,
	new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("fakeUser", "fakePassword")
	.exchange(url, HttpMethod.DELETE, new HttpEntity<String>(null, headers),
	    String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNAUTHORIZED, statusCode);

    ResponseEntity<Integer> successResponse = new TestRestTemplate("lockss-u",
	"lockss-p").exchange(url, HttpMethod.DELETE,
	    new HttpEntity<String>(null, headers), Integer.class);

    statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    Integer result = successResponse.getBody();
    assertEquals(1, result.intValue());

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    successResponse = new TestRestTemplate("lockss-u", "lockss-p").exchange(url,
	HttpMethod.DELETE, new HttpEntity<String>(null, headers),
	Integer.class);

    statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    result = successResponse.getBody();
    assertEquals(0, result.intValue());

    deleteMdupdatesCommonTest();

    if (logger.isDebugEnabled()) logger.debug("Done.");
  }

  /**
   * Runs the deleteMdupdates()-related authenticated-independent tests.
   */
  private void deleteMdupdatesCommonTest() {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    String template = getTestUrlTemplate("/mdupdates/{jobid}");

    // Create the URI of the request to the REST service.
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(template)
	.build().expand(Collections.singletonMap("jobid", "1234567"));

    URI uri = UriComponentsBuilder.newInstance().uriComponents(uriComponents)
	.build().encode().toUri();
    if (logger.isDebugEnabled()) logger.debug("uri = " + uri);

    ResponseEntity<String> errorResponse = new TestRestTemplate("lockss-u",
	"lockss-p").exchange(uri, HttpMethod.DELETE, null, String.class);

    HttpStatus statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusCode);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("lockss-u", "lockss-p").exchange(uri,
	HttpMethod.DELETE, new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.NOT_FOUND, statusCode);

    // Create the URI of the request to the REST service.
    uriComponents = UriComponentsBuilder.fromUriString(template).build()
	.expand(Collections.singletonMap("jobid", "non-numeric"));

    uri = UriComponentsBuilder.newInstance().uriComponents(uriComponents)
	.build().encode().toUri();
    if (logger.isDebugEnabled()) logger.debug("uri = " + uri);

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    errorResponse = new TestRestTemplate("lockss-u", "lockss-p").exchange(uri,
	HttpMethod.DELETE, new HttpEntity<String>(null, headers), String.class);

    statusCode = errorResponse.getStatusCode();
    assertEquals(HttpStatus.NOT_FOUND, statusCode);
    if (logger.isDebugEnabled()) logger.debug("Done.");
  }

  /**
   * Provides the URL template to be tested.
   * 
   * @param pathAndQueryParams
   *          A String with the path and query parameters of the URL template to
   *          be tested.
   * @return a String with the URL template to be tested.
   */
  private String getTestUrlTemplate(String pathAndQueryParams) {
    return "http://localhost:" + port + pathAndQueryParams;
  }
}
