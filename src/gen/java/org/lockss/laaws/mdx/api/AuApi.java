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
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.lockss.laaws.mdx.api.factories.AuApiServiceFactory;
import org.lockss.rs.auth.Roles;

/**
 * Provider of access to the metadata of an AU.
 */
@Path("/au")
@Api(value = "/au")
public class AuApi  {
  private final AuApiService delegate = AuApiServiceFactory.getAuApi();

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
  @RolesAllowed(Roles.ROLE_CONTENT_ADMIN) // Allow this role.
  public Response deleteAuAuid(@PathParam("auid") String auid,
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
  @RolesAllowed(Roles.ROLE_ANY) // Allow any authenticated user.
  public Response getAu(@QueryParam("page") Integer page,
      @QueryParam("limit") Integer limit,
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
  @RolesAllowed(Roles.ROLE_ANY) // Allow any authenticated user.
  public Response getAuAuidJob(@PathParam("auid") String auid,
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
  @RolesAllowed(Roles.ROLE_CONTENT_ADMIN) // Allow this role.
  public Response putAuAuid(@PathParam("auid") String auid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.putAuAuid(auid,securityContext);
  }
}
