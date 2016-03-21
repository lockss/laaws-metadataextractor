package org.lockss.snafl.mdx.api;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import io.swagger.annotations.Api;
import org.lockss.snafl.mdx.api.factories.JobsApiServiceFactory;

@Path("/jobs")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
@Api(value = "/jobs", description = "Provide access to jobs on metadata service")
public class JobsApi  {
   private final JobsApiService delegate = JobsApiServiceFactory.getJobsApi();

    @DELETE
    @Produces({ "application/json" })
    public Response deleteJobs(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.deleteJobs(securityContext);
    }

    @DELETE
    @Path("/au/{auid}")
    @Produces({ "application/json" })
    public Response deleteJobsAuAuid( @PathParam("auid") String auid,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.deleteJobsAuAuid(auid,securityContext);
    }

    @DELETE
    @Path("/{jobid}")
    @Produces({ "application/json" })
    public Response deleteJobsJobid( @PathParam("jobid") String jobid,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.deleteJobsJobid(jobid,securityContext);
    }

    @GET
    @Produces({ "application/json" })
    public Response getJobs( @QueryParam("page") Integer page, @QueryParam("limit") Integer limit,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.getJobs(page,limit,securityContext);
    }

    @GET
    @Path("/au/{auid}")
    @Produces({ "application/json" })
    public Response getJobsAuAuid( @PathParam("auid") String auid,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.getJobsAuAuid(auid,securityContext);
    }

    @GET
    @Path("/{jobid}")
    @Produces({ "application/json" })
    public Response getJobsJobid( @PathParam("jobid") String jobid,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.getJobsJobid(jobid,securityContext);
    }
}
