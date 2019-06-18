/*

 Copyright (c) 2017-2019 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.laaws.mdx.impl;

import static org.lockss.laaws.mdx.impl.MdupdatesApiServiceImpl.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lockss.config.Configuration;
import org.lockss.laaws.mdx.model.JobPageInfo;
import org.lockss.laaws.mdx.model.MetadataUpdateSpec;
import org.lockss.laaws.mdx.model.PageInfo;
import org.lockss.laaws.rs.client.WARCImporter;
import org.lockss.laaws.rs.core.LocalLockssRepository;
import org.lockss.laaws.rs.core.LockssRepository;
import org.lockss.log.L4JLogger;
import org.lockss.metadata.MetadataDbManager;
import org.lockss.metadata.extractor.job.Job;
import org.lockss.metadata.extractor.job.JobContinuationToken;
import org.lockss.metadata.extractor.job.JobDbManager;
import org.lockss.metadata.extractor.job.JobManager;
import org.lockss.metadata.extractor.job.Status;
import org.lockss.rs.RestUtil;
import org.lockss.test.SpringLockssTestCase;
import org.lockss.util.ListUtil;
import org.skyscreamer.jsonassert.JSONAssert;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test class for org.lockss.laaws.mdx.api.MdupdatesApiServiceImpl.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestMdupdatesApiServiceImpl extends SpringLockssTestCase {
  private static final L4JLogger log = L4JLogger.getLogger();

  private static final String UI_PORT_CONFIGURATION_TEMPLATE =
      "UiPortConfigTemplate.txt";
  private static final String UI_PORT_CONFIGURATION_FILE = "UiPort.txt";
  private static final String DB_CONFIGURATION_TEMPLATE =
      "DbConfigTemplate.txt";
  private static final String DB_CONFIGURATION_FILE = "DbConfig.txt";
  private static final String REPO_CONFIGURATION_TEMPLATE =
      "RepositoryConfigTemplate.txt";
  private static final String REPO_CONFIGURATION_FILE = "RepositoryConfig.txt";

  private static final String EMPTY_STRING = "";

  private static final String REPO_COLLECTION = "testrepo";

  // The identifiers of the Archival Units that contains metadata in the test
  // system.
  private static final String AUID_1 =
      "org|lockss|plugin|pensoft|oai|PensoftOaiPlugin"
	  + "&au_oai_date~2014&au_oai_set~biorisk"
	  + "&base_url~http%3A%2F%2Fbiorisk%2Epensoft%2Enet%2F";
  private static final String AUID_2 =
      "org|lockss|plugin|atypon|bloomsburyqatar|BloomsburyQatarPlugin"
	  + "&base_url~http%3A%2F%2Fwww%2Eqscience%2Ecom%2F"
	  + "&journal_id~nmejre&volume_name~2014";
  private static final String AUID_3 =
      "org|lockss|plugin|hindawi|HindawiPublishingCorporationPlugin"
	  + "&base_url~http%3A%2F%2Fwww%2Ehindawi%2Ecom%2F"
	  + "&download_url~http%3A%2F%2Fdownloads%2Ehindawi%2Ecom%2F"
	  + "&journal_id~ijpg&volume_name~2014";

  // The names of the Archival Units that contains metadata in the test system.
  private static final String AU_NAME_1 = "BioRisk Volume 2014";
  private static final String AU_NAME_2 = "Bloomsbury Qatar Foundation Plugin,"
      + " Base URL http://www.qscience.com/, Journal Abbreviation nmejre,"
      + " Volume 2014";
  private static final String AU_NAME_3 = "Hindawi Publishing Corporation "
      + "Plugin, Base URL http://www.hindawi.com/, Download URL "
      + "http://downloads.hindawi.com/, Journal ID ijpg, Volume 2014";

  // The names of the WARC files with the contents of the Archival Units that
  // contains metadata in the test system.
  private static final String WARC_1 =
      "biorisk2014-20170202064641427-00000.warc";
  private static final String WARC_2 =
      "nmejre2014-20170202064712817-00000.warc";
  private static final String WARC_3 = "ijpg2014-20170202064659461-00000.warc";

  // Identifiers of AUs for testing job queries.
  private static final String TEST_AUID_1 =
      "org|lockss|plugin|pensoft|oai|PensoftOaiPlugin"
      + "&au_oai_date~2010&au_oai_set~zookeys"
      + "&base_url~http%3A%2F%2Fzookeys%2Epensoft%2Enet%2F";
  private static final String TEST_AUID_2 =
      "org|lockss|plugin|pensoft|oai|PensoftOaiPlugin"
      + "&au_oai_date~2011&au_oai_set~zookeys"
      + "&base_url~http%3A%2F%2Fzookeys%2Epensoft%2Enet%2F";
  private static final String TEST_AUID_3 =
      "org|lockss|plugin|pensoft|oai|PensoftOaiPlugin"
      + "&au_oai_date~2012&au_oai_set~zookeys"
      + "&base_url~http%3A%2F%2Fzookeys%2Epensoft%2Enet%2F";
  private static final String TEST_AUID_4 =
      "org|lockss|plugin|pensoft|oai|PensoftOaiPlugin"
      + "&au_oai_date~2013&au_oai_set~zookeys"
      + "&base_url~http%3A%2F%2Fzookeys%2Epensoft%2Enet%2F";
  private static final String TEST_AUID_5 =
      "org|lockss|plugin|pensoft|oai|PensoftOaiPlugin"
      + "&au_oai_date~2014&au_oai_set~zookeys"
      + "&base_url~http%3A%2F%2Fzookeys%2Epensoft%2Enet%2F";
  private static final String TEST_AUID_6 =
      "org|lockss|plugin|pensoft|oai|PensoftOaiPlugin"
      + "&au_oai_date~2015&au_oai_set~zookeys"
      + "&base_url~http%3A%2F%2Fzookeys%2Epensoft%2Enet%2F";
  private static final String TEST_AUID_7 =
      "org|lockss|plugin|pensoft|oai|PensoftOaiPlugin"
      + "&au_oai_date~2016&au_oai_set~zookeys"
      + "&base_url~http%3A%2F%2Fzookeys%2Epensoft%2Enet%2F";

  // The identifier of an AU that does not exist in the test system.
  private static final String UNKNOWN_AUID ="unknown_auid";

  // The identifier of a job that does not exist in the test system.
  private static final String UNKNOWN_JOBID ="unknown_jobid";

  // A bad update type for POST operations.
  private static final String BAD_UPDATE_TYPE ="bad_update_type";

  // The test jobs.
  private Job job1 = null;
  private Job job2 = null;
  private Job job3 = null;
  private Job job4 = null;
  private Job job5 = null;
  private Job job6 = null;
  private Job job7 = null;

  // Credentials.
  private final Credentials USER_ADMIN =
      this.new Credentials("lockss-u", "lockss-p");
  private final Credentials CONTENT_ADMIN =
      this.new Credentials("content-admin", "I'mContentAdmin");
  private final Credentials ACCESS_CONTENT =
      this.new Credentials("access-content", "I'mAccessContent");
  private final Credentials ANYBODY =
      this.new Credentials("someUser", "somePassword");

  // The port that Tomcat is using during this test.
  @LocalServerPort
  private int port;

  // The application Context used to specify the command line arguments to be
  // used for the tests.
  @Autowired
  ApplicationContext appCtx;

  /**
   * Set up code to be run before all tests.
   */
  @BeforeClass
  public static void setUpBeforeAllTests() {
  }

  /**
   * Set up code to be run before each test.
   * 
   * @throws Exception if there are problems.
   */
  @Before
  public void setUpBeforeEachTest() throws Exception {
    log.debug2("port = {}", port);

    // Set up the temporary directory where the test data will reside.
    setUpTempDirectory(TestMdupdatesApiServiceImpl.class.getCanonicalName());

    // Copy the necessary files to the test temporary directory.
    File srcTree1 = new File(new File("test"), "tdbxml");
    log.trace("srcTree1 = {}", () -> srcTree1.getAbsolutePath());

    copyToTempDir(srcTree1);

    File srcTree2 = new File(new File("test"), "content");
    log.trace("srcTree2 = {}", () -> srcTree2.getAbsolutePath());

    copyToTempDir(srcTree2);

    // Set up the UI port.
    setUpUiPort(UI_PORT_CONFIGURATION_TEMPLATE, UI_PORT_CONFIGURATION_FILE);

    // Set up the database configuration.
    setUpDbConfig(DB_CONFIGURATION_TEMPLATE, DB_CONFIGURATION_FILE);

    // Set up the repository configuration.
    setUpRepositoryConfig(REPO_CONFIGURATION_TEMPLATE, REPO_CONFIGURATION_FILE);

    File repositoryDir = new File(getTempDirPath(), "testRepo");
    log.trace("repositoryDir = {}", () -> repositoryDir.getAbsolutePath());

    LockssRepository repository =
	new LocalLockssRepository(repositoryDir, "artifact-index.ser");

    // Import the content of the first Archival Unit.
    File warc1 = new File(new File(getTempDirPath(), "content"), WARC_1);
    log.trace("warc1 = {}", () -> warc1.getAbsolutePath());

    new WARCImporter(repository, REPO_COLLECTION, AUID_1).importWARC(warc1);
    log.trace("Done importing WARC file = {}", () -> warc1.getAbsolutePath());

    // Import the content of the second Archival Unit.
    File warc2 = new File(new File(getTempDirPath(), "content"), WARC_2);
    log.trace("warc2 = {}", () -> warc2.getAbsolutePath());

    new WARCImporter(repository, REPO_COLLECTION, AUID_2).importWARC(warc2);
    log.trace("Done importing WARC file = {}", () -> warc2.getAbsolutePath());

    // Import the content of the first Archival Unit.
    File warc3 = new File(new File(getTempDirPath(), "content"), WARC_3);
    log.trace("warc3 = {}", () -> warc3.getAbsolutePath());

    new WARCImporter(repository, REPO_COLLECTION, AUID_3).importWARC(warc3);
    log.trace("Done importing WARC file = {}", () -> warc3.getAbsolutePath());

    // Create the embedded Derby metadata database to use during the tests.
    TestDerbyMetadataDbManager testMetadataDbManager =
	new TestDerbyMetadataDbManager();
    testMetadataDbManager.startService();

    // Create the embedded Derby job database to use during the tests.
    TestDerbyJobDbManager testJobDbManager = new TestDerbyJobDbManager();
    testJobDbManager.startService();

    // Populate the jobs test database.
    JobManager jm = new JobManager(testJobDbManager);

    jm.createJobForTesting(1L, "Job-1", TEST_AUID_1, 5L, "Success-1");
    jm.createJobForTesting(2L, "Job-2", TEST_AUID_2, 5L, "Success-2");
    jm.createJobForTesting(1L, "Job-3", TEST_AUID_3, 5L, "Success-3");
    jm.createJobForTesting(2L, "Job-4", TEST_AUID_4, 5L, "Success-4");
    jm.createJobForTesting(1L, "Job-5", TEST_AUID_5, 5L, "Success-5");
    jm.createJobForTesting(2L, "Job-6", TEST_AUID_6, 5L, "Success-6");
    jm.createJobForTesting(1L, "Job-7", TEST_AUID_7, 5L, "Success-7");

    log.debug2("Done");
  }

  /**
   * Runs the tests with authentication turned off.
   * 
   * @throws Exception
   *           if there are problems.
   */
  @Test
  public void runUnAuthenticatedTests() throws Exception {
    log.debug2("Invoked");

    // Specify the command line parameters to be used for the tests.
    List<String> cmdLineArgs = getCommandLineArguments();
    cmdLineArgs.add("-p");
    cmdLineArgs.add("test/config/testAuthOff.txt");

    CommandLineRunner runner = appCtx.getBean(CommandLineRunner.class);
    runner.run(cmdLineArgs.toArray(new String[cmdLineArgs.size()]));

    getSwaggerDocsTest();
    runMethodsNotAllowedUnAuthenticatedTest();
    getMdupdatesJobidUnAuthenticatedTest();
    getMdupdatesUnAuthenticatedTest();
    postMdupdatesUnAuthenticatedTest();
    deleteMdupdatesJobidUnAuthenticatedTest();
    deleteMdupdatesUnAuthenticatedTest();

    log.debug2("Done");
  }

  /**
   * Runs the tests with authentication turned on.
   * 
   * @throws Exception
   *           if there are problems.
   */
  @Test
  public void runAuthenticatedTests() throws Exception {
    log.debug2("Invoked");

    // Specify the command line parameters to be used for the tests.
    List<String> cmdLineArgs = getCommandLineArguments();
    cmdLineArgs.add("-p");
    cmdLineArgs.add("test/config/testAuthOn.txt");

    CommandLineRunner runner = appCtx.getBean(CommandLineRunner.class);
    runner.run(cmdLineArgs.toArray(new String[cmdLineArgs.size()]));

    getSwaggerDocsTest();
    runMethodsNotAllowedAuthenticatedTest();
    getMdupdatesJobidAuthenticatedTest();
    getMdupdatesAuthenticatedTest();
    postMdupdatesAuthenticatedTest();
    deleteMdupdatesJobidAuthenticatedTest();
    deleteMdupdatesAuthenticatedTest();

    log.debug2("Done");
  }

  /**
   * Provides the standard command line arguments to start the server.
   * 
   * @return a List<String> with the command line arguments.
   * @throws IOException
   *           if there are problems.
   */
  private List<String> getCommandLineArguments() throws IOException {
    log.debug2("Invoked");

    List<String> cmdLineArgs = new ArrayList<String>();
    cmdLineArgs.add("-p");
    cmdLineArgs.add(getPlatformDiskSpaceConfigPath());
    cmdLineArgs.add("-p");
    cmdLineArgs.add("config/common.xml");

    File folder =
	new File(new File(new File(getTempDirPath()), "tdbxml"), "prod");
    log.trace("folder = {}", () -> folder);

    cmdLineArgs.add("-x");
    cmdLineArgs.add(folder.getAbsolutePath());
    cmdLineArgs.add("-p");
    cmdLineArgs.add("test/config/lockss.txt");
    cmdLineArgs.add("-p");
    cmdLineArgs.add(getUiPortConfigFile().getAbsolutePath());
    cmdLineArgs.add("-p");
    cmdLineArgs.add(getDbConfigFile().getAbsolutePath());
    cmdLineArgs.add("-p");
    cmdLineArgs.add(getRepositoryConfigFile().getAbsolutePath());
    cmdLineArgs.add("-p");
    cmdLineArgs.add("test/config/lockss.opt");

    log.debug2("cmdLineArgs = {}", () -> cmdLineArgs);
    return cmdLineArgs;
  }

  /**
   * Runs the Swagger-related tests.
   * 
   * @throws Exception
   *           if there are problems.
   */
  private void getSwaggerDocsTest() throws Exception {
    log.debug2("Invoked");

    ResponseEntity<String> successResponse = new TestRestTemplate().exchange(
	getTestUrlTemplate("/v2/api-docs"), HttpMethod.GET, null, String.class);

    HttpStatus statusCode = successResponse.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    String expectedBody = "{'swagger':'2.0','info':{'description':"
	+ "'REST API of the LOCKSS Metadata Extraction Service'"
        + "}}";

    JSONAssert.assertEquals(expectedBody, successResponse.getBody(), false);

    log.debug2("Done");
  }

  /**
   * Runs the invalid method-related un-authenticated-specific tests.
   */
  private void runMethodsNotAllowedUnAuthenticatedTest() {
    log.debug2("Invoked");

    // Missing job ID.
    runTestMethodNotAllowed(null, null, HttpMethod.PUT, HttpStatus.NOT_FOUND);

    // Empty job ID.
    runTestMethodNotAllowed(EMPTY_STRING, ANYBODY, HttpMethod.PATCH,
	HttpStatus.NOT_FOUND);

    // Unknown job ID.
    runTestMethodNotAllowed(UNKNOWN_JOBID, ANYBODY, HttpMethod.PUT,
	HttpStatus.METHOD_NOT_ALLOWED);

    runTestMethodNotAllowed(UNKNOWN_JOBID, null, HttpMethod.PATCH,
	HttpStatus.METHOD_NOT_ALLOWED);

    // Good AUId.
    runTestMethodNotAllowed("1", null, HttpMethod.PATCH,
	HttpStatus.METHOD_NOT_ALLOWED);

    runTestMethodNotAllowed("2", ANYBODY, HttpMethod.PUT,
	HttpStatus.METHOD_NOT_ALLOWED);

    runMethodsNotAllowedCommonTest();

    log.debug2("Done");
  }

  /**
   * Runs the invalid method-related authenticated-specific tests.
   */
  private void runMethodsNotAllowedAuthenticatedTest() {
    log.debug2("Invoked");

    // Missing job ID.
    runTestMethodNotAllowed(null, ANYBODY, HttpMethod.PUT,
	HttpStatus.UNAUTHORIZED);

    // Empty job ID.
    runTestMethodNotAllowed(EMPTY_STRING, null, HttpMethod.PATCH,
	HttpStatus.UNAUTHORIZED);

    // Unknown job ID.
    runTestMethodNotAllowed(UNKNOWN_JOBID, ANYBODY, HttpMethod.PUT,
	HttpStatus.UNAUTHORIZED);

    // No credentials.
    runTestMethodNotAllowed("1", null, HttpMethod.PATCH,
	HttpStatus.UNAUTHORIZED);

    // Bad credentials.
    runTestMethodNotAllowed("2", ANYBODY, HttpMethod.PUT,
	HttpStatus.UNAUTHORIZED);

    runMethodsNotAllowedCommonTest();

    log.debug2("Done");
  }

  /**
   * Runs the invalid method-related authentication-independent tests.
   */
  private void runMethodsNotAllowedCommonTest() {
    log.debug2("Invoked");

    // Missing job ID.
    runTestMethodNotAllowed(null, USER_ADMIN, HttpMethod.PUT,
	HttpStatus.NOT_FOUND);

    // Empty job ID.
    runTestMethodNotAllowed(EMPTY_STRING, CONTENT_ADMIN, HttpMethod.PATCH,
	HttpStatus.NOT_FOUND);

    // Unknown job ID.
    runTestMethodNotAllowed(UNKNOWN_JOBID, ACCESS_CONTENT, HttpMethod.PUT,
	HttpStatus.METHOD_NOT_ALLOWED);

    runTestMethodNotAllowed("1", USER_ADMIN, HttpMethod.PUT,
	HttpStatus.METHOD_NOT_ALLOWED);

    runTestMethodNotAllowed("2", CONTENT_ADMIN, HttpMethod.PATCH,
	HttpStatus.METHOD_NOT_ALLOWED);

    log.debug2("Done");
  }

  /**
   * Performs an operation using a method that is not allowed.
   * 
   * @param jobId
   *          A String with the identifier of the job.
   * @param credentials
   *          A Credentials with the request credentials.
   * @param method
   *          An HttpMethod with the request method.
   * @param expectedStatus
   *          An HttpStatus with the HTTP status of the result.
   */
  private void runTestMethodNotAllowed(String jobId, Credentials credentials,
      HttpMethod method, HttpStatus expectedStatus) {
    log.debug2("jobId = {}", jobId);
    log.debug2("credentials = {}", credentials);
    log.debug2("method = {}", method);
    log.debug2("expectedStatus = {}", expectedStatus);

    // Get the test URL template.
    String template = getTestUrlTemplate("/mdupdates/{jobid}");

    // Create the URI of the request to the REST service.
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(template)
	.build().expand(Collections.singletonMap("jobid", jobId));

    URI uri = UriComponentsBuilder.newInstance().uriComponents(uriComponents)
	.build().encode().toUri();
    log.trace("uri = {}", uri);

    // Initialize the request to the REST service.
    RestTemplate restTemplate = new RestTemplate();

    HttpEntity<String> requestEntity = null;

    // Get the individual credentials elements.
    String user = null;
    String password = null;

    if (credentials != null) {
      user = credentials.getUser();
      password = credentials.getPassword();
    }

    // Check whether there are any custom headers to be specified in the
    // request.
    if (user != null || password != null) {

      // Initialize the request headers.
      HttpHeaders headers = new HttpHeaders();

      // Set up the authentication credentials, if necessary.
      if (credentials != null) {
	credentials.setUpBasicAuthentication(headers);
      }

      log.trace("requestHeaders = {}", () -> headers.toSingleValueMap());

      // Create the request entity.
      requestEntity = new HttpEntity<String>(null, headers);
    }

    // Make the request and get the response. 
    ResponseEntity<String> response = new TestRestTemplate(restTemplate)
	.exchange(uri, method, requestEntity, String.class);

    // Get the response status.
    HttpStatus statusCode = response.getStatusCode();
    assertFalse(RestUtil.isSuccess(statusCode));
    assertEquals(expectedStatus, statusCode);
  }

  /**
   * Runs the getMdupdates()-related un-authenticated-specific tests.
   * 
   * @throws Exception
   *           if there are problems.
   */
  private void getMdupdatesUnAuthenticatedTest() throws Exception {
    log.debug2("Invoked");

    // Get all the test jobs.
    JobPageInfo result = runTestGetMdupdates(null, null, null, HttpStatus.OK);

    // Verify.
    assertEquals(new Integer(7), result.getPageInfo().getResultsPerPage());
    assertNull(result.getPageInfo().getTotalCount());
    assertNull(result.getPageInfo().getContinuationToken());
    assertTrue(result.getPageInfo().getCurLink()
	.startsWith(getTestUrlTemplate("")));

    List<Job> jobs = result.getJobs();
    assertEquals(7, jobs.size());

    Job job = jobs.get(0);
    assertEquals("1", job.getId());
    assertEquals("Job-1", job.getDescription());
    assertNotNull(job.getCreationDate());
    assertNotNull(job.getStartDate());
    assertNotNull(job.getEndDate());
    assertEquals(TEST_AUID_1, job.getAu().getId());
    assertEquals("ZooKeys Volume 2010", job.getAu().getName());
    assertEquals(5, job.getStatus().getCode().intValue());
    assertEquals("Success-1", job.getStatus().getMsg());

    job1 = job;

    job = jobs.get(1);
    assertEquals("2", job.getId());
    assertEquals("Job-2", job.getDescription());
    assertNotNull(job.getCreationDate());
    assertNotNull(job.getStartDate());
    assertNotNull(job.getEndDate());
    assertEquals(TEST_AUID_2, job.getAu().getId());
    assertEquals("ZooKeys Volume 2011", job.getAu().getName());
    assertEquals(5, job.getStatus().getCode().intValue());
    assertEquals("Success-2", job.getStatus().getMsg());

    job2 = job;

    job = jobs.get(2);
    assertEquals("3", job.getId());
    assertEquals("Job-3", job.getDescription());
    assertNotNull(job.getCreationDate());
    assertNotNull(job.getStartDate());
    assertNotNull(job.getEndDate());
    assertEquals(TEST_AUID_3, job.getAu().getId());
    assertEquals("ZooKeys Volume 2012", job.getAu().getName());
    assertEquals(5, job.getStatus().getCode().intValue());
    assertEquals("Success-3", job.getStatus().getMsg());

    job3 = job;

    job = jobs.get(3);
    assertEquals("4", job.getId());
    assertEquals("Job-4", job.getDescription());
    assertNotNull(job.getCreationDate());
    assertNotNull(job.getStartDate());
    assertNotNull(job.getEndDate());
    assertEquals(TEST_AUID_4, job.getAu().getId());
    assertEquals("ZooKeys Volume 2013", job.getAu().getName());
    assertEquals(5, job.getStatus().getCode().intValue());
    assertEquals("Success-4", job.getStatus().getMsg());

    job4 = job;

    job = jobs.get(4);
    assertEquals("5", job.getId());
    assertEquals("Job-5", job.getDescription());
    assertNotNull(job.getCreationDate());
    assertNotNull(job.getStartDate());
    assertNotNull(job.getEndDate());
    assertEquals(TEST_AUID_5, job.getAu().getId());
    assertEquals("ZooKeys Volume 2014", job.getAu().getName());
    assertEquals(5, job.getStatus().getCode().intValue());
    assertEquals("Success-5", job.getStatus().getMsg());

    job5 = job;

    job = jobs.get(5);
    assertEquals("6", job.getId());
    assertEquals("Job-6", job.getDescription());
    assertNotNull(job.getCreationDate());
    assertNotNull(job.getStartDate());
    assertNotNull(job.getEndDate());
    assertEquals(TEST_AUID_6, job.getAu().getId());
    assertEquals("ZooKeys Volume 2015", job.getAu().getName());
    assertEquals(5, job.getStatus().getCode().intValue());
    assertEquals("Success-6", job.getStatus().getMsg());

    job6 = job;

    job = jobs.get(6);
    assertEquals("7", job.getId());
    assertEquals("Job-7", job.getDescription());
    assertNotNull(job.getCreationDate());
    assertNotNull(job.getStartDate());
    assertNotNull(job.getEndDate());
    assertEquals(TEST_AUID_7, job.getAu().getId());
    assertEquals("ZooKeys Volume 2016", job.getAu().getName());
    assertEquals(5, job.getStatus().getCode().intValue());
    assertEquals("Success-7", job.getStatus().getMsg());

    job7 = job;

    // Pagination with no credentials.
    runTestGetMdupdatesPagination(null);

    // Pagination with bad credentials.
    runTestGetMdupdatesPagination(ANYBODY);

    getMdupdatesCommonTest();

    log.debug2("Done");
  }

  /**
   * Runs the getMdupdates()-related authenticated-specific tests.
   * 
   * @throws Exception
   *           if there are problems.
   */
  private void getMdupdatesAuthenticatedTest() throws Exception {
    log.debug2("Invoked");

    // No credentials.
    runTestGetMdupdates(null, null, null, HttpStatus.UNAUTHORIZED);

    // Bad credentials.
    runTestGetMdupdates(null, null, ANYBODY, HttpStatus.UNAUTHORIZED);

    // Get all the test jobs.
    JobPageInfo result =
	runTestGetMdupdates(null, null, USER_ADMIN, HttpStatus.OK);

    // Verify.
    assertEquals(new Integer(7), result.getPageInfo().getResultsPerPage());
    assertNull(result.getPageInfo().getTotalCount());
    assertNull(result.getPageInfo().getContinuationToken());
    assertTrue(result.getPageInfo().getCurLink()
	.startsWith(getTestUrlTemplate("")));

    List<Job> jobs = result.getJobs();
    assertEquals(7, jobs.size());

    Job job = jobs.get(0);
    assertEquals("1", job.getId());
    assertEquals("Job-1", job.getDescription());
    assertNotNull(job.getCreationDate());
    assertNotNull(job.getStartDate());
    assertNotNull(job.getEndDate());
    assertEquals(TEST_AUID_1, job.getAu().getId());
    assertEquals("ZooKeys Volume 2010", job.getAu().getName());
    assertEquals(5, job.getStatus().getCode().intValue());
    assertEquals("Success-1", job.getStatus().getMsg());

    job1 = job;

    job = jobs.get(1);
    assertEquals("2", job.getId());
    assertEquals("Job-2", job.getDescription());
    assertNotNull(job.getCreationDate());
    assertNotNull(job.getStartDate());
    assertNotNull(job.getEndDate());
    assertEquals(TEST_AUID_2, job.getAu().getId());
    assertEquals("ZooKeys Volume 2011", job.getAu().getName());
    assertEquals(5, job.getStatus().getCode().intValue());
    assertEquals("Success-2", job.getStatus().getMsg());

    job2 = job;

    job = jobs.get(2);
    assertEquals("3", job.getId());
    assertEquals("Job-3", job.getDescription());
    assertNotNull(job.getCreationDate());
    assertNotNull(job.getStartDate());
    assertNotNull(job.getEndDate());
    assertEquals(TEST_AUID_3, job.getAu().getId());
    assertEquals("ZooKeys Volume 2012", job.getAu().getName());
    assertEquals(5, job.getStatus().getCode().intValue());
    assertEquals("Success-3", job.getStatus().getMsg());

    job3 = job;

    job = jobs.get(3);
    assertEquals("4", job.getId());
    assertEquals("Job-4", job.getDescription());
    assertNotNull(job.getCreationDate());
    assertNotNull(job.getStartDate());
    assertNotNull(job.getEndDate());
    assertEquals(TEST_AUID_4, job.getAu().getId());
    assertEquals("ZooKeys Volume 2013", job.getAu().getName());
    assertEquals(5, job.getStatus().getCode().intValue());
    assertEquals("Success-4", job.getStatus().getMsg());

    job4 = job;

    job = jobs.get(4);
    assertEquals("5", job.getId());
    assertEquals("Job-5", job.getDescription());
    assertNotNull(job.getCreationDate());
    assertNotNull(job.getStartDate());
    assertNotNull(job.getEndDate());
    assertEquals(TEST_AUID_5, job.getAu().getId());
    assertEquals("ZooKeys Volume 2014", job.getAu().getName());
    assertEquals(5, job.getStatus().getCode().intValue());
    assertEquals("Success-5", job.getStatus().getMsg());

    job5 = job;

    job = jobs.get(5);
    assertEquals("6", job.getId());
    assertEquals("Job-6", job.getDescription());
    assertNotNull(job.getCreationDate());
    assertNotNull(job.getStartDate());
    assertNotNull(job.getEndDate());
    assertEquals(TEST_AUID_6, job.getAu().getId());
    assertEquals("ZooKeys Volume 2015", job.getAu().getName());
    assertEquals(5, job.getStatus().getCode().intValue());
    assertEquals("Success-6", job.getStatus().getMsg());

    job6 = job;

    job = jobs.get(6);
    assertEquals("7", job.getId());
    assertEquals("Job-7", job.getDescription());
    assertNotNull(job.getCreationDate());
    assertNotNull(job.getStartDate());
    assertNotNull(job.getEndDate());
    assertEquals(TEST_AUID_7, job.getAu().getId());
    assertEquals("ZooKeys Volume 2016", job.getAu().getName());
    assertEquals(5, job.getStatus().getCode().intValue());
    assertEquals("Success-7", job.getStatus().getMsg());

    job7 = job;

    getMdupdatesCommonTest();

    log.debug2("Done");
  }

  /**
   * Runs the getMdupdates()-related authentication-independent tests.
   * 
   * @throws Exception
   *           if there are problems.
   */
  private void getMdupdatesCommonTest() throws Exception {
    log.debug2("Invoked");

    // Pagination.
    runTestGetMdupdatesPagination(USER_ADMIN);
    runTestGetMdupdatesPagination(CONTENT_ADMIN);
    runTestGetMdupdatesPagination(ACCESS_CONTENT);

    log.debug2("Done");
  }

  /**
   * Performs pagination tests.
   * 
   * @param user
   *          A String with the request username.
   * @param password
   *          A String with the request password.
   * @throws Exception
   *           if there are problems.
   */
  private void runTestGetMdupdatesPagination(Credentials credentials)
      throws Exception {
    log.debug2("credentials = {}", () -> credentials);

    // Bad limit.
    int requestCount = -1;
    String continuationToken = null;
    runTestGetMdupdates(requestCount, continuationToken, credentials,
	HttpStatus.BAD_REQUEST);

    // Get all the jobs.
    JobContinuationToken expectedJct = null;
    verifyJobs(ListUtil.list(job1, job2, job3, job4, job5, job6, job7),
	expectedJct, runTestGetMdupdates(null, continuationToken, credentials,
	    HttpStatus.OK));

    // Get all the jobs.
    requestCount = 0;
    verifyJobs(ListUtil.list(job1, job2, job3, job4, job5, job6, job7),
	expectedJct, runTestGetMdupdates(requestCount, continuationToken,
	    credentials, HttpStatus.OK));

    // Ask for more jobs than there are.
    requestCount = 10;
    verifyJobs(ListUtil.list(job1, job2, job3, job4, job5, job6, job7),
	expectedJct, runTestGetMdupdates(requestCount, continuationToken,
	    credentials, HttpStatus.OK));

    // Get the first job.
    requestCount = 1;
    JobPageInfo jpi = runTestGetMdupdates(requestCount, continuationToken,
	credentials, HttpStatus.OK);

    continuationToken = jpi.getPageInfo().getContinuationToken();
    JobContinuationToken firstJct = new JobContinuationToken(continuationToken);
    Long queueTruncationTimestamp = firstJct.getQueueTruncationTimestamp();
    Long lastJobSeq = firstJct.getLastJobSeq();

    // The seven jobs have identifiers ranging from 1 to 7, both inclusive.
    assertEquals(1L, lastJobSeq.longValue());
    verifyJobs(ListUtil.list(job1), firstJct, jpi);

    // Get the next one.
    jpi = runTestGetMdupdates(requestCount, continuationToken, credentials,
	HttpStatus.OK);

    continuationToken = jpi.getPageInfo().getContinuationToken();
    expectedJct = new JobContinuationToken(queueTruncationTimestamp,
	lastJobSeq.longValue() + requestCount);
    verifyJobs(ListUtil.list(job2), expectedJct, jpi);
    lastJobSeq = expectedJct.getLastJobSeq();

    // Get the next two.
    requestCount = 2;
    jpi = runTestGetMdupdates(requestCount, continuationToken, credentials,
	HttpStatus.OK);

    continuationToken = jpi.getPageInfo().getContinuationToken();
    expectedJct = new JobContinuationToken(queueTruncationTimestamp,
	lastJobSeq.longValue() + requestCount);
    verifyJobs(ListUtil.list(job3, job4), expectedJct, jpi);

    // Get the next three (the rest).
    requestCount = 3;
    expectedJct = null;
    verifyJobs(ListUtil.list(job5, job6, job7), expectedJct,
	runTestGetMdupdates(requestCount, continuationToken, credentials,
	    HttpStatus.OK));

    // Get the first two items.
    requestCount = 2;
    continuationToken = null;
    jpi = runTestGetMdupdates(requestCount, continuationToken, credentials,
	HttpStatus.OK);

    continuationToken = jpi.getPageInfo().getContinuationToken();
    firstJct = new JobContinuationToken(continuationToken);
    lastJobSeq = firstJct.getLastJobSeq();
    assertEquals(2L, lastJobSeq.longValue());
    verifyJobs(ListUtil.list(job1, job2), firstJct, jpi);

    // Get the next three.
    requestCount = 3;
    jpi = runTestGetMdupdates(requestCount, continuationToken, credentials,
	HttpStatus.OK);

    continuationToken = jpi.getPageInfo().getContinuationToken();
    expectedJct = new JobContinuationToken(queueTruncationTimestamp,
	lastJobSeq.longValue() + requestCount);
    verifyJobs(ListUtil.list(job3, job4, job5), expectedJct, jpi);

    // Get the last (partial) page.
    requestCount = 4;
    expectedJct = null;
    verifyJobs(ListUtil.list(job6, job7), expectedJct,
	runTestGetMdupdates(requestCount, continuationToken, credentials,
	    HttpStatus.OK));

    // Get the first three items.
    requestCount = 3;
    continuationToken = null;
    jpi = runTestGetMdupdates(requestCount, continuationToken, credentials,
	HttpStatus.OK);

    continuationToken = jpi.getPageInfo().getContinuationToken();
    firstJct = new JobContinuationToken(continuationToken);
    lastJobSeq = firstJct.getLastJobSeq();
    assertEquals(3L, lastJobSeq.longValue());
    verifyJobs(ListUtil.list(job1, job2, job3), firstJct, jpi);

    // Get the next four (the rest).
    requestCount = 4;
    expectedJct = null;
    verifyJobs(ListUtil.list(job4, job5, job6, job7), expectedJct,
	runTestGetMdupdates(requestCount, continuationToken, credentials,
	    HttpStatus.OK));

    // Get the first four items.
    continuationToken = null;
    jpi = runTestGetMdupdates(requestCount, continuationToken, credentials,
	HttpStatus.OK);

    continuationToken = jpi.getPageInfo().getContinuationToken();
    firstJct = new JobContinuationToken(continuationToken);
    lastJobSeq = firstJct.getLastJobSeq();
    assertEquals(4L, lastJobSeq.longValue());
    verifyJobs(ListUtil.list(job1, job2, job3, job4), firstJct, jpi);

    // Get the last (partial) page.
    requestCount = 5;
    expectedJct = null;
    verifyJobs(ListUtil.list(job5, job6, job7), expectedJct,
	runTestGetMdupdates(requestCount, continuationToken, credentials,
	    HttpStatus.OK));

    // Get the first five items.
    continuationToken = null;
    jpi = runTestGetMdupdates(requestCount, continuationToken, credentials,
	HttpStatus.OK);

    continuationToken = jpi.getPageInfo().getContinuationToken();
    firstJct = new JobContinuationToken(continuationToken);
    lastJobSeq = firstJct.getLastJobSeq();
    assertEquals(5L, lastJobSeq.longValue());
    verifyJobs(ListUtil.list(job1, job2, job3, job4, job5), firstJct, jpi);

    // Get the last (partial) page.
    requestCount = 6;
    expectedJct = null;
    verifyJobs(ListUtil.list(job6, job7), expectedJct,
	runTestGetMdupdates(requestCount, continuationToken, credentials,
	    HttpStatus.OK));

    // Get the first six items.
    continuationToken = null;
    jpi = runTestGetMdupdates(requestCount, continuationToken, credentials,
	HttpStatus.OK);

    continuationToken = jpi.getPageInfo().getContinuationToken();
    firstJct = new JobContinuationToken(continuationToken);
    lastJobSeq = firstJct.getLastJobSeq();
    assertEquals(6L, lastJobSeq.longValue());
    verifyJobs(ListUtil.list(job1, job2, job3, job4, job5, job6), firstJct,
	jpi);

    // Get the last (partial) page.
    requestCount = 7;
    expectedJct = null;
    verifyJobs(ListUtil.list(job7), expectedJct,
	runTestGetMdupdates(requestCount, continuationToken, credentials,
	    HttpStatus.OK));

    // Get the first seven (all) items.
    continuationToken = null;
    verifyJobs(ListUtil.list(job1, job2, job3, job4, job5, job6, job7),
	expectedJct, runTestGetMdupdates(requestCount, continuationToken,
	    credentials, HttpStatus.OK));

    // Try to get the first one with an incorrect queue truncation timestamp in
    // the future.
    continuationToken = new JobContinuationToken(
	queueTruncationTimestamp + 1000000L, 0L)
	.toWebResponseContinuationToken();
    runTestGetMdupdates(requestCount, continuationToken,
	credentials, HttpStatus.CONFLICT);

    log.debug2("Done");
  }

  /**
   * Performs a GET operation for the list of existing jobs.
   * 
   * @param limit
   *          An Integer with the maximum number of jobs to be returned.
   * @param continuationToken
   *          A String with the continuation token of the next page of jobs to
   *          be returned.
   * @param credentials
   *          A Credentials with the request credentials.
   * @param expectedStatus
   *          An HttpStatus with the HTTP status of the result.
   * @return a JobPageInfo with the job status.
   * @throws Exception
   *           if there are problems.
   */
  private JobPageInfo runTestGetMdupdates(Integer limit,
      String continuationToken, Credentials credentials,
      HttpStatus expectedStatus) throws Exception {
    log.debug2("limit = {}", limit);
    log.debug2("continuationToken = {}", continuationToken);
    log.debug2("credentials = {}", () -> credentials);
    log.debug2("expectedStatus = {}", () -> expectedStatus);

    // Get the test URL template.
    String template = getTestUrlTemplate("/mdupdates");

    // Create the URI of the request to the REST service.
    UriComponents uriComponents =
	UriComponentsBuilder.fromUriString(template).build();

    UriComponentsBuilder ucb =
	UriComponentsBuilder.newInstance().uriComponents(uriComponents);

    if (limit != null) {
      ucb.queryParam("limit", limit);
    }

    if (continuationToken != null) {
      ucb.queryParam("continuationToken", continuationToken);
    }

    URI uri = ucb.build().encode().toUri();
    log.trace("uri = {}", () -> uri);

    // Initialize the request to the REST service.
    RestTemplate restTemplate = new RestTemplate();

    HttpEntity<String> requestEntity = null;

    // Get the individual credentials elements.
    String user = null;
    String password = null;

    if (credentials != null) {
      user = credentials.getUser();
      password = credentials.getPassword();
    }

    // Check whether there are any custom headers to be specified in the
    // request.
    if (user != null || password != null) {

      // Initialize the request headers.
      HttpHeaders headers = new HttpHeaders();

      // Set up the authentication credentials, if necessary.
      if (credentials != null) {
	credentials.setUpBasicAuthentication(headers);
      }

      log.trace("requestHeaders = {}", () -> headers.toSingleValueMap());

      // Create the request entity.
      requestEntity = new HttpEntity<String>(null, headers);
    }

    // Make the request and get the response. 
    ResponseEntity<String> response = new TestRestTemplate(restTemplate)
	.exchange(uri, HttpMethod.GET, requestEntity, String.class);

    // Get the response status.
    HttpStatus statusCode = response.getStatusCode();
    assertEquals(expectedStatus, statusCode);

    JobPageInfo result = null;

    if (RestUtil.isSuccess(statusCode)) {
      result = new ObjectMapper().readValue(response.getBody(),
	  JobPageInfo.class);
    }

    if (log.isDebug2Enabled()) log.debug2("result = {}", result);
    return result;
  }

  /**
   * Verifies that the passed job list matches the expected jobs.
   * 
   * @param expectedJobs
   *          A List<Job> with the expected jobs to be found.
   * @param expectedContinuationToken
   *          A JobContinuationToken with the expected continuation token
   *          returned.
   * @param jobPageInfo
   *          A JobPageInfo with the jobs to be verified.
   */
  private void verifyJobs(List<Job> expectedJobs,
      JobContinuationToken expectedContinuationToken, JobPageInfo jobPageInfo) {
    log.debug2("expectedJobs = {}", () -> expectedJobs);
    log.debug2("expectedContinuationToken = {}",
	() -> expectedContinuationToken);
    log.debug2("jobPageInfo = {}", () -> jobPageInfo);

    PageInfo pageInfo = jobPageInfo.getPageInfo();
    assertNull(pageInfo.getTotalCount());
    assertEquals(expectedJobs.size(), pageInfo.getResultsPerPage().intValue());

    if (expectedContinuationToken != null) {
      assertEquals(expectedContinuationToken.toWebResponseContinuationToken(),
	  pageInfo.getContinuationToken());
      assertEquals(expectedContinuationToken.getLastJobSeq().toString(),
	  jobPageInfo.getJobs().get(jobPageInfo.getJobs().size()-1).getId());
      assertTrue(pageInfo.getNextLink().startsWith(getTestUrlTemplate("")));
    } else {
      assertNull(pageInfo.getContinuationToken());
    }

    assertTrue(pageInfo.getCurLink().startsWith(getTestUrlTemplate("")));
    assertEquals(expectedJobs.size(), jobPageInfo.getJobs().size());

    for (int i = 0; i < expectedJobs.size(); i++) {
      assertEquals(expectedJobs.get(i), jobPageInfo.getJobs().get(i));
    }

    log.debug2("Done");
  }

  /**
   * Runs the getMdupdatesJobid()-related un-authenticated-specific tests.
   */
  private void getMdupdatesJobidUnAuthenticatedTest() throws Exception {
    log.debug2("Invoked");

    // Missing job ID.
    runTestGetMdupdatesJobid(null, null, HttpStatus.NOT_FOUND);
    runTestGetMdupdatesJobid(null, ANYBODY, HttpStatus.NOT_FOUND);

    // Empty job ID.
    runTestGetMdupdatesJobid(EMPTY_STRING, null, HttpStatus.NOT_FOUND);
    runTestGetMdupdatesJobid(EMPTY_STRING, ANYBODY, HttpStatus.NOT_FOUND);

    // Unknown job ID.
    runTestGetMdupdatesJobid(UNKNOWN_JOBID, null, HttpStatus.NOT_FOUND);
    runTestGetMdupdatesJobid(UNKNOWN_JOBID, ANYBODY, HttpStatus.NOT_FOUND);

    // Test jobs.
    assertEquals(new Status(5, "Success-1"),
	runTestGetMdupdatesJobid("1", null, HttpStatus.OK));

    assertEquals(new Status(5, "Success-2"),
	runTestGetMdupdatesJobid("2", ANYBODY, HttpStatus.OK));

    assertEquals(new Status(5, "Success-3"),
	runTestGetMdupdatesJobid("3", null, HttpStatus.OK));

    assertEquals(new Status(5, "Success-4"),
	runTestGetMdupdatesJobid("4", ANYBODY, HttpStatus.OK));

    assertEquals(new Status(5, "Success-5"),
	runTestGetMdupdatesJobid("5", null, HttpStatus.OK));

    assertEquals(new Status(5, "Success-6"),
	runTestGetMdupdatesJobid("6", ANYBODY, HttpStatus.OK));

    assertEquals(new Status(5, "Success-7"),
	runTestGetMdupdatesJobid("7", null, HttpStatus.OK));

    runTestGetMdupdatesJobid("8", ANYBODY, HttpStatus.NOT_FOUND);

    getMdupdatesJobidCommonTest();

    log.debug2("Done");
  }

  /**
   * Runs the getMdupdatesJobid()-related authenticated-specific tests.
   */
  private void getMdupdatesJobidAuthenticatedTest() throws Exception {
    log.debug2("Invoked");

    // Missing job ID.
    runTestGetMdupdatesJobid(null, null, HttpStatus.UNAUTHORIZED);
    runTestGetMdupdatesJobid(null, ANYBODY, HttpStatus.UNAUTHORIZED);

    // Empty job ID.
    runTestGetMdupdatesJobid(EMPTY_STRING, null, HttpStatus.UNAUTHORIZED);
    runTestGetMdupdatesJobid(EMPTY_STRING, ANYBODY, HttpStatus.UNAUTHORIZED);

    // Unknown job ID.
    runTestGetMdupdatesJobid(UNKNOWN_JOBID, null, HttpStatus.UNAUTHORIZED);
    runTestGetMdupdatesJobid(UNKNOWN_JOBID, ANYBODY, HttpStatus.UNAUTHORIZED);

    // Test jobs.
    runTestGetMdupdatesJobid("1", null, HttpStatus.UNAUTHORIZED);
    runTestGetMdupdatesJobid("2", ANYBODY, HttpStatus.UNAUTHORIZED);

    getMdupdatesJobidCommonTest();

    log.debug2("Done");
  }

  /**
   * Runs the getMdupdatesJobid()-related authentication-independent tests.
   */
  private void getMdupdatesJobidCommonTest() throws Exception {
    log.debug2("Invoked");

    // Missing job ID.
    runTestGetMdupdatesJobid(null, USER_ADMIN, HttpStatus.NOT_FOUND);

    // Empty job ID.
    runTestGetMdupdatesJobid(EMPTY_STRING, CONTENT_ADMIN, HttpStatus.NOT_FOUND);

    // Unknown job ID.
    runTestGetMdupdatesJobid(UNKNOWN_JOBID, ACCESS_CONTENT,
	HttpStatus.NOT_FOUND);

    // Test jobs.
    assertEquals(new Status(5, "Success-1"),
	runTestGetMdupdatesJobid("1", USER_ADMIN, HttpStatus.OK));

    assertEquals(new Status(5, "Success-2"),
	runTestGetMdupdatesJobid("2", CONTENT_ADMIN, HttpStatus.OK));

    assertEquals(new Status(5, "Success-3"),
	runTestGetMdupdatesJobid("3", ACCESS_CONTENT, HttpStatus.OK));

    assertEquals(new Status(5, "Success-4"),
	runTestGetMdupdatesJobid("4", USER_ADMIN, HttpStatus.OK));

    assertEquals(new Status(5, "Success-5"),
	runTestGetMdupdatesJobid("5", CONTENT_ADMIN, HttpStatus.OK));

    assertEquals(new Status(5, "Success-6"),
	runTestGetMdupdatesJobid("6", ACCESS_CONTENT, HttpStatus.OK));

    assertEquals(new Status(5, "Success-7"),
	runTestGetMdupdatesJobid("7", USER_ADMIN, HttpStatus.OK));

    runTestGetMdupdatesJobid("8", CONTENT_ADMIN, HttpStatus.NOT_FOUND);

    log.debug2("Done");
  }

  /**
   * Performs a GET operation for the status of a job.
   * 
   * @param jobId
   *          A String with the identifier of the job.
   * @param credentials
   *          A Credentials with the request credentials.
   * @param expectedHttpStatus
   *          An HttpStatus with the expected HTTP status of the result.
   * @return a Status with the job status.
   */
  private Status runTestGetMdupdatesJobid(String jobId, Credentials credentials,
      HttpStatus expectedHttpStatus)
	  throws Exception {
    log.debug2("jobId = {}", jobId);
    log.debug2("credentials = {}", () -> credentials);
    log.debug2("expectedHttpStatus = {}", () -> expectedHttpStatus);

    // Get the test URL template.
    String template = getTestUrlTemplate("/mdupdates/{jobid}");

    // Create the URI of the request to the REST service.
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(template)
	.build().expand(Collections.singletonMap("jobid", jobId));

    URI uri = UriComponentsBuilder.newInstance().uriComponents(uriComponents)
	.build().encode().toUri();
    log.trace("uri = {}", () -> uri);

    // Initialize the request to the REST service.
    RestTemplate restTemplate = new RestTemplate();

    HttpEntity<String> requestEntity = null;

    // Get the individual credentials elements.
    String user = null;
    String password = null;

    if (credentials != null) {
      user = credentials.getUser();
      password = credentials.getPassword();
    }

    // Check whether there are any custom headers to be specified in the
    // request.
    if (user != null || password != null) {

      // Initialize the request headers.
      HttpHeaders headers = new HttpHeaders();

      // Set up the authentication credentials, if necessary.
      if (credentials != null) {
	credentials.setUpBasicAuthentication(headers);
      }

      log.trace("requestHeaders = {}", () -> headers.toSingleValueMap());

      // Create the request entity.
      requestEntity = new HttpEntity<String>(null, headers);
    }

    // Make the request and get the response. 
    ResponseEntity<String> response = new TestRestTemplate(restTemplate)
	.exchange(uri, HttpMethod.GET, requestEntity, String.class);

    // Get the response status.
    HttpStatus statusCode = response.getStatusCode();
    assertEquals(expectedHttpStatus, statusCode);

    Status result = null;

    if (RestUtil.isSuccess(statusCode)) {
      result = new ObjectMapper().readValue(response.getBody(), Status.class);
    }

    if (log.isDebug2Enabled()) log.debug2("result = {}", result);
    return result;
  }

  /**
   * Runs the postMdupdates()-related un-authenticated-specific tests.
   */
  private void postMdupdatesUnAuthenticatedTest() throws Exception {
    log.debug2("Invoked");

    // Missing payload (This should return HttpStatus.BAD_REQUEST, but Spring
    // returns HttpStatus.UNSUPPORTED_MEDIA_TYPE).
    runTestPostMetadataAus(null, null, null, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    runTestPostMetadataAus(null, null, ANYBODY,
	HttpStatus.UNSUPPORTED_MEDIA_TYPE);

    // Missing AUId.
    runTestPostMetadataAus(null, MD_UPDATE_FULL_EXTRACTION, null,
	HttpStatus.BAD_REQUEST);
    runTestPostMetadataAus(null, MD_UPDATE_INCREMENTAL_EXTRACTION, ANYBODY,
	HttpStatus.BAD_REQUEST);

    // Empty AUId.
    runTestPostMetadataAus(EMPTY_STRING, MD_UPDATE_DELETE, null,
	HttpStatus.BAD_REQUEST);
    runTestPostMetadataAus(EMPTY_STRING, MD_UPDATE_FULL_EXTRACTION, ANYBODY,
	HttpStatus.BAD_REQUEST);

    // Missing update type.
    runTestPostMetadataAus(AUID_1, null, null, HttpStatus.BAD_REQUEST);
    runTestPostMetadataAus(AUID_2, null, ANYBODY, HttpStatus.BAD_REQUEST);

    // Empty update type.
    runTestPostMetadataAus(AUID_3, EMPTY_STRING, null,
	HttpStatus.BAD_REQUEST);
    runTestPostMetadataAus(AUID_1, EMPTY_STRING, ANYBODY,
	HttpStatus.BAD_REQUEST);

    // Unknown AUId.
    runTestPostMetadataAus(UNKNOWN_AUID, MD_UPDATE_INCREMENTAL_EXTRACTION, null,
	HttpStatus.NOT_FOUND);
    runTestPostMetadataAus(UNKNOWN_AUID, MD_UPDATE_DELETE, ANYBODY,
	HttpStatus.NOT_FOUND);

    // Bad update type.
    runTestPostMetadataAus(AUID_2, BAD_UPDATE_TYPE, null,
	HttpStatus.BAD_REQUEST);
    runTestPostMetadataAus(AUID_3, BAD_UPDATE_TYPE, ANYBODY,
	HttpStatus.BAD_REQUEST);

    Date beforeTestDate = new Date();

    // Full extraction with no credentials.
    Job job = runTestPostMetadataAus(AUID_1, MD_UPDATE_FULL_EXTRACTION,
	null, HttpStatus.ACCEPTED);

    assertEquals(AUID_1, job.getAu().getId());
    assertEquals(AU_NAME_1, job.getAu().getName());

    Date jobCreationDate = job.getCreationDate();
    assertNotNull(jobCreationDate);
    assertFalse(jobCreationDate.before(beforeTestDate));

    assertNull(job.getEndDate());

    String jobId = job.getId();
    assertNotNull(jobId);
    Long jobSeq = Long.parseLong(jobId);
    assertNotNull(jobSeq);

    waitForJobStatus(jobId, null, "Success");

    // Full extraction with bad credentials.
    job = runTestPostMetadataAus(AUID_2, MD_UPDATE_FULL_EXTRACTION,
	ANYBODY, HttpStatus.ACCEPTED);

    assertEquals(AUID_2, job.getAu().getId());
    assertEquals(AU_NAME_2, job.getAu().getName());

    jobCreationDate = job.getCreationDate();
    assertNotNull(jobCreationDate);
    assertFalse(jobCreationDate.before(beforeTestDate));

    assertNull(job.getEndDate());

    jobId = job.getId();
    assertNotNull(jobId);
    Long jobSeq2 = Long.parseLong(jobId);
    assertNotNull(jobSeq2);
    assertEquals((Long)(jobSeq + 1L), jobSeq2);

    waitForJobStatus(jobId, ANYBODY, "Success");

    beforeTestDate = new Date();

    // Incremental extraction with no credentials.
    job = runTestPostMetadataAus(AUID_3, MD_UPDATE_INCREMENTAL_EXTRACTION,
	null, HttpStatus.ACCEPTED);

    assertEquals(AUID_3, job.getAu().getId());
    assertEquals(AU_NAME_3, job.getAu().getName());

    jobCreationDate = job.getCreationDate();
    assertNotNull(jobCreationDate);
    assertFalse(jobCreationDate.before(beforeTestDate));

    assertNull(job.getEndDate());

    jobId = job.getId();
    assertNotNull(jobId);
    Long jobSeq3 = Long.parseLong(jobId);
    assertNotNull(jobSeq3);
    assertEquals((Long)(jobSeq2 + 1L), jobSeq3);

    waitForJobStatus(jobId, null, "Success");

    // Incremental extraction with bad credentials.
    job = runTestPostMetadataAus(AUID_1, MD_UPDATE_INCREMENTAL_EXTRACTION,
	ANYBODY, HttpStatus.ACCEPTED);

    assertEquals(AUID_1, job.getAu().getId());
    assertEquals(AU_NAME_1, job.getAu().getName());

    jobCreationDate = job.getCreationDate();
    assertNotNull(jobCreationDate);
    assertFalse(jobCreationDate.before(beforeTestDate));

    assertNull(job.getEndDate());

    jobId = job.getId();
    assertNotNull(jobId);
    Long jobSeq4 = Long.parseLong(jobId);
    assertNotNull(jobSeq4);
    assertEquals((Long)(jobSeq3 + 1L), jobSeq4);

    waitForJobStatus(jobId, ANYBODY, "Success");

    beforeTestDate = new Date();

    // Delete with no credentials.
    job = runTestPostMetadataAus(AUID_1, MD_UPDATE_DELETE, null,
	HttpStatus.ACCEPTED);

    assertEquals(AUID_1, job.getAu().getId());
    assertEquals(AU_NAME_1, job.getAu().getName());

    jobCreationDate = job.getCreationDate();
    assertNotNull(jobCreationDate);
    assertFalse(jobCreationDate.before(beforeTestDate));

    assertNull(job.getEndDate());

    jobId = job.getId();
    assertNotNull(jobId);
    Long jobSeq5 = Long.parseLong(jobId);
    assertNotNull(jobSeq5);
    assertEquals((Long)(jobSeq4 + 1L), jobSeq5);

    waitForJobStatus(jobId, null, "Success");

    // Delete with bad credentials.
    job = runTestPostMetadataAus(AUID_2, MD_UPDATE_DELETE, ANYBODY,
	HttpStatus.ACCEPTED);

    assertEquals(AUID_2, job.getAu().getId());
    assertEquals(AU_NAME_2, job.getAu().getName());

    jobCreationDate = job.getCreationDate();
    assertNotNull(jobCreationDate);
    assertFalse(jobCreationDate.before(beforeTestDate));

    assertNull(job.getEndDate());

    jobId = job.getId();
    assertNotNull(jobId);
    Long jobSeq6 = Long.parseLong(jobId);
    assertNotNull(jobSeq6);
    assertEquals((Long)(jobSeq5 + 1L), jobSeq6);

    waitForJobStatus(jobId, ANYBODY, "Success");

    postMdupdatesCommonTest();

    log.debug2("Done");
  }

  /**
   * Runs the postMdupdates()-related authenticated-specific tests.
   */
  private void postMdupdatesAuthenticatedTest() throws Exception {
    log.debug2("Invoked");

    // Missing payload.
    runTestPostMetadataAus(null, null, null, HttpStatus.UNAUTHORIZED);
    runTestPostMetadataAus(null, null, ANYBODY, HttpStatus.UNAUTHORIZED);

    // Missing AUId.
    runTestPostMetadataAus(null, MD_UPDATE_FULL_EXTRACTION, null,
	HttpStatus.UNAUTHORIZED);
    runTestPostMetadataAus(null, MD_UPDATE_INCREMENTAL_EXTRACTION, ANYBODY,
	HttpStatus.UNAUTHORIZED);

    // Empty AUId.
    runTestPostMetadataAus(EMPTY_STRING, MD_UPDATE_DELETE, null,
	HttpStatus.UNAUTHORIZED);
    runTestPostMetadataAus(EMPTY_STRING, MD_UPDATE_FULL_EXTRACTION, ANYBODY,
	HttpStatus.UNAUTHORIZED);

    // Missing update type.
    runTestPostMetadataAus(AUID_1, null, null, HttpStatus.UNAUTHORIZED);
    runTestPostMetadataAus(AUID_2, null, ANYBODY, HttpStatus.UNAUTHORIZED);

    // Empty update type.
    runTestPostMetadataAus(AUID_3, EMPTY_STRING, null,
	HttpStatus.UNAUTHORIZED);
    runTestPostMetadataAus(AUID_1, EMPTY_STRING, ANYBODY,
	HttpStatus.UNAUTHORIZED);

    // Unknown AUId.
    runTestPostMetadataAus(UNKNOWN_AUID, MD_UPDATE_INCREMENTAL_EXTRACTION, null,
	HttpStatus.UNAUTHORIZED);
    runTestPostMetadataAus(UNKNOWN_AUID, MD_UPDATE_DELETE, ANYBODY,
	HttpStatus.UNAUTHORIZED);

    // Bad update type.
    runTestPostMetadataAus(AUID_2, BAD_UPDATE_TYPE, null,
	HttpStatus.UNAUTHORIZED);
    runTestPostMetadataAus(AUID_3, BAD_UPDATE_TYPE, ANYBODY,
	HttpStatus.UNAUTHORIZED);

    // Full extraction.
    runTestPostMetadataAus(AUID_1, MD_UPDATE_FULL_EXTRACTION, null,
	HttpStatus.UNAUTHORIZED);
    runTestPostMetadataAus(AUID_2, MD_UPDATE_FULL_EXTRACTION, ANYBODY,
	HttpStatus.UNAUTHORIZED);

    // Incremental extraction.
    runTestPostMetadataAus(AUID_3, MD_UPDATE_INCREMENTAL_EXTRACTION, null,
	HttpStatus.UNAUTHORIZED);
    runTestPostMetadataAus(AUID_1, MD_UPDATE_INCREMENTAL_EXTRACTION,
	ANYBODY, HttpStatus.UNAUTHORIZED);

    // Delete.
    runTestPostMetadataAus(AUID_2, MD_UPDATE_DELETE, null,
	HttpStatus.UNAUTHORIZED);
    runTestPostMetadataAus(AUID_3, MD_UPDATE_DELETE, ANYBODY,
	HttpStatus.UNAUTHORIZED);

    postMdupdatesCommonTest();

    log.debug2("Done");
  }

  /**
   * Runs the postMdupdates()-related authentication-independent tests.
   */
  private void postMdupdatesCommonTest() throws Exception {
    log.debug2("Invoked");

    // Missing payload (This should return HttpStatus.BAD_REQUEST, but Spring
    // returns HttpStatus.UNSUPPORTED_MEDIA_TYPE).
    runTestPostMetadataAus(null, null, USER_ADMIN,
	HttpStatus.UNSUPPORTED_MEDIA_TYPE);

    // Missing AUId.
    runTestPostMetadataAus(null, MD_UPDATE_INCREMENTAL_EXTRACTION,
	CONTENT_ADMIN, HttpStatus.BAD_REQUEST);

    // Empty AUId.
    runTestPostMetadataAus(EMPTY_STRING, MD_UPDATE_FULL_EXTRACTION, USER_ADMIN,
	HttpStatus.BAD_REQUEST);

    // Missing update type.
    runTestPostMetadataAus(AUID_1, null, CONTENT_ADMIN,
	HttpStatus.BAD_REQUEST);

    // Empty update type.
    runTestPostMetadataAus(AUID_2, EMPTY_STRING, USER_ADMIN,
	HttpStatus.BAD_REQUEST);

    // Unknown AUId.
    runTestPostMetadataAus(UNKNOWN_AUID, MD_UPDATE_DELETE, CONTENT_ADMIN,
	HttpStatus.NOT_FOUND);

    // Bad update type.
    runTestPostMetadataAus(AUID_3, BAD_UPDATE_TYPE, USER_ADMIN,
	HttpStatus.BAD_REQUEST);

    Date beforeTestDate = new Date();

    // Full extraction.
    Job job = runTestPostMetadataAus(AUID_1, MD_UPDATE_FULL_EXTRACTION,
	CONTENT_ADMIN, HttpStatus.ACCEPTED);

    assertEquals(AUID_1, job.getAu().getId());
    assertEquals(AU_NAME_1, job.getAu().getName());

    Date jobCreationDate = job.getCreationDate();
    assertNotNull(jobCreationDate);
    assertFalse(jobCreationDate.before(beforeTestDate));

    assertNull(job.getEndDate());

    String jobId = job.getId();
    assertNotNull(jobId);
    Long jobSeq = Long.parseLong(jobId);
    assertNotNull(jobSeq);

    waitForJobStatus(jobId, CONTENT_ADMIN, "Success");

    beforeTestDate = new Date();

    // Incremental extraction.
    job = runTestPostMetadataAus(AUID_2, MD_UPDATE_INCREMENTAL_EXTRACTION,
	USER_ADMIN, HttpStatus.ACCEPTED);

    assertEquals(AUID_2, job.getAu().getId());
    assertEquals(AU_NAME_2, job.getAu().getName());

    jobCreationDate = job.getCreationDate();
    assertNotNull(jobCreationDate);
    assertFalse(jobCreationDate.before(beforeTestDate));

    assertNull(job.getEndDate());

    jobId = job.getId();
    assertNotNull(jobId);
    Long jobSeq2 = Long.parseLong(jobId);
    assertNotNull(jobSeq2);
    assertEquals((Long)(jobSeq + 1L), jobSeq2);

    waitForJobStatus(jobId, USER_ADMIN, "Success");

    beforeTestDate = new Date();

    // Delete.
    job = runTestPostMetadataAus(AUID_3, MD_UPDATE_DELETE, CONTENT_ADMIN,
	HttpStatus.ACCEPTED);

    assertEquals(AUID_3, job.getAu().getId());
    assertEquals(AU_NAME_3, job.getAu().getName());

    jobCreationDate = job.getCreationDate();
    assertNotNull(jobCreationDate);
    assertFalse(jobCreationDate.before(beforeTestDate));

    assertNull(job.getEndDate());

    jobId = job.getId();
    assertNotNull(jobId);
    Long jobSeq3 = Long.parseLong(jobId);
    assertNotNull(jobSeq3);
    assertEquals((Long)(jobSeq2 + 1L), jobSeq3);

    waitForJobStatus(jobId, CONTENT_ADMIN, "Success");

    log.debug2("Done");
  }

  /**
   * Performs a POST operation for the metadata of an Archival Unit.
   * 
   * @param auId
   *          A String with the identifier of the Archival Unit.
   * @param updateType
   *          A String with the type of metadata update.
   * @param credentials
   *          A Credentials with the request credentials.
   * @param expectedStatus
   *          An HttpStatus with the HTTP status of the result.
   * @return a Job with the details of the scheduled job.
   */
  private Job runTestPostMetadataAus(String auId, String updateType,
      Credentials credentials, HttpStatus expectedStatus) throws IOException {
    log.debug2("auId = {}", auId);
    log.debug2("updateType = {}", updateType);
    log.debug2("credentials = {}", () -> credentials);
    log.debug2("expectedStatus = {}", () -> expectedStatus);

    // Get the test URL template.
    String template = getTestUrlTemplate("/mdupdates");

    // Create the URI of the request to the REST service.
    UriComponents uriComponents =
	UriComponentsBuilder.fromUriString(template).build();

    URI uri = UriComponentsBuilder.newInstance().uriComponents(uriComponents)
	.build().encode().toUri();
    log.trace("uri = {}", () -> uri);

    // Initialize the request to the REST service.
    RestTemplate restTemplate = new RestTemplate();

    HttpEntity<MetadataUpdateSpec> requestEntity = null;

    // Get the individual credentials elements.
    String user = null;
    String password = null;

    if (credentials != null) {
      user = credentials.getUser();
      password = credentials.getPassword();
    }

    // Check whether there are any custom headers to be specified in the
    // request.
    if (auId != null || updateType != null || user != null || password != null)
    {
      // Initialize the payload.
      MetadataUpdateSpec metadataUpdateSpec = null;

      if (auId != null || updateType != null) {
	metadataUpdateSpec = new MetadataUpdateSpec();
	metadataUpdateSpec.setAuid(auId);
	metadataUpdateSpec.setUpdateType(updateType);
      }

      if (log.isTraceEnabled())
	log.trace("metadataUpdateSpec = {}", metadataUpdateSpec);

      HttpHeaders headers = null;

      if (user != null || password != null) {
	// Initialize the request headers.
	headers = new HttpHeaders();

	// Set up the authentication credentials, if necessary.
	if (credentials != null) {
	  credentials.setUpBasicAuthentication(headers);
	}

	if (log.isTraceEnabled())
	  log.trace("requestHeaders = {}", headers.toSingleValueMap());
      }

      // Create the request entity.
      requestEntity =
	  new HttpEntity<MetadataUpdateSpec>(metadataUpdateSpec, headers);
    }

    // The next call should use the Job class instead of the String class,
    // but Spring gets confused when errors are reported.
    // Make the request and get the response.
    ResponseEntity<String> response = new TestRestTemplate(restTemplate).
	exchange(uri, HttpMethod.POST, requestEntity, String.class);

    // Get the response status.
    HttpStatus statusCode = response.getStatusCode();
    assertEquals(expectedStatus, statusCode);

    Job result = null;

    if (RestUtil.isSuccess(statusCode)) {
      result = new ObjectMapper().readValue(response.getBody(), Job.class);
    }

    if (log.isDebug2Enabled()) log.debug2("result = {}", result);
    return result;
  }

  /**
   * Waits for a job to reach an expected status.
   * 
   * @param jobId
   *          A String with the identifier of the job.
   * @param credentials
   *          A Credentials with the request credentials.
   * @param expectedJobStatusPrefix
   *          A String with the expected job status first characters.
   */
  private void waitForJobStatus(String jobId, Credentials credentials,
      String expectedJobStatusPrefix) throws Exception {
    int tries = 0;
    String jobStatusMessage = "";

    while (tries < 10) {
      Status jobStatus =
	  runTestGetMdupdatesJobid(jobId, credentials, HttpStatus.OK);
      log.trace("jobStatus = {}", () -> jobStatus);

      jobStatusMessage = jobStatus.getMsg();

      if (jobStatusMessage != null
	  && jobStatusMessage.startsWith(expectedJobStatusPrefix)) {
	break;
      }

      try {
	Thread.sleep(1000);
      } catch (InterruptedException ie) {}

      tries++;
    }

    assertTrue(jobStatusMessage != null
	&& jobStatusMessage.startsWith(expectedJobStatusPrefix));
  }

  /**
   * Runs the deleteMdupdatesJobid()-related un-authenticated-specific tests.
   */
  private void deleteMdupdatesJobidUnAuthenticatedTest() throws Exception {
    log.debug2("Invoked");

    runTestDeleteMdupdatesJobid(null, null, HttpStatus.NOT_FOUND);
    runTestDeleteMdupdatesJobid(UNKNOWN_JOBID, ANYBODY, HttpStatus.NOT_FOUND);

    runTestDeleteMdupdatesJobid("1", ACCESS_CONTENT, HttpStatus.OK);
    runTestDeleteMdupdatesJobid("2", null, HttpStatus.OK);

    deleteMdupdatesJobidCommonTest();

    log.debug2("Done");
  }

  /**
   * Runs the deleteMdupdatesJobid()-related authenticated-specific tests.
   */
  private void deleteMdupdatesJobidAuthenticatedTest() throws Exception {
    log.debug2("Invoked");

    runTestDeleteMdupdatesJobid(null, null, HttpStatus.UNAUTHORIZED);
    runTestDeleteMdupdatesJobid(UNKNOWN_JOBID, ANYBODY,
	HttpStatus.UNAUTHORIZED);

    runTestDeleteMdupdatesJobid("1", USER_ADMIN, HttpStatus.OK);
    runTestDeleteMdupdatesJobid("2", CONTENT_ADMIN, HttpStatus.OK);

    deleteMdupdatesJobidCommonTest();

    log.debug2("Done");
  }

  /**
   * Runs the deleteMdupdatesJobId()-related authenticated-independent tests.
   */
  private void deleteMdupdatesJobidCommonTest() throws Exception {
    log.debug2("Invoked");

    runTestDeleteMdupdatesJobid(null, USER_ADMIN, HttpStatus.NOT_FOUND);
    runTestDeleteMdupdatesJobid(UNKNOWN_JOBID, CONTENT_ADMIN,
	HttpStatus.NOT_FOUND);

    runTestDeleteMdupdatesJobid("3", USER_ADMIN, HttpStatus.OK);
    runTestDeleteMdupdatesJobid("4", CONTENT_ADMIN, HttpStatus.OK);

    log.debug2("Done");
  }

  /**
   * Performs a DELETE operation for a job.
   * 
   * @param credentials
   *          A Credentials with the request credentials.
   * @param expectedStatus
   *          An HttpStatus with the HTTP status of the result.
   * @throws Exception
   *           if there are problems.
   */
  private void runTestDeleteMdupdatesJobid(String jobId,
      Credentials credentials, HttpStatus expectedStatus) throws Exception {
    log.debug2("jobId = {}", jobId);
    log.debug2("credentials = {}", () -> credentials);
    log.debug2("expectedStatus = {}", () -> expectedStatus);

    // Get the test URL template.
    String template = getTestUrlTemplate("/mdupdates/{jobid}");

    // Create the URI of the request to the REST service.
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(template)
	.build().expand(Collections.singletonMap("jobid", jobId));

    URI uri = UriComponentsBuilder.newInstance().uriComponents(uriComponents)
	.build().encode().toUri();
    log.trace("uri = {}", () -> uri);

    // Initialize the request to the REST service.
    RestTemplate restTemplate = new RestTemplate();

    HttpEntity<String> requestEntity = null;

    // Get the individual credentials elements.
    String user = null;
    String password = null;

    if (credentials != null) {
      user = credentials.getUser();
      password = credentials.getPassword();
    }

    // Check whether there are any custom headers to be specified in the
    // request.
    if (user != null || password != null) {

      // Initialize the request headers.
      HttpHeaders headers = new HttpHeaders();

      // Set up the authentication credentials, if necessary.
      if (credentials != null) {
	credentials.setUpBasicAuthentication(headers);
      }

      log.trace("requestHeaders = {}", () -> headers.toSingleValueMap());

      // Create the request entity.
      requestEntity = new HttpEntity<String>(null, headers);
    }

    // The next call should use the Integer class instead of the String class,
    // but Spring gets confused when errors are reported.
    // Make the request and get the response. 
    ResponseEntity<String> response = new TestRestTemplate(restTemplate).
	exchange(uri, HttpMethod.DELETE, requestEntity, String.class);

    // Get the response status.
    HttpStatus statusCode = response.getStatusCode();
    assertEquals(expectedStatus, statusCode);

    if (RestUtil.isSuccess(statusCode)) {
      // Verify the identity of the deleted job.
      Job job = new ObjectMapper().readValue(response.getBody(), Job.class);
      assertEquals(jobId, job.getId());

      // Verify that the job now does not exist anymore.
      runTestGetMdupdatesJobid(jobId, credentials, HttpStatus.NOT_FOUND);
    }

    log.debug2("Done");
  }

  /**
   * Runs the deleteMdupdates()-related un-authenticated-specific tests.
   */
  private void deleteMdupdatesUnAuthenticatedTest() throws Exception {
    log.debug2("Invoked");

    // Verify that there are 6 jobs in the queue, 5, 6 and 7 from the job queue
    // tests and one for each of the Archival Units with metadata.
    JobPageInfo allJobs = runTestGetMdupdates(6, null, null, HttpStatus.OK);
    assertEquals(6, allJobs.getJobs().size());
    assertNull(allJobs.getPageInfo().getContinuationToken());

    // Delete them all.
    runTestDeleteMdupdates(null, HttpStatus.OK, 6);

    // Verify that the job queue is empty.
    runTestDeleteMdupdates(ANYBODY, HttpStatus.OK, 0);
    runTestDeleteMdupdates(ACCESS_CONTENT, HttpStatus.OK, 0);

    deleteMdupdatesCommonTest();

    log.debug2("Done");
  }

  /**
   * Runs the deleteMdupdates()-related authenticated-specific tests.
   */
  private void deleteMdupdatesAuthenticatedTest() throws Exception {
    log.debug2("Invoked");

    runTestDeleteMdupdates(null, HttpStatus.UNAUTHORIZED, -1);
    runTestDeleteMdupdates(ANYBODY, HttpStatus.UNAUTHORIZED, -1);
    runTestDeleteMdupdates(ACCESS_CONTENT, HttpStatus.FORBIDDEN, -1);

    // Verify that there are 6 jobs in the queue, 5, 6 and 7 from the job queue
    // tests and one for each of the Archival Units with metadata.
    JobPageInfo allJobs =
	runTestGetMdupdates(6, null, USER_ADMIN, HttpStatus.OK);
    assertEquals(6, allJobs.getJobs().size());
    assertNull(allJobs.getPageInfo().getContinuationToken());

    // Delete them all.
    runTestDeleteMdupdates(USER_ADMIN, HttpStatus.OK, 6);

    // Verify that the job queue is empty.
    runTestDeleteMdupdates(CONTENT_ADMIN, HttpStatus.OK, 0);

    deleteMdupdatesCommonTest();

    log.debug2("Done");
  }

  /**
   * Runs the deleteMdupdates()-related authenticated-independent tests.
   */
  private void deleteMdupdatesCommonTest() throws Exception {
    log.debug2("Invoked");

    // Verify that the job queue is empty.
    runTestDeleteMdupdates(USER_ADMIN, HttpStatus.OK, 0);
    runTestDeleteMdupdates(CONTENT_ADMIN, HttpStatus.OK, 0);

    log.debug2("Done");
  }

  /**
   * Performs a DELETE operation for all the jobs.
   * 
   * @param credentials
   *          A Credentials with the request credentials.
   * @param expectedStatus
   *          An HttpStatus with the HTTP status of the result.
   * @param expectedDeletedCount
   *          An int with the count of expected jobs to be deleted.
   * @throws Exception
   *           if there are problems.
   */
  private void runTestDeleteMdupdates(Credentials credentials,
      HttpStatus expectedStatus, int expectedDeletedCount) throws Exception {
    log.debug2("credentials = {}", () -> credentials);
    log.debug2("expectedStatus = {}", () -> expectedStatus);
    log.debug2("expectedDeletedCount = {}", expectedDeletedCount);

    // Get the test URL template.
    String template = getTestUrlTemplate("/mdupdates");

    // Create the URI of the request to the REST service.
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(template)
	.build();

    URI uri = UriComponentsBuilder.newInstance().uriComponents(uriComponents)
	.build().encode().toUri();
    log.trace("uri = {}", () -> uri);

    // Initialize the request to the REST service.
    RestTemplate restTemplate = new RestTemplate();

    HttpEntity<String> requestEntity = null;

    // Get the individual credentials elements.
    String user = null;
    String password = null;

    if (credentials != null) {
      user = credentials.getUser();
      password = credentials.getPassword();
    }

    // Check whether there are any custom headers to be specified in the
    // request.
    if (user != null || password != null) {

      // Initialize the request headers.
      HttpHeaders headers = new HttpHeaders();

      // Set up the authentication credentials, if necessary.
      if (credentials != null) {
	credentials.setUpBasicAuthentication(headers);
      }

      log.trace("requestHeaders = {}", () -> headers.toSingleValueMap());

      // Create the request entity.
      requestEntity = new HttpEntity<String>(null, headers);
    }

    // The next call should use the Integer class instead of the String class,
    // but Spring gets confused when errors are reported.
    // Make the request and get the response. 
    ResponseEntity<String> response = new TestRestTemplate(restTemplate).
	exchange(uri, HttpMethod.DELETE, requestEntity, String.class);

    // Get the response status.
    HttpStatus statusCode = response.getStatusCode();
    assertEquals(expectedStatus, statusCode);

    if (RestUtil.isSuccess(statusCode)) {
      // Verify the count of deleted items.
      assertEquals(expectedDeletedCount, Integer.parseInt(response.getBody()));

      // Verify that the Archival Unit now does not exist anymore.
      assertTrue(runTestGetMdupdates(null, null, credentials, HttpStatus.OK)
	  .getJobs().isEmpty());
    }

    log.debug2("Done");
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

  /**
   * The database manager used to create the test metadata database.
   */
  private class TestDerbyMetadataDbManager extends MetadataDbManager {
    private String dbName = "MetadataDbManager";

    public TestDerbyMetadataDbManager() {
      super(true);
    }

    @Override
    protected String getDataSourceClassName(Configuration config) {
      return EmbeddedDataSource.class.getCanonicalName();
    }

    @Override
    protected String getDataSourceDatabaseName(Configuration config) {
      return getTempDirPath() + "/cache/db/" + dbName;
    }

    @Override
    protected String getVersionSubsystemName() {
      return dbName;
    }
  }

  /**
   * The database manager used to create the test job database.
   */
  private class TestDerbyJobDbManager extends JobDbManager {
    private String dbName = "JobDbManager";

    public TestDerbyJobDbManager() {
      super();
    }

    @Override
    protected String getDataSourceClassName(Configuration config) {
      return EmbeddedDataSource.class.getCanonicalName();
    }

    @Override
    protected String getDataSourceDatabaseName(Configuration config) {
      return getTempDirPath() + "/cache/db/" + dbName;
    }

    @Override
    protected String getVersionSubsystemName() {
      return dbName;
    }
  }
}
