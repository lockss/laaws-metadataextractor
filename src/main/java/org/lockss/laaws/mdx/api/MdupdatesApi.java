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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.lockss.laaws.mdx.model.Job;
import org.lockss.laaws.mdx.model.JobPageInfo;
import org.lockss.laaws.mdx.model.MetadataUpdateSpec;
import org.lockss.laaws.mdx.model.Status;
import org.lockss.spring.status.SpringLockssBaseApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Provider of access to the AU metadata jobs.
 */
@Api(value = "mdupdates")
public interface MdupdatesApi extends SpringLockssBaseApi {

  public static final String MD_UPDATE_DELETE = "delete";
  public static final String MD_UPDATE_FULL_EXTRACTION = "full_extraction";
  public static final String MD_UPDATE_INCREMENTAL_EXTRACTION =
      "incremental_extraction";

  /**
   * Deletes all of the queued jobs and stops any processing and deletes any
   * active jobs.
   * 
   * @return a {@code ResponseEntity<Integer>} with the count of jobs deleted.
   */
  @ApiOperation(value = "Delete all of the currently queued and active jobs",
  notes = "Delete all of the currently queued and active jobs",
  response = Integer.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "mdupdates", })
  @ApiResponses(value = { 
      @ApiResponse(code = 200, message = "Jobs were successfully deleted",
	  response = Integer.class),
      @ApiResponse(code = 401, message = "Unauthorized request",
      response = Integer.class),
      @ApiResponse(code = 403, message = "Forbidden request",
      response = Integer.class),
      @ApiResponse(code = 500, message = "Internal server error",
      response = Integer.class),
      @ApiResponse(code = 503,
      message = "Some or all of the system is not available",
      response = Integer.class) })
  @RequestMapping(value = "/mdupdates",
  produces = { "application/json" }, consumes = { "application/json" },
  method = RequestMethod.DELETE)
  default ResponseEntity<Integer> deleteMdupdates() {
    return new ResponseEntity<Integer>(HttpStatus.NOT_IMPLEMENTED);
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
  @ApiOperation(value = "Delete a job",
  notes = "Delete a job given the job identifier, stopping any current processing, if necessary",
  response = Job.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "mdupdates", })
  @ApiResponses(value = { 
      @ApiResponse(code = 200, message = "The deleted job",
	  response = Job.class),
      @ApiResponse(code = 401, message = "Unauthorized request",
      response = Job.class),
      @ApiResponse(code = 403, message = "Forbidden request",
      response = Job.class),
      @ApiResponse(code = 404, message = "Job not found",
      response = Job.class),
      @ApiResponse(code = 500, message = "Internal server error",
      response = Job.class),
      @ApiResponse(code = 503,
      message = "Some or all of the system is not available",
      response = Job.class) })
  @RequestMapping(value = "/mdupdates/{jobid}",
  produces = { "application/json" }, consumes = { "application/json" },
  method = RequestMethod.DELETE)
  default ResponseEntity<Job> deleteMdupdatesJobid(
      @ApiParam(value = "The identifier of the job to be deleted",
      required=true) @PathVariable("jobid") String jobid) {
    return new ResponseEntity<Job>(HttpStatus.NOT_IMPLEMENTED);
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
  @ApiOperation(value = "Get a list of currently active jobs",
  notes = "Get a list of all currently active jobs (no parameters) or a list of the currently active jobs in a page defined by the page index and size",
  response = JobPageInfo.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "mdupdates", })
  @ApiResponses(value = { 
      @ApiResponse(code = 200, message = "The requested jobs",
	  response = JobPageInfo.class),
      @ApiResponse(code = 401, message = "Unauthorized request",
      response = JobPageInfo.class),
      @ApiResponse(code = 500, message = "Internal server error",
      response = JobPageInfo.class),
      @ApiResponse(code = 503,
      message = "Some or all of the system is not available",
      response = JobPageInfo.class) })
  @RequestMapping(value = "/mdupdates",
  produces = { "application/json" },
  method = RequestMethod.GET)
  default public ResponseEntity<JobPageInfo> getMdupdates(
      @ApiParam(value = "The identifier of the page of jobs to be returned",
      defaultValue="1") @RequestParam(value = "page", required = false,
      defaultValue="1") Integer page,
      @ApiParam(value = "The number of jobs per page", defaultValue="50")
      @RequestParam(value = "limit", required = false, defaultValue="50")
      Integer limit) {
    return new ResponseEntity<JobPageInfo>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * Provides a job given the job identifier.
   * 
   * @param jobid
   *          A String with the job identifier.
   * @return a {@code ResponseEntity<Status>} with the job information.
   */
  @ApiOperation(value = "Get a job",
  notes = "Get a job given the job identifier", response = Status.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "mdupdates", })
  @ApiResponses(value = { 
      @ApiResponse(code = 200, message = "The status of the requested job",
	  response = Status.class),
      @ApiResponse(code = 401, message = "Unauthorized request",
      response = Status.class),
      @ApiResponse(code = 404, message = "Job not found",
      response = Status.class),
      @ApiResponse(code = 500, message = "Internal server error",
      response = Status.class),
      @ApiResponse(code = 503,
      message = "Some or all of the system is not available",
      response = Status.class) })
  @RequestMapping(value = "/mdupdates/{jobid}",
  produces = { "application/json" },
  method = RequestMethod.GET)
  default ResponseEntity<Status> getMdupdatesJobid(
      @ApiParam(value = "The identifier of the requested job", required=true)
      @PathVariable("jobid") String jobid) {
    return new ResponseEntity<Status>(HttpStatus.NOT_IMPLEMENTED);
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
  @ApiOperation(value = "Perform an AU metadata update operation",
  notes =
  "Perform an AU metadata update operation given the update specification",
  response = Job.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "mdupdates", })
  @ApiResponses(value = { 
      @ApiResponse(code = 202, message =
	  "The job created to perform the AU metadata update operation",
	  response = Job.class),
      @ApiResponse(code = 400, message = "Bad request",
      response = Job.class),
      @ApiResponse(code = 401, message = "Unauthorized request",
      response = Job.class),
      @ApiResponse(code = 403, message = "Forbidden request",
      response = Job.class),
      @ApiResponse(code = 404, message = "AU not found",
      response = Job.class),
      @ApiResponse(code = 500, message = "Internal server error",
      response = Job.class),
      @ApiResponse(code = 503,
      message = "Some or all of the system is not available",
      response = Job.class) })
  @RequestMapping(value = "/mdupdates",
  produces = { "application/json" }, consumes = { "application/json" },
  method = RequestMethod.POST)
  default ResponseEntity<Job> postMdupdates(@ApiParam(
      value = "The information defining the AU metadata update operation",
      required=true) @RequestBody MetadataUpdateSpec metadataUpdateSpec) {
    return new ResponseEntity<Job>(HttpStatus.NOT_IMPLEMENTED);
  }
}
