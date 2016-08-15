/*

 Copyright (c) 2016 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.laaws.mdx.api.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import org.apache.log4j.Logger;
import org.lockss.app.LockssDaemon;
import org.lockss.job.JobAuStatus;
import org.lockss.laaws.mdx.api.ApiException;
import org.lockss.laaws.mdx.api.ApiResponseMessage;
import org.lockss.laaws.mdx.api.JobsApiService;
import org.lockss.laaws.mdx.api.NotFoundException;
import org.lockss.laaws.mdx.model.Job;
import org.lockss.laaws.mdx.model.JobPageInfo;
import org.lockss.laaws.mdx.model.PageInfo;
import org.lockss.laaws.mdx.model.Status;

/**
 * Provider of access to the AU metadata jobs.
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public class JobsApiServiceImpl extends JobsApiService {
  private static Logger log = Logger.getLogger(JobsApiServiceImpl.class);

  /**
   * Deletes all of the queued jobs and stops any processing and deletes any
   * active jobs.
   * 
   * @param securityContext
   *          A SecurityContext providing access to security related
   *          information.
   * @return a Response with any data that needs to be returned to the runtime.
   */
  @Override
  public Response deleteJobs(SecurityContext securityContext)
      throws ApiException {
    if (log.isDebugEnabled()) log.debug("Invoked");


    try {
      int removedCount =
	  LockssDaemon.getLockssDaemon().getJobManager().removeAllJobs();
      String message = "Count of all jobs deleted: " + removedCount;
      if (log.isDebugEnabled()) log.debug(message);
      ApiResponseMessage result =
	  new ApiResponseMessage(ApiResponseMessage.OK, message);

      return Response.ok().entity(result).build();
    } catch (Exception e) {
      String message = "Cannot deleteJobs()";
      log.error(message, e);
      throw new ApiException(1, message + ": " + e.getMessage());
    }
  }

  /**
   * Deletes the job for an AU given the AU identifier if it's queued and it
   * stops any processing and deletes it if it's active.
   * 
   * @param auid
   *          A String with the AU identifier.
   * @param securityContext
   *          A SecurityContext providing access to security related
   *          information.
   * @return a Response with any data that needs to be returned to the runtime.
   * @throws NotFoundException
   *           if the AU with the given identifier does not exist.
   */
  @Override
  public Response deleteJobsAuAuid(String auid, SecurityContext securityContext)
      throws NotFoundException {
    if (log.isDebugEnabled()) log.debug("auid = " + auid);

    try {
      JobAuStatus jobAuStatus =
	  LockssDaemon.getLockssDaemon().getJobManager().removeAuJob(auid);
      if (log.isDebugEnabled()) log.debug("jobAuStatus = " + jobAuStatus);

      if (jobAuStatus != null) {
	Job result = new Job(jobAuStatus);
	if (log.isDebugEnabled()) log.debug("result = " + result);
	
	return Response.ok().entity(result).build();
      }
    } catch (IllegalArgumentException iae) {
      String message = "No Archival Unit found for auid = '" + auid + "'";
      log.error(message);
      throw new NotFoundException(1, message);
    } catch (Exception e) {
      String message = "Cannot deleteJobsAuAuid() for auid = '" + auid + "'";
      log.error(message, e);
      throw new NotFoundException(1, message + ": " + e.getMessage());
    }

    String message = "Found no job for auid = '" + auid + "'";
    log.error(message);
    throw new NotFoundException(1, message);
  }

  /**
   * Deletes a job given the job identifier if it's queued and it stops any
   * processing and deletes it if it's active.
   * 
   * @param jobid
   *          A String with the job identifier.
   * @param securityContext
   *          A SecurityContext providing access to security related
   *          information.
   * @return a Response with any data that needs to be returned to the runtime.
   * @throws NotFoundException
   *           if the job with the given identifier does not exist.
   */
  @Override
  public Response deleteJobsJobid(String jobid, SecurityContext securityContext)
      throws NotFoundException {
    if (log.isDebugEnabled()) log.debug("jobid = " + jobid);

    try {
      JobAuStatus jobAuStatus =
	  LockssDaemon.getLockssDaemon().getJobManager().removeJob(jobid);
      if (log.isDebugEnabled()) log.debug("jobAuStatus = " + jobAuStatus);

      Job result = new Job(jobAuStatus);
      if (log.isDebugEnabled()) log.debug("result = " + result);

      return Response.ok().entity(result).build();
    } catch (IllegalArgumentException iae) {
      String message = "No job found for jobid = '" + jobid + "'";
      log.error(message);
      throw new NotFoundException(1, message);
    } catch (Exception e) {
      String message = "Cannot deleteJobsJobid() for jobid = '" + jobid + "'";
      log.error(message, e);
      throw new NotFoundException(1, message + ": " + e.getMessage());
    }
  }

  /**
   * Provides a list of existing jobs.
   * 
   * @param page
   *          An Integer with the index of the page to be returned.
   * @param limit
   *          An Integer with the maximum number of jobs to be returned.
   * @param securityContext
   *          A SecurityContext providing access to security related
   *          information.
   * @return a Response with any data that needs to be returned to the runtime.
   * @throws NotFoundException
   *           if the job with the given identifier does not exist.
   */
  @Override
  public Response getJob(Integer page, Integer limit,
      SecurityContext securityContext) throws NotFoundException {
    if (log.isDebugEnabled()) {
      log.debug("page = " + page);
      log.debug("limit = " + limit);
    }

    PageInfo pi = new PageInfo();

    URI baseUri = UriBuilder.fromUri(System.getProperty("LAAWS_MDX_SERVER_HOST",
	"http://localhost")).
	port(Integer.getInteger(System.getProperty("LAAWS_MDX_SERVER_PORT"), 8888)).
	build();

    String curLink = baseUri + "/jobs";
    String nextLink = baseUri + "/jobs";

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

    try {
      List<JobAuStatus> jobAuStatuses =
	  LockssDaemon.getLockssDaemon().getJobManager().getJobs(page, limit);
      if (log.isDebugEnabled()) log.debug("jobAuStatuses = " + jobAuStatuses);

      List<Job> jobs = new ArrayList<Job>();

      for (JobAuStatus jobAuStatus : jobAuStatuses) {
	jobs.add(new Job(jobAuStatus));
      }

      if (log.isDebugEnabled()) log.debug("jobs = " + jobs);
      result.setJobs(jobs);
    } catch (Exception e) {
      String message =
	  "Cannot getJob() for page = " + page + ", limit = " + limit;
      log.error(message, e);
      throw new NotFoundException(2, message + ": " + e.getMessage());
    }

    if (log.isDebugEnabled()) log.debug("result = " + result);

    return Response.ok().entity(result).build();
  }

  /**
   * Provides the job for an AU given the AU identifier.
   * 
   * @param auid
   *          A String with the AU identifier.
   * @param securityContext
   *          A SecurityContext providing access to security related
   *          information.
   * @return a Response with any data that needs to be returned to the runtime.
   * @throws NotFoundException
   *           if the AU with the given identifier does not exist.
   */
  @Override
  public Response getJobsAuAuid(String auid, SecurityContext securityContext)
      throws NotFoundException {
    if (log.isDebugEnabled()) log.debug("auid = " + auid);

    try {
      JobAuStatus jobAuStatus =
	  LockssDaemon.getLockssDaemon().getJobManager().getAuJob(auid);
      if (log.isDebugEnabled()) log.debug("jobAuStatus = " + jobAuStatus);

      Job result = new Job(jobAuStatus);
      if (log.isDebugEnabled()) log.debug("result = " + result);

      return Response.ok().entity(result).build();
    } catch (IllegalArgumentException iae) {
      String message = "No Archival Unit found for auid = '" + auid + "'";
      log.error(message);
      throw new NotFoundException(1, message);
    } catch (Exception e) {
      String message = "Cannot getJobsAuAuid() for auid = '" + auid + "'";
      log.error(message, e);
      throw new NotFoundException(1, message + ": " + e.getMessage());
    }
  }

  /**
   * Provides the status of a job given the job identifier.
   * 
   * @param jobid
   *          A String with the job identifier.
   * @param securityContext
   *          A SecurityContext providing access to security related
   *          information.
   * @return a Response with any data that needs to be returned to the runtime.
   * @throws NotFoundException
   *           if the job with the given identifier does not exist.
   */
  @Override
  public Response getJobsJobid(String jobid, SecurityContext securityContext)
      throws NotFoundException {
    if (log.isDebugEnabled()) log.debug("jobid = " + jobid);

    try {
      JobAuStatus jobAuStatus =
	  LockssDaemon.getLockssDaemon().getJobManager().getJobStatus(jobid);
      if (log.isDebugEnabled()) log.debug("jobAuStatus = " + jobAuStatus);

      Status result = new Status(jobAuStatus);
      if (log.isDebugEnabled()) log.debug("result = " + result);

      return Response.ok().entity(result).build();
    } catch (IllegalArgumentException iae) {
      String message = "No job found for jobid = '" + jobid + "'";
      log.error(message);
      throw new NotFoundException(1, message);
    } catch (Exception e) {
      String message = "Cannot getJobsJobid() for jobid = '" + jobid + "'";
      log.error(message, e);
      throw new NotFoundException(1, message + ": " + e.getMessage());
    }
  }
}
