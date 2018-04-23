/*

Copyright (c) 2000-2018 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package org.lockss.laaws.mdx.api;

import io.swagger.annotations.ApiParam;
import java.lang.reflect.MalformedParametersException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.lockss.app.LockssApp;
import org.lockss.laaws.mdx.model.Job;
import org.lockss.laaws.mdx.model.JobPageInfo;
import org.lockss.laaws.mdx.model.PageInfo;
import org.lockss.laaws.mdx.model.MetadataUpdateSpec;
import org.lockss.laaws.mdx.model.Status;
import org.lockss.metadata.extractor.job.JobAuStatus;
import org.lockss.metadata.extractor.job.JobManager;
import org.lockss.spring.auth.Roles;
import org.lockss.spring.auth.SpringAuthenticationFilter;
import org.lockss.spring.status.ApiStatus;
import org.lockss.spring.status.SpringLockssBaseApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for access to the AU metadata jobs.
 */
@RestController
public class MdupdatesApiController extends SpringLockssBaseApiController
    implements MdupdatesApi {
  private static Logger log = Logger.getLogger(MdupdatesApiController.class);

  @Autowired
  private HttpServletRequest request;

  /**
   * Deletes all of the queued jobs and stops any processing and deletes any
   * active jobs.
   * 
   * @return a {@code ResponseEntity<Integer>} with the count of jobs deleted.
   */
  @Override
  @RequestMapping(value = "/mdupdates",
  produces = { "application/json" }, consumes = { "application/json" },
  method = RequestMethod.DELETE)
  public ResponseEntity<Integer> deleteMdupdates() {
    if (log.isDebugEnabled()) log.debug("Invoked");

    SpringAuthenticationFilter.checkAuthorization(Roles.ROLE_CONTENT_ADMIN);

    try {
      int removedCount = getJobManager().removeAllJobs();
      if (log.isDebugEnabled()) log.debug("removedCount = " + removedCount);

      return new ResponseEntity<Integer>(removedCount, HttpStatus.OK);
    } catch (Exception e) {
      String message = "Cannot deleteMdupdates()";
      log.error(message, e);
      throw new RuntimeException(message);
    }
  }

  /**
   * Deletes a job given the job identifier if it's queued and it stops any
   * processing and deletes it if it's active.
   * 
   * @param jobid
   *          A String with the job identifier.
   * @return a {@code ResponseEntity<Job>} with information about the deleted
   *         job.
   */
  @Override
  @RequestMapping(value = "/mdupdates/{jobid}",
  produces = { "application/json" }, consumes = { "application/json" },
  method = RequestMethod.DELETE)
  public ResponseEntity<Job> deleteMdupdatesJobid(@PathVariable("jobid")
  String jobid) {
    if (log.isDebugEnabled()) log.debug("jobid = " + jobid);

    SpringAuthenticationFilter.checkAuthorization(Roles.ROLE_CONTENT_ADMIN);

    try {
      JobAuStatus jobAuStatus = getJobManager().removeJob(jobid);
      if (log.isDebugEnabled()) log.debug("jobAuStatus = " + jobAuStatus);

      Job result = new Job(jobAuStatus);
      if (log.isDebugEnabled()) log.debug("result = " + result);

      return new ResponseEntity<Job>(result, HttpStatus.OK);
    } catch (IllegalArgumentException iae) {
      String message = "No job found for jobid = '" + jobid + "'";
      log.error(message);
      throw new IllegalArgumentException(message);
    } catch (Exception e) {
      String message =
	  "Cannot deleteMdupdatesJobid() for jobid = '" + jobid + "'";
      log.error(message, e);
      throw new RuntimeException(message);
    }
  }

  /**
   * Provides a list of existing jobs.
   * 
   * @param page
   *          An Integer with the index of the page to be returned.
   * @param limit
   *          An Integer with the maximum number of jobs to be returned.
   * @return a {@code ResponseEntity<JobPageInfo>} with the list of jobs.
   */
  @Override
  public ResponseEntity<JobPageInfo> getMdupdates(@RequestParam(value = "page",
	 required = false, defaultValue="1") Integer page,
	 @RequestParam(value = "limit", required = false, defaultValue="50")
	 Integer limit) {
    if (log.isDebugEnabled()) {
      log.debug("page = " + page);
      log.debug("limit = " + limit);
    }

    try {
      PageInfo pi = new PageInfo();

      String curLink = request.getRequestURL().toString();
      String nextLink = curLink;

      if (page != null) {
	curLink = curLink + "?page=" + page;
	nextLink = nextLink + "?page=" + (page + 1);

	if (limit != null) {
	  curLink = curLink + "&limit=" + limit;
	  nextLink = nextLink + "&limit=" + limit;
	}
      } else if (limit != null) {
	curLink = curLink + "?limit=" + limit;
	nextLink = nextLink + "?limit=" + limit;
      }

      if (log.isDebugEnabled()) {
	log.debug("curLink = " + curLink);
	log.debug("nextLink = " + nextLink);
      }

      pi.setCurLink(curLink);
      pi.setNextLink(nextLink);
      pi.setCurrentPage(page);
      pi.setResultsPerPage(limit);

      JobPageInfo result = new JobPageInfo();
      result.setPageInfo(pi);

      List<JobAuStatus> jobAuStatuses = getJobManager().getJobs(page, limit);
      if (log.isDebugEnabled()) log.debug("jobAuStatuses = " + jobAuStatuses);

      List<Job> jobs = new ArrayList<Job>();

      for (JobAuStatus jobAuStatus : jobAuStatuses) {
	jobs.add(new Job(jobAuStatus));
      }

      if (log.isDebugEnabled()) log.debug("jobs = " + jobs);
      result.setJobs(jobs);
      if (log.isDebugEnabled()) log.debug("result = " + result);

      return new ResponseEntity<JobPageInfo>(result, HttpStatus.OK);
    } catch (Exception e) {
      String message =
	  "Cannot getMdupdates() for page = " + page + ", limit = " + limit;
      log.error(message, e);
      throw new RuntimeException(message);
    }
  }

  /**
   * Provides the status of a job given the job identifier.
   * 
   * @param jobid
   *          A String with the job identifier.
   * @return a {@code ResponseEntity<Status>} with the job information.
   */
  @Override
  @RequestMapping(value = "/mdupdates/{jobid}",
  produces = { "application/json" },
  method = RequestMethod.GET)
  public ResponseEntity<Status> getMdupdatesJobid(@PathVariable("jobid")
      String jobid) {
    if (log.isDebugEnabled()) log.debug("jobid = " + jobid);

    try {
      JobAuStatus jobAuStatus = getJobManager().getJobStatus(jobid);
      if (log.isDebugEnabled()) log.debug("jobAuStatus = " + jobAuStatus);

      Status result = new Status(jobAuStatus);
      if (log.isDebugEnabled()) log.debug("result = " + result);

      return new ResponseEntity<Status>(result, HttpStatus.OK);
    } catch (IllegalArgumentException iae) {
      String message = "No job found for jobid = '" + jobid + "'";
      log.error(message);
      throw new IllegalArgumentException(message);
    } catch (Exception e) {
      String message = "Cannot getMdupdatesJobid() for jobid = '" + jobid + "'";
      log.error(message, e);
      throw new RuntimeException(message);
    }
  }

  /**
   * Extracts and stores all or part of the metadata for an AU, or deletes the
   * metadata for an AU.
   * 
   * @param metadataUpdateSpec
   *          A MetadataUpdateSpec with the specification of the metadata update
   *          operation.
   * @return a {@code ResponseEntity<Job>} with the information of the job
   *         created.
   */
  @Override
  @RequestMapping(value = "/mdupdates",
  produces = { "application/json" }, consumes = { "application/json" },
  method = RequestMethod.POST)
  public ResponseEntity<Job> postMdupdates(@ApiParam(required=true) @RequestBody
      MetadataUpdateSpec metadataUpdateSpec) {
    if (log.isDebugEnabled())
      log.debug("metadataUpdateSpec = " + metadataUpdateSpec);

    SpringAuthenticationFilter.checkAuthorization(Roles.ROLE_CONTENT_ADMIN);
    String auid = null;

    try {
      if (metadataUpdateSpec == null) {
	String message = "Invalid metadata update specification: null";
	log.error(message);
	throw new MalformedParametersException(message);
      }

      auid = metadataUpdateSpec.getAuid();
      if (log.isDebugEnabled()) log.debug("auid = " + auid);

      if (auid == null || auid.isEmpty()) {
	String message = "Invalid auid = '" + auid + "'";
	log.error(message);
	throw new MalformedParametersException(message);
      }

      String updateType = metadataUpdateSpec.getUpdateType();
      if (log.isDebugEnabled()) log.debug("updateType = " + updateType);

      if (updateType == null || updateType.isEmpty()) {
	String message = "Invalid updateType = '" + updateType + "'";
	log.error(message);
	throw new MalformedParametersException(message);
      }

      String canonicalUpdateType = updateType.toLowerCase();
      if (log.isDebugEnabled())
	log.debug("canonicalUpdateType = " + canonicalUpdateType);

      JobAuStatus jobAuStatus = null;

      if (canonicalUpdateType.equals(MD_UPDATE_FULL_EXTRACTION)) {
	jobAuStatus = getJobManager().scheduleMetadataExtraction(auid, true);
      } else if (canonicalUpdateType.equals(MD_UPDATE_INCREMENTAL_EXTRACTION)) {
	jobAuStatus = getJobManager().scheduleMetadataExtraction(auid, false);
      } else if (canonicalUpdateType.equals(MD_UPDATE_DELETE)) {
	jobAuStatus = getJobManager().scheduleMetadataRemoval(auid);
      } else {
	String message = "Invalid updateType = '" + updateType + "'";
	log.error(message);
	throw new MalformedParametersException(message);
      }

      if (log.isDebugEnabled()) log.debug("jobAuStatus = " + jobAuStatus);

      Job result = new Job(jobAuStatus);
      if (log.isDebugEnabled()) log.debug("result = " + result);

      return new ResponseEntity<Job>(result, HttpStatus.ACCEPTED);
    } catch (IllegalArgumentException iae) {
      String message = "No Archival Unit found for auid = '" + auid + "'";
      log.error(message);
      throw new IllegalArgumentException(message);
    } catch (Exception e) {
      String message = "Cannot postMdupdates() for metadataUpdateSpec = '"
	  + metadataUpdateSpec + "'";
      log.error(message, e);
      throw new RuntimeException(message);
    }
  }

  @ExceptionHandler(AccessControlException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorResponse authorizationExceptionHandler(AccessControlException e) {
    return new ErrorResponse(e.getMessage()); 	
  }

  @ExceptionHandler(MalformedParametersException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse authorizationExceptionHandler(
      MalformedParametersException e) {
    return new ErrorResponse(e.getMessage()); 	
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse notFoundExceptionHandler(IllegalArgumentException e) {
    return new ErrorResponse(e.getMessage()); 	
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse internalExceptionHandler(RuntimeException e) {
    return new ErrorResponse(e.getMessage()); 	
  }

  private static final String API_VERSION = "1.0.0";

  /**
   * Provides the status object.
   * 
   * @return an ApiStatus with the status.
   */
  @Override
  public ApiStatus getApiStatus() {
    return new ApiStatus()
      .setVersion(API_VERSION)
      .setReady(LockssApp.getLockssApp().isAppRunning());
  }

  /**
   * Provides the job manager.
   * 
   * @return a JobManager with the job manager.
   */
  private JobManager getJobManager() {
    return LockssApp.getManagerByTypeStatic(JobManager.class);
  }
}
