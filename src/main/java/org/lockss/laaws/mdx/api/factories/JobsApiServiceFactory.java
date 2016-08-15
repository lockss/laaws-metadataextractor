package org.lockss.laaws.mdx.api.factories;

import org.lockss.laaws.mdx.api.JobsApiService;
import org.lockss.laaws.mdx.api.impl.JobsApiServiceImpl;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public class JobsApiServiceFactory {

   private final static JobsApiService service = new JobsApiServiceImpl();

   public static JobsApiService getJobsApi()
   {
      return service;
   }
}
