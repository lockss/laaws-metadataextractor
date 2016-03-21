package org.lockss.snafl.mdx.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public abstract class JobsApiService {
  
      public abstract Response deleteJobs(SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response deleteJobsAuAuid(String auid,SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response deleteJobsJobid(String jobid,SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response getJobs(Integer page,Integer limit,SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response getJobsAuAuid(String auid,SecurityContext securityContext)
      throws NotFoundException;
  
      public abstract Response getJobsJobid(String jobid,SecurityContext securityContext)
      throws NotFoundException;
  
}
