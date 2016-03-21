package org.lockss.snafl.mdx.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public abstract class AuApiService {
  
      public abstract Response auAuidJobsGet(String auid,SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response deleteAuAuid(String auid,SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response getAu(Integer page,Integer limit,SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response getAuAuid(String auid,SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response putAuAuid(String auid,SecurityContext securityContext)
      throws NotFoundException;
  
}
