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
package org.lockss.laaws.mdx.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.lockss.laaws.mdx.api.factories.JobsApiServiceFactory;
import org.lockss.laaws.mdx.model.Job;
import org.lockss.laaws.mdx.model.JobPageInfo;
import org.lockss.laaws.mdx.model.Status;
import org.lockss.rs.auth.Roles;

/**
 * Provider of access to the AU metadata jobs.
 */
@Path("/jobs")
@Produces({ "application/json" })
@Api(value = "/jobs")
public class JobsApi  {
  private final JobsApiService delegate = JobsApiServiceFactory.getJobApi();

  /**
   * Deletes all of the queued jobs and stops any processing and deletes any
   * active jobs.
   * 
   * @param securityContext
   *          A SecurityContext providing access to security related
   *          information.
   * @return a Response with any data that needs to be returned to the runtime.
   */
  @DELETE
  @Produces({"application/json"})
  @ApiOperation(value = "Delete all of the currently queued and active jobs",
  notes = "Delete all of the currently queued and active jobs",
  response = void.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "jobs", })
  @ApiResponses(value = { 
      @ApiResponse(code = 200, message = "Jobs were successfully deleted",
	  response = void.class),
      @ApiResponse(code = 401, message = "Unauthorized request",
      response = void.class),
      @ApiResponse(code = 403, message = "Forbidden request",
      response = void.class),
      @ApiResponse(code = 404, message = "AU not found",
      response = void.class),
      @ApiResponse(code = 500, message = "Internal server error",
      response = void.class),
      @ApiResponse(code = 503,
      message = "Some or all of the system is not available",
      response = void.class) })
  @RolesAllowed(Roles.ROLE_CONTENT_ADMIN) // Allow this role.
  public Response deleteJob(@Context SecurityContext securityContext)
      throws ApiException {
    return delegate.deleteJob(securityContext);
  }

  /**
   * Deletes the job for an AU given the AU identifier.
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
  @DELETE
  @Path("/au/{auid}")
  @Produces({"application/json"})
  @ApiOperation(value = "Delete the job for an AU",
  notes = "Delete the job for an AU given the AU identifier",
  response = Job.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "jobs", })
  @ApiResponses(value = { 
      @ApiResponse(code = 200, message = "The deleted AU job",
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
  @RolesAllowed(Roles.ROLE_CONTENT_ADMIN) // Allow this role.
  public Response deleteJobAuAuid(
      @ApiParam(value = "The identifier of the AU whose job is to be deleted",
      required=true) @PathParam("auid") String auid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.deleteJobAuAuid(auid,securityContext);
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
  @DELETE
  @Path("/{jobid}")
  @Produces({"application/json"})
  @ApiOperation(value = "Delete a job",
  notes = "Delete a job given the job identifier, stopping any current processing, if necessary",
  response = Job.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "jobs", })
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
  @RolesAllowed(Roles.ROLE_CONTENT_ADMIN) // Allow this role.
  public Response deleteJobJobid(
      @ApiParam(value = "The identifier of the job to be deleted",
      required=true) @PathParam("jobid") String jobid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.deleteJobJobid(jobid,securityContext);
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
  @GET
  @Produces({"application/json"})
  @ApiOperation(value = "Get a list of currently active jobs",
  notes = "Get a list of all currently active jobs (no parameters) or a list of the currently active jobs in a page defined by the page index and size",
  response = JobPageInfo.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "jobs", })
  @ApiResponses(value = { 
      @ApiResponse(code = 200, message = "The requested jobs",
	  response = JobPageInfo.class),
      @ApiResponse(code = 500, message = "Internal server error",
      response = JobPageInfo.class),
      @ApiResponse(code = 503,
      message = "Some or all of the system is not available",
      response = JobPageInfo.class) })
  @RolesAllowed(Roles.ROLE_ANY) // Allow any authenticated user.
  public Response getJob(
      @ApiParam(value = "The identifier of the page of jobs to be returned",
      defaultValue="1") @DefaultValue("1") @QueryParam("page") Integer page,
      @ApiParam(value = "The number of jobs per page", defaultValue="50")
      @DefaultValue("50") @QueryParam("limit") Integer limit,
      @Context HttpServletRequest request,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.getJob(page,limit,request,securityContext);
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
  @GET
  @Path("/au/{auid}")
  @Produces({"application/json"})
  @ApiOperation(value = "Get the job for an AU",
  notes = "Get the job for an AU given the AU identifier", response = Job.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "jobs", })
  @ApiResponses(value = { 
      @ApiResponse(code = 200, message = "The job for the AU",
	  response = Job.class),
      @ApiResponse(code = 404, message = "AU not found",
      response = Job.class),
      @ApiResponse(code = 500, message = "Internal server error",
      response = Job.class),
      @ApiResponse(code = 503,
      message = "Some or all of the system is not available",
      response = Job.class) })
  @RolesAllowed(Roles.ROLE_ANY) // Allow any authenticated user.
  public Response getJobAuAuid(
      @ApiParam(
	  value = "The identifier of the AU for which the job is requested",
      required=true) @PathParam("auid") String auid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.getJobAuAuid(auid,securityContext);
  }

  /**
   * Provides a job given the job identifier.
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
  @GET
  @Path("/{jobid}")
  @Produces({"application/json"})
  @ApiOperation(value = "Get a job",
  notes = "Get a job given the job identifier", response = Status.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "jobs", })
  @ApiResponses(value = { 
      @ApiResponse(code = 200, message = "The status of the requested job",
	  response = Status.class),
      @ApiResponse(code = 404, message = "Job not found",
      response = Status.class),
      @ApiResponse(code = 500, message = "Internal server error",
      response = Status.class),
      @ApiResponse(code = 503,
      message = "Some or all of the system is not available",
      response = Status.class) })
  @RolesAllowed(Roles.ROLE_ANY) // Allow any authenticated user.
  public Response getJobJobid(
      @ApiParam(value = "The identifier of the requested job", required=true)
      @PathParam("jobid") String jobid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.getJobJobid(jobid,securityContext);
  }
}
