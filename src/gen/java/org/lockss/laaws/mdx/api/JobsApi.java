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
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.lockss.laaws.mdx.api.factories.JobsApiServiceFactory;

/**
 * Provider of access to the AU metadata jobs.
 */
@Path("/jobs")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
@Api(value = "/jobs")
public class JobsApi  {
  private final JobsApiService delegate = JobsApiServiceFactory.getJobsApi();

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
  public Response deleteJobs(@Context SecurityContext securityContext)
      throws ApiException {
    return delegate.deleteJobs(securityContext);
  }

  /**
   * Deletes the the job for an AU given the AU identifier.
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
  public Response deleteJobsAuAuid(@PathParam("auid") String auid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.deleteJobsAuAuid(auid,securityContext);
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
  public Response deleteJobsJobid(@PathParam("jobid") String jobid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.deleteJobsJobid(jobid,securityContext);
  }

  /**
   * Provides a list of existing jobss.
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
  public Response getJobsAuAuid(@PathParam("auid") String auid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.getJobsAuAuid(auid,securityContext);
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
  public Response getJobsJobid(@PathParam("jobid") String jobid,
      @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.getJobsJobid(jobid,securityContext);
  }
}
