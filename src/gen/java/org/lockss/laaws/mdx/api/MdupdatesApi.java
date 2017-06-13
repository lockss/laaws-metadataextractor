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
import org.lockss.laaws.mdx.api.NotFoundException;
import org.lockss.laaws.mdx.api.factories.MdupdatesApiServiceFactory;
import org.lockss.laaws.mdx.model.Job;
import org.lockss.laaws.mdx.model.JobPageInfo;
import org.lockss.laaws.mdx.model.MetadataUpdateSpec;
import org.lockss.laaws.mdx.model.Status;
import org.lockss.rs.auth.Roles;

/**
 * Provider of access to the AU metadata jobs.
 */
@Path("/mdupdates")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@Api(value = "/mdupdates")
public class MdupdatesApi  {
  private final MdupdatesApiService delegate =
      MdupdatesApiServiceFactory.getMdupdatesApi();

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
  @Consumes({"application/json"})
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
  public Response deleteMdupdates(@Context SecurityContext securityContext)
      throws ApiException {
    return delegate.deleteMdupdates(securityContext);
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
  @Consumes({"application/json"})
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
  public Response deleteMdupdatesJobid(
      @ApiParam(value = "The identifier of the job to be deleted",
      required=true) @PathParam("jobid") String jobid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.deleteMdupdatesJobid(jobid, securityContext);
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
  @Consumes({"application/json"})
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
  public Response getMdupdates(
      @ApiParam(value = "The identifier of the page of jobs to be returned",
      defaultValue="1") @DefaultValue("1") @QueryParam("page") Integer page,
      @ApiParam(value = "The number of jobs per page", defaultValue="50")
      @DefaultValue("50") @QueryParam("limit") Integer limit,
      @Context HttpServletRequest request,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.getMdupdates(page, limit, request, securityContext);
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
  @Consumes({"application/json"})
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
  public Response getMdupdatesJobid(
      @ApiParam(value = "The identifier of the requested job", required=true)
      @PathParam("jobid") String jobid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.getMdupdatesJobid(jobid, securityContext);
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
  @POST
  @Consumes({"application/json"})
  @Produces({"application/json"})
  @ApiOperation(value = "Perform an AU metadata update operation",
  notes =
  "Perform an AU metadata update operation given the update specification",
  response = Job.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "aus", })
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
  @RolesAllowed(Roles.ROLE_CONTENT_ADMIN) // Allow this role.
  public Response postMdupdates(@ApiParam(
      value =
      "The information defining the AU metadata update operation",
      required=true) MetadataUpdateSpec metadataUpdateSpec,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.postMdupdates(metadataUpdateSpec, securityContext);
  }
}
