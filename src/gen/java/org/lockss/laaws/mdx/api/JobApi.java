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
import org.lockss.laaws.mdx.api.factories.JobApiServiceFactory;
import org.lockss.servlet.LockssServlet;

/**
 * Provider of access to the AU metadata jobs.
 */
@Path("/job")
@Api(value = "/job")
public class JobApi  {
  private final JobApiService delegate = JobApiServiceFactory.getJobApi();

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
  @RolesAllowed(LockssServlet.ROLE_CONTENT_ADMIN) // Allow this role.
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
  @RolesAllowed(LockssServlet.ROLE_CONTENT_ADMIN) // Allow this role.
  public Response deleteJobAuAuid(@PathParam("auid") String auid,
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
  @RolesAllowed(LockssServlet.ROLE_CONTENT_ADMIN) // Allow this role.
  public Response deleteJobJobid(@PathParam("jobid") String jobid,
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
   * @param securityContext
   *          A SecurityContext providing access to security related
   *          information.
   * @return a Response with any data that needs to be returned to the runtime.
   * @throws NotFoundException
   *           if the job with the given identifier does not exist.
   */
  @GET
  @Produces({"application/json"})
  @RolesAllowed(LockssServlet.ROLE_CONTENT_ACCESS) // Allow this role.
  public Response getJob(@QueryParam("page") Integer page,
      @QueryParam("limit") Integer limit,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.getJob(page,limit,securityContext);
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
  @RolesAllowed(LockssServlet.ROLE_CONTENT_ACCESS) // Allow this role.
  public Response getJobAuAuid(@PathParam("auid") String auid,
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
  @RolesAllowed(LockssServlet.ROLE_CONTENT_ACCESS) // Allow this role.
  public Response getJobJobid(@PathParam("jobid") String jobid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.getJobJobid(jobid,securityContext);
  }
}
