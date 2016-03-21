package org.lockss.snafl.mdx.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.annotation.XmlTransient;



import org.lockss.snafl.mdx.api.factories.AuApiServiceFactory;
import org.lockss.snafl.mdx.model.Job;

@Path("/au")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
@Api(value = "/au", description = "Provide access to metadata for a au")
public class AuApi  {
   private final AuApiService delegate = AuApiServiceFactory.getAuApi();

    @GET
    @Path("/{auid}/jobs")
    @Produces({ "application/json" })
    public Response auAuidJobsGet( @PathParam("auid") String auid,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.auAuidJobsGet(auid,securityContext);
    }

    @DELETE
    @Path("/{auid}")
    @Produces({ "application/json" })
    public Response deleteAuAuid( @PathParam("auid") String auid,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.deleteAuAuid(auid,securityContext);
    }

    @GET
    @Produces({ "application/json" })
    public Response getAu( @QueryParam("page") Integer page, @QueryParam("limit") Integer limit,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.getAu(page,limit,securityContext);
    }

    @GET
    @Path("/{auid}")
    @Produces({ "application/json" })
    public Response getAuAuid( @PathParam("auid") String auid,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.getAuAuid(auid,securityContext);
    }

    @PUT
    @Path("/{auid}")
    @Produces({ "application/json" })
    public Response putAuAuid( @PathParam("auid") String auid,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.putAuAuid(auid,securityContext);
    }
}
