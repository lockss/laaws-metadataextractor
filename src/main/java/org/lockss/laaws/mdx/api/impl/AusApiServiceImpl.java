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

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.apache.log4j.Logger;
import org.lockss.app.LockssApp;
//import org.lockss.app.LockssDaemon;
import org.lockss.job.JobAuStatus;
import org.lockss.job.JobManager;
import org.lockss.laaws.mdx.api.AusApiService;
import org.lockss.laaws.mdx.api.NotFoundException;
import org.lockss.laaws.mdx.model.Au;
import org.lockss.laaws.mdx.model.AuPageInfo;
import org.lockss.laaws.mdx.model.Job;
import org.lockss.laaws.mdx.model.PageInfo;

/**
 * Implementation of the base provider of access to the metadata of an AU.
 */
public class AusApiServiceImpl extends AusApiService {
  private static Logger log = Logger.getLogger(AusApiServiceImpl.class);

  /**
   * Deletes the metadata stored for an AU given the AU identifier.
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
  public Response deleteAuAuid(String auid, SecurityContext securityContext)
      throws NotFoundException {
    if (log.isDebugEnabled()) log.debug("auid = " + auid);

    try {
      //JobAuStatus jobAuStatus = LockssDaemon.getLockssDaemon().getJobManager().
	//  scheduleMetadataRemoval(auid);
      JobAuStatus jobAuStatus = getJobManager().scheduleMetadataRemoval(auid);
      if (log.isDebugEnabled()) log.debug("jobAuStatus = " + jobAuStatus);

      Job result = new Job(jobAuStatus);
      if (log.isDebugEnabled()) log.debug("result = " + result);

      return Response.ok().entity(result).build();
    } catch (IllegalArgumentException iae) {
      String message = "No Archival Unit found for auid = '" + auid + "'";
      log.error(message);
      throw new NotFoundException(1, message);
    } catch (Exception e) {
      String message = "Cannot deleteAuAuid() for auid = '" + auid + "'";
      log.error(message, e);
      throw new NotFoundException(3, message + ": " + e.getMessage());
    }
  }

  /**
   * Provides a list of existing AUs.
   * 
   * @param page
   *          An Integer with the index of the page to be returned.
   * @param limit
   *          An Integer with the maximum number of AUs to be returned.
   * @param request
   *          An HttpServletRequest providing access to the incoming request.
   * @param securityContext
   *          A SecurityContext providing access to security related
   *          information.
   * @return a Response with any data that needs to be returned to the runtime.
   * @throws NotFoundException
   *           if the AU with the given identifier does not exist.
   */
  @Override
  public Response getAu(Integer page, Integer limit,
      HttpServletRequest request, SecurityContext securityContext)
	  throws NotFoundException {
    if (log.isDebugEnabled()) {
      log.debug("page = " + page);
      log.debug("limit = " + limit);
    }

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

    AuPageInfo result = new AuPageInfo();
    result.setPageInfo(pi);

    try {
//      List<JobAuStatus> jobAuStatuses =
//	  LockssDaemon.getLockssDaemon().getJobManager().getAus(page, limit);
      List<JobAuStatus> jobAuStatuses = getJobManager().getAus(page, limit);
      if (log.isDebugEnabled()) log.debug("jobAuStatuses = " + jobAuStatuses);

      List<Au> aus = new ArrayList<Au>();

      for (JobAuStatus jobAuStatus : jobAuStatuses) {
	aus.add(new Au(jobAuStatus));
      }

      if (log.isDebugEnabled()) log.debug("aus = " + aus);
      result.setAus(aus);
    } catch (Exception e) {
      String message =
	  "Cannot getAu() for page = " + page + ", limit = " + limit;
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
  public Response getAuAuidJob(String auid, SecurityContext securityContext)
      throws NotFoundException {
    if (log.isDebugEnabled()) log.debug("auid = " + auid);

    try {
//      JobAuStatus jobAuStatus =
//	  LockssDaemon.getLockssDaemon().getJobManager().getAuJob(auid);
      JobAuStatus jobAuStatus = getJobManager().getAuJob(auid);
      if (log.isDebugEnabled()) log.debug("jobAuStatus = " + jobAuStatus);

      Job result = new Job(jobAuStatus);
      if (log.isDebugEnabled()) log.debug("result = " + result);

      return Response.ok().entity(result).build();
    } catch (IllegalArgumentException iae) {
      String message = "No Archival Unit found for auid = '" + auid + "'";
      log.error(message);
      throw new NotFoundException(1, message);
    } catch (Exception e) {
      String message = "Cannot getAuAuidJob() for auid = '" + auid + "'";
      log.error(message, e);
      throw new NotFoundException(1, message + ": " + e.getMessage());
    }
  }

  /**
   * Extracts and stores all or part of the metadata for an AU given the AU
   * identifier.
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
  public Response putAuAuid(String auid, SecurityContext securityContext)
      throws NotFoundException {
    if (log.isDebugEnabled()) log.debug("auid = " + auid);

    try {
//      JobAuStatus jobAuStatus = LockssDaemon.getLockssDaemon().getJobManager().
//	  scheduleMetadataExtraction(auid);
      JobAuStatus jobAuStatus =
	  getJobManager().scheduleMetadataExtraction(auid);
      if (log.isDebugEnabled()) log.debug("jobAuStatus = " + jobAuStatus);

      Job result = new Job(jobAuStatus);
      if (log.isDebugEnabled()) log.debug("result = " + result);

      return Response.ok().entity(result).build();
    } catch (IllegalArgumentException iae) {
      String message = "No Archival Unit found for auid = '" + auid + "'";
      log.error(message);
      throw new NotFoundException(1, message);
    } catch (Exception e) {
      String message = "Cannot putAuAuid() for auid = '" + auid + "'";
      log.error(message, e);
      throw new NotFoundException(1, message + ": " + e.getMessage());
    }
  }

  /**
   * Provides the job manager.
   * 
   * @return a JobManager with the job manager.
   */
  private JobManager getJobManager() {
    return (JobManager)LockssApp.getManager(JobManager.getManagerKey());
  }
}
