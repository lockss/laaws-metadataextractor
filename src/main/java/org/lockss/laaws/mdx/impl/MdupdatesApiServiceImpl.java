/*

Copyright (c) 2000-2020 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.laaws.mdx.impl;

import static org.lockss.servlet.DebugPanel.ACTION_FORCE_REINDEX_METADATA;
import static org.lockss.servlet.DebugPanel.ACTION_REINDEX_METADATA;
import static org.lockss.util.rest.MetadataExtractorConstants.*;
import java.security.AccessControlException;
import java.util.ConcurrentModificationException;
import javax.servlet.http.HttpServletRequest;
import org.lockss.account.UserAccount;
import org.lockss.app.LockssApp;
import org.lockss.app.LockssDaemon;
import org.lockss.laaws.mdx.api.MdupdatesApiDelegate;
import org.lockss.laaws.mdx.model.JobPageInfo;
import org.lockss.laaws.mdx.model.PageInfo;
import org.lockss.log.L4JLogger;
import org.lockss.metadata.extractor.MetadataExtractorManager;
import org.lockss.metadata.extractor.job.Job;
import org.lockss.metadata.extractor.job.JobAuStatus;
import org.lockss.metadata.extractor.job.JobContinuationToken;
import org.lockss.metadata.extractor.job.JobManager;
import org.lockss.metadata.extractor.job.JobPage;
import org.lockss.metadata.extractor.job.Status;
import org.lockss.plugin.ArchivalUnit;
import org.lockss.plugin.AuUtil;
import org.lockss.servlet.DebugPanel;
import org.lockss.spring.auth.Roles;
import org.lockss.spring.auth.SpringAuthenticationFilter;
import org.lockss.spring.base.BaseSpringApiServiceImpl;
import org.lockss.state.AuState;
import org.lockss.util.rest.mdx.MetadataUpdateSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Controller for access to the AU metadata jobs.
 */
