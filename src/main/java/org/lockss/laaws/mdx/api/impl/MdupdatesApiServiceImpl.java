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
package org.lockss.laaws.mdx.api.impl;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.apache.log4j.Logger;
import org.lockss.app.LockssDaemon;
import org.lockss.job.JobAuStatus;
import org.lockss.job.JobManager;
import org.lockss.laaws.mdx.api.ApiException;
import org.lockss.laaws.mdx.api.MdupdatesApiService;
import org.lockss.laaws.mdx.api.NotFoundException;
import org.lockss.laaws.mdx.model.Job;
import org.lockss.laaws.mdx.model.JobPageInfo;
import org.lockss.laaws.mdx.model.PageInfo;
import org.lockss.laaws.mdx.model.MetadataUpdateSpec;
import org.lockss.laaws.mdx.model.Status;

/**
 * Provider of access to the AU metadata jobs.
 */
public class MdupdatesApiServiceImpl extends MdupdatesApiService {
  private static Logger log = Logger.getLogger(MdupdatesApiServiceImpl.class);

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
  public Response deleteMdupdates(SecurityContext securityContext)
      throws ApiException {
    if (log.isDebugEnabled()) log.debug("Invoked");


    try {
      int removedCount = getJobManager().removeAllJobs();
      if (log.isDebugEnabled()) log.debug("removedCount = " + removedCount);

      return Response.ok().entity(removedCount).build();
    } catch (Exception e) {
      String message = "Cannot deleteMdupdates()";
      log.error(message, e);
      return getErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, message);
    }
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
  public Response deleteMdupdatesJobid(String jobid,
      SecurityContext securityContext) throws NotFoundException {
    if (log.isDebugEnabled()) log.debug("jobid = " + jobid);

    try {
      JobAuStatus jobAuStatus = getJobManager().removeJob(jobid);
      if (log.isDebugEnabled()) log.debug("jobAuStatus = " + jobAuStatus);

      Job result = new Job(jobAuStatus);
      if (log.isDebugEnabled()) log.debug("result = " + result);

      return Response.ok().entity(result).build();
    } catch (IllegalArgumentException iae) {
      String message = "No job found for jobid = '" + jobid + "'";
      log.error(message);
      return getErrorResponse(Response.Status.NOT_FOUND, message);
    } catch (Exception e) {
      String message =
	  "Cannot deleteMdupdatesJobid() for jobid = '" + jobid + "'";
      log.error(message, e);
      return getErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, message);
    }
  }

  /**
   * Provides a list of existing jobs.
   * 
   * @param page
   *          An Integer with the index of the page to be returned.
   * @param limit
   *          An Integer with the maximum number of jobs to be returned.
   * @param request
   *          An HttpServletRequest providing access to the incoming request.
   * @param securityContext
   *          A SecurityContext providing access to security related
   *          information.
   * @return a Response with any data that needs to be returned to the runtime.
   * @throws NotFoundException
   *           if the job with the given identifier does not exist.
   */
  @Override
  public Response getMdupdates(Integer page, Integer limit,
      HttpServletRequest request, SecurityContext securityContext)
	  throws NotFoundException {
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

      return Response.ok().entity(result).build();
    } catch (Exception e) {
      String message =
	  "Cannot getMdupdates() for page = " + page + ", limit = " + limit;
      log.error(message, e);
      return getErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, message);
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
  public Response getMdupdatesJobid(String jobid,
      SecurityContext securityContext) throws NotFoundException {
    if (log.isDebugEnabled()) log.debug("jobid = " + jobid);

    try {
      JobAuStatus jobAuStatus = getJobManager().getJobStatus(jobid);
      if (log.isDebugEnabled()) log.debug("jobAuStatus = " + jobAuStatus);

      Status result = new Status(jobAuStatus);
      if (log.isDebugEnabled()) log.debug("result = " + result);

      return Response.ok().entity(result).build();
    } catch (IllegalArgumentException iae) {
      String message = "No job found for jobid = '" + jobid + "'";
      log.error(message);
      return getErrorResponse(Response.Status.NOT_FOUND, message);
    } catch (Exception e) {
      String message = "Cannot getMdupdatesJobid() for jobid = '" + jobid + "'";
      log.error(message, e);
      return getErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, message);
    }
  }

  /**
   * Extracts and stores all or part of the metadata for an AU, or deletes the
   * metadata for an AU.
   * 
   * @param metadataUpdateSpec
   *          A MetadataUpdateSpec with the specification of the metadata update
   *          operation.
   * @param securityContext
   *          A SecurityContext providing access to security related
   *          information.
   * @return a Response with any data that needs to be returned to the runtime.
   * @throws NotFoundException
   *           if the AU with the given identifier does not exist.
   */
  @Override
  public Response postMdupdates(MetadataUpdateSpec metadataUpdateSpec,
      SecurityContext securityContext) throws NotFoundException {
    if (log.isDebugEnabled())
      log.debug("metadataUpdateSpec = " + metadataUpdateSpec);

    String auid = null;

    try {
      if (metadataUpdateSpec == null) {
	String message = "Invalid metadata update specification: null";
	log.error(message);
	return getErrorResponse(Response.Status.BAD_REQUEST, message);
      }

      auid = metadataUpdateSpec.getAuid();
      if (log.isDebugEnabled()) log.debug("auid = " + auid);

      if (auid == null || auid.isEmpty()) {
	String message = "Invalid auid = '" + auid + "'";
	log.error(message);
	return getErrorResponse(Response.Status.BAD_REQUEST, message);
      }

      String updateType = metadataUpdateSpec.getUpdateType();
      if (log.isDebugEnabled()) log.debug("updateType = " + updateType);

      if (updateType == null || updateType.isEmpty()) {
	String message = "Invalid updateType = '" + updateType + "'";
	log.error(message);
	return getErrorResponse(Response.Status.BAD_REQUEST, message);
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
	return getErrorResponse(Response.Status.BAD_REQUEST, message);
      }

      if (log.isDebugEnabled()) log.debug("jobAuStatus = " + jobAuStatus);

      Job result = new Job(jobAuStatus);
      if (log.isDebugEnabled()) log.debug("result = " + result);

      return Response.status(Response.Status.ACCEPTED).entity(result).build();
    } catch (IllegalArgumentException iae) {
      String message = "No Archival Unit found for auid = '" + auid + "'";
      log.error(message);
      return getErrorResponse(Response.Status.NOT_FOUND, message);
    } catch (Exception e) {
      String message = "Cannot postMdupdates() for metadataUpdateSpec = '"
	  + metadataUpdateSpec + "'";
      log.error(message, e);
      return getErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, message);
    }
  }

  /**
   * Provides the job manager.
   * 
   * @return a JobManager with the job manager.
   */
  private JobManager getJobManager() {
    return LockssDaemon.getLockssDaemon().getJobManager();
  }

  /**
   * Provides the appropriate response in case of an error.
   * 
   * @param statusCode
   *          A Response.Status with the error status code.
   * @param message
   *          A String with the error message.
   * @return a Response with the error response.
   */
  private Response getErrorResponse(Response.Status status, String message) {
    return Response.status(status).entity(toJsonMessage(message)).build();
  }

  /**
   * Formats to JSON any message to be returned.
   * 
   * @param message
   *          A String with the message to be formatted.
   * @return a String with the JSON-formatted message.
   */
  private String toJsonMessage(String message) {
    return "{\"message\":\"" + message + "\"}"; 
  }
}
