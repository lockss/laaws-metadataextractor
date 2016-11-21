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
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.lockss.laaws.mdx.api.factories.AusApiServiceFactory;
import org.lockss.laaws.mdx.model.AuPageInfo;
import org.lockss.laaws.mdx.model.Job;
import org.lockss.rs.auth.Roles;

/**
 * Provider of access to the metadata of an AU.
 */
@Path("/aus")
@Produces({ "application/json" })
@Api(value = "/aus")
public class AusApi  {
  private final AusApiService delegate = AusApiServiceFactory.getAuApi();

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
  @DELETE
  @Path("/{auid}")
  @Produces({"application/json"})
  @ApiOperation(value = "Delete the metadata stored for an AU",
  notes = "Delete the metadata stored for an AU given the AU identifier",
  response = Job.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "aus", })
  @ApiResponses(value = { 
      @ApiResponse(code = 202,
	  message = "The job created to delete the AU metadata",
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
  public Response deleteAuAuid(
      @ApiParam(value =
      "The identifier of the AU for which the metadata is to be deleted",
      required=true) @PathParam("auid") String auid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.deleteAuAuid(auid,securityContext);
  }

  /**
   * Provides a list of existing AUs.
   * 
   * @param page
   *          An Integer with the index of the page to be returned.
   * @param limit
   *          An Integer with the maximum number of AUs to be returned.
   * @param securityContext
   *          A SecurityContext providing access to security related
   *          information.
   * @return a Response with any data that needs to be returned to the runtime.
   * @throws NotFoundException
   *           if the AUs could not be obtained.
   */
  @GET
  @Produces({"application/json"})
  @ApiOperation(value = "Get a list of existing AUs",
  notes = "Get a list of all existing AUs (no parameters) or a list of the AUs in a page defined by the page index and size",
  response = AuPageInfo.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "aus", })
  @ApiResponses(value = { 
      @ApiResponse(code = 200, message = "The requested AUs",
	  response = AuPageInfo.class),
      @ApiResponse(code = 500, message = "Internal server error",
      response = AuPageInfo.class),
      @ApiResponse(code = 503,
      message = "Some or all of the system is not available",
      response = AuPageInfo.class) })
  @RolesAllowed(Roles.ROLE_ANY) // Allow any authenticated user.
  public Response getAu(
      @ApiParam(value = "The identifier of the page of AUs to be returned",
      defaultValue="1") @DefaultValue("1") @QueryParam("page") Integer page,
      @ApiParam(value = "The number of AUs per page", defaultValue="50")
      @DefaultValue("50") @QueryParam("limit") Integer limit,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.getAu(page,limit,securityContext);
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
  @Path("/{auid}/job")
  @Produces({"application/json"})
  @ApiOperation(value = "Get the job for an AU",
  notes = "Get the job for an AU given the AU identifier", response = Job.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "aus", })
  @ApiResponses(value = { 
      @ApiResponse(code = 200,
	  message = "The job for the AU",
	  response = Job.class),
      @ApiResponse(code = 404, message = "AU not found",
      response = Job.class),
      @ApiResponse(code = 500, message = "Internal server error",
      response = Job.class),
      @ApiResponse(code = 503,
      message = "Some or all of the system is not available",
      response = Job.class) })
  @RolesAllowed(Roles.ROLE_ANY) // Allow any authenticated user.
  public Response getAuAuidJob(
      @ApiParam(value =
      "The identifier of the AU for which the job is requested",
      required=true) @PathParam("auid") String auid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.getAuAuidJob(auid,securityContext);
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
  @PUT
  @Path("/{auid}")
  @Produces({"application/json"})
  @ApiOperation(value = "Reindex the metadata for an AU",
  notes = "Reindex the metadata for an AU given the AU identifier",
  response = Job.class,
  authorizations = {@Authorization(value = "basicAuth")}, tags={ "aus", })
  @ApiResponses(value = { 
      @ApiResponse(code = 202,
	  message = "The job created to reindex the AU metadata",
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
  public Response putAuAuid(
      @ApiParam(value =
      "The identifier of the AU for which the metadata is to be reindexed",
      required=true) @PathParam("auid") String auid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.putAuAuid(auid,securityContext);
  }
}