@Service
public class MdupdatesApiServiceImpl extends BaseSpringApiServiceImpl
    implements MdupdatesApiDelegate {
  private static final L4JLogger log = L4JLogger.getLogger();

  static final String USE_FORCE_MESSAGE =
      "Use the 'force=true' query parameter to override.";

  @Autowired
  private HttpServletRequest request;

  /**
   * Deletes all of the queued jobs and stops any processing and deletes any
   * active jobs.
   * 
   * @return a {@code ResponseEntity<Integer>} with the count of jobs deleted.
   */
  @Override
  public ResponseEntity<Integer> deleteMdupdates() {
    log.debug2("Invoked");

    // Check whether the service has not been fully initialized.
    if (!waitReady()) {
      // Yes: Notify the client.
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Check authorization.
    try {
      SpringAuthenticationFilter.checkAuthorization(Roles.ROLE_CONTENT_ADMIN);
    } catch (AccessControlException ace) {
      log.warn(ace.getMessage());
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    try {
      int removedCount = getJobManager().removeAllJobs();
      log.trace("removedCount = {}", removedCount);

      return new ResponseEntity<Integer>(removedCount, HttpStatus.OK);
    } catch (Exception e) {
      String message = "Cannot deleteMdupdates()";
      log.error(message, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
  public ResponseEntity<Job> deleteMdupdatesJobid(String jobid) {
    log.debug2("jobid = {}", jobid);

    // Check whether the service has not been fully initialized.
    if (!waitReady()) {
      // Yes: Notify the client.
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Check authorization.
    try {
      SpringAuthenticationFilter.checkAuthorization(Roles.ROLE_CONTENT_ADMIN);
    } catch (AccessControlException ace) {
      log.warn(ace.getMessage());
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    try {
      JobAuStatus jobAuStatus = getJobManager().removeJob(jobid);
      log.trace("jobAuStatus = {}", () -> jobAuStatus);

      Job result = new Job(jobAuStatus);
      log.trace("result = {}", () -> result);

      return new ResponseEntity<Job>(result, HttpStatus.OK);
    } catch (IllegalArgumentException iae) {
      String message = "No job found for jobid = '" + jobid + "'";
      log.warn(message, iae);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      String message =
	  "Cannot deleteMdupdatesJobid() for jobid = '" + jobid + "'";
      log.error(message, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Provides a list of all currently active jobs or a pageful of the list
   * defined by the continuation token and size.
   * 
   * @param limit
   *          An Integer with the maximum number of jobs to be returned.
   * @param continuationToken
   *          A String with the continuation token of the next page of jobs to
   *          be returned.
   * @return a {@code ResponseEntity<JobPageInfo>} with the list of jobs.
   */
  @Override
  public ResponseEntity<JobPageInfo> getMdupdates(Integer limit,
      String continuationToken) {
    log.debug2("limit = {}", limit);
    log.debug2("continuationToken = {}", continuationToken);

    // Check whether the service has not been fully initialized.
    if (!waitReady()) {
      // Yes: Notify the client.
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    if (limit == null || limit.intValue() < 0) {
      String message =
	  "Limit of requested items must be a non-negative integer; it was '"
	      + limit + "'";
	log.warn(message);
	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Parse the request continuation token.
    JobContinuationToken jct = null;

    try {
      jct = new JobContinuationToken(continuationToken);
    } catch (IllegalArgumentException iae) {
      String message = "Invalid continuation token '" + continuationToken + "'";
      log.warn(message, iae);
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    try {
      // Get the pageful of results.
      JobPage jobPage = getJobManager().getJobs(limit, jct);
      log.trace("jobPage = {}", () -> jobPage);

      JobPageInfo result = new JobPageInfo();
      PageInfo pi = new PageInfo();
      result.setPageInfo(pi);

      StringBuffer curLinkBuffer = new StringBuffer(
	  request.getRequestURL().toString()).append("?limit=").append(limit);

      if (continuationToken != null) {
	curLinkBuffer.append("&continuationToken=").append(continuationToken);
      }

      if (log.isTraceEnabled())
	log.trace("curLink = {}", curLinkBuffer.toString());

      pi.setCurLink(curLinkBuffer.toString());
      pi.setResultsPerPage(jobPage.getJobs().size());

      // Check whether there is a response continuation token.
      if (jobPage.getContinuationToken() != null) {
	// Yes.
	pi.setContinuationToken(jobPage.getContinuationToken()
	    .toWebResponseContinuationToken());

	String nextLink = request.getRequestURL().toString() + "?limit=" + limit
	    + "&continuationToken=" + pi.getContinuationToken();
	log.trace("nextLink = {}", nextLink);

	pi.setNextLink(nextLink);
      }

      result.setJobs(jobPage.getJobs());

      log.debug2("result = {}", () -> result);
      return new ResponseEntity<JobPageInfo>(result, HttpStatus.OK);
    } catch (ConcurrentModificationException cme) {
      String message = "Pagination conflict for jobs: " + cme.getMessage();
      log.warn(message, cme);
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    } catch (Exception e) {
      String message = "Cannot getMdupdates() for limit = " + limit
	  + ", continuationToken = " + continuationToken;
      log.error(message, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
  public ResponseEntity<Status> getMdupdatesJobid(String jobid) {
    log.debug2("jobid = {}", jobid);

    // Check whether the service has not been fully initialized.
    if (!waitReady()) {
      // Yes: Notify the client.
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    try {
      JobAuStatus jobAuStatus = getJobManager().getJobStatus(jobid);
      log.trace("jobAuStatus = {}", () -> jobAuStatus);

      Status result = new Status(jobAuStatus);
      log.trace("result = {}", () -> result);

      return new ResponseEntity<Status>(result, HttpStatus.OK);
    } catch (IllegalArgumentException iae) {
      String message = "No job found for jobid = '" + jobid + "'";
      log.warn(message, iae);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      String message = "Cannot getMdupdatesJobid() for jobid = '" + jobid + "'";
      log.error(message, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Extracts and stores all or part of the metadata for an AU, or deletes the
   * metadata for an AU.
   * 
   * @param metadataUpdateSpec A MetadataUpdateSpec with the specification of
   *                           the metadata update operation.
   * @param force              A Boolean with the indication of whether to force
   *                           the operation regardless of the current state of
   *                           the AU.
   * @return a {@code ResponseEntity<Job>} with the information of the job
   *         created.
   */
  @Override
  public ResponseEntity<Job> postMdupdates(
      MetadataUpdateSpec metadataUpdateSpec, Boolean force) {
    log.debug2("metadataUpdateSpec = {}", metadataUpdateSpec);
    log.debug2("force = {}", force);

    // Check whether the service has not been fully initialized.
    if (!waitReady()) {
      // Yes: Notify the client.
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Check authorization.
    try {
      SpringAuthenticationFilter.checkAuthorization(Roles.ROLE_CONTENT_ADMIN);
    } catch (AccessControlException ace) {
      log.warn(ace.getMessage());
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    // Check whether metadata extraction is not enabled.
    if (!LockssApp.getManagerByTypeStatic(MetadataExtractorManager.class)
	.isIndexingEnabled()) {
      // Yes: Add to the audit log a reference to this operation, if necessary.
      try {
        if (force) {
          audit(ACTION_FORCE_REINDEX_METADATA, null);
        } else {
          audit(ACTION_REINDEX_METADATA, null);
        }
      } catch (AccessControlException ace) {
        log.warn(ace.getMessage());
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      }

      // Report the problem.
      String message = "Metadata extraction is disabled";
      log.warn(message);
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    String auid = null;

    try {
      // Check whether no metadata extraction specification was received.
      if (metadataUpdateSpec == null) {
	// Yes: Add to the audit log a reference to this operation, if
	// necessary.
	try {
	  if (force) {
	    audit(ACTION_FORCE_REINDEX_METADATA, null);
	  } else {
	    audit(ACTION_REINDEX_METADATA, null);
	  }
	} catch (AccessControlException ace) {
	  log.warn(ace.getMessage());
	  return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	// Report the problem.
	String message = "Invalid metadata update specification: null";
	log.warn(message);
	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      auid = metadataUpdateSpec.getAuid();
      log.trace("auid = {}", auid);

      // Add to the audit log a reference to this operation, if necessary.
      try {
        if (force) {
          audit(ACTION_FORCE_REINDEX_METADATA, auid);
        } else {
          audit(ACTION_REINDEX_METADATA, auid);
        }
      } catch (AccessControlException ace) {
        log.warn(ace.getMessage());
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      }

      if (auid == null || auid.isEmpty()) {
	String message = "Invalid auid = '" + auid + "'";
	log.warn(message);
	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      String updateType = metadataUpdateSpec.getUpdateType();
      log.trace("updateType = {}", updateType);

      if (updateType == null || updateType.isEmpty()) {
	String message = "Invalid updateType = '" + updateType + "'";
	log.warn(message);
	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      // Check whether the state of the AU needs to be validated before
      // proceeding with the operation.
      if (!force) {
	// Yes: Get the Archival Unit to have its metadata indexing enabled.
	ArchivalUnit au =
	    LockssDaemon.getLockssDaemon().getPluginManager().getAuFromId(auid);
	log.trace("au = {}", au);

	// Handle a missing Archival Unit.
	if (au == null) {
	  throw new IllegalArgumentException();
	}

	String message = null;

	if (!AuUtil.hasCrawled(au)) {
	  message = "AU has never been crawled. " + USE_FORCE_MESSAGE;
	  log.warn(message);
	  return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	AuState auState = AuUtil.getAuState(au);

	switch (auState.getSubstanceState()) {
	  case No:
	    message = "AU has no substance. " + USE_FORCE_MESSAGE;
	    log.warn(message);
	    return new ResponseEntity<>(HttpStatus.CONFLICT);
	  case Unknown:
	    message = "Unknown substance for AU. " + USE_FORCE_MESSAGE;
	    log.warn(message);
	    return new ResponseEntity<>(HttpStatus.CONFLICT);
	  case Yes:
	    // Fall through.
	}
      }

      String canonicalUpdateType = updateType.toLowerCase();
      log.trace("canonicalUpdateType = {}", canonicalUpdateType);

      JobAuStatus jobAuStatus = null;

      if (canonicalUpdateType.equals(MD_UPDATE_FULL_EXTRACTION)) {
	jobAuStatus = getJobManager().scheduleMetadataExtraction(auid, true);
      } else if (canonicalUpdateType.equals(MD_UPDATE_INCREMENTAL_EXTRACTION)) {
	jobAuStatus = getJobManager().scheduleMetadataExtraction(auid, false);
      } else if (canonicalUpdateType.equals(MD_UPDATE_DELETE)) {
	jobAuStatus = getJobManager().scheduleMetadataRemoval(auid);
      } else {
	String message = "Invalid updateType = '" + updateType + "'";
	log.warn(message);
	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      if (log.isTraceEnabled()) log.trace("jobAuStatus = {}", jobAuStatus);

      Job result = new Job(jobAuStatus);
      log.trace("result = {}", () -> result);

      return new ResponseEntity<Job>(result, HttpStatus.ACCEPTED);
    } catch (IllegalArgumentException iae) {
      String message = "No Archival Unit found for auid = '" + auid + "'";
      log.warn(message, iae);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      String message = "Cannot postMdupdates() for metadataUpdateSpec = '"
	  + metadataUpdateSpec + "', force = " + force;
      log.error(message, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Provides the job manager.
   * 
   * @return a JobManager with the job manager.
   */
  private JobManager getJobManager() {
    return LockssApp.getManagerByTypeStatic(JobManager.class);
  }

  /**
   * Adds to the audit log a reference to this operation, if necessary.
   * 
   * @param action
   *          A String with the name of the operation.
   * @param auId
   *          A String with the identifier (auid) of the archival unit.
   * @throws AccessControlException if the user cannot be validated.
   */
  private void audit(String action, String auId) throws AccessControlException {
    log.debug2("action = {}", action);
    log.debug2("auId = {}", auId);

    String userName =
	SecurityContextHolder.getContext().getAuthentication().getName();
    log.trace("userName = {}", userName);

    // Get the user account.
    UserAccount userAccount = null;

    try {
      userAccount =
          LockssDaemon.getLockssDaemon().getAccountManager().getUser(userName);
      log.trace("userAccount = {}", userAccount);
    } catch (Exception e) {
      log.error("userName = {}", userName);
      log.error("LockssDaemon.getLockssDaemon().getAccountManager()."
          + "getUser(" + userName + ")", e);
      throw new AccessControlException("Unable to get user '" + userName + "'");
    }

    if (userAccount != null && !DebugPanel.noAuditActions.contains(action)) {
      userAccount.auditableEvent("Called AusApi web service operation '"
	  + action + "' AU ID: " + auId);
    }
  }
}
