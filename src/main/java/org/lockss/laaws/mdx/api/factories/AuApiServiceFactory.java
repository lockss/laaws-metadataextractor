package org.lockss.laaws.mdx.api.factories;

import org.lockss.laaws.mdx.api.AuApiService;
import org.lockss.laaws.mdx.api.impl.AuApiServiceImpl;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public class AuApiServiceFactory {

   private final static AuApiService service = new AuApiServiceImpl();

   public static AuApiService getAuApi()
   {
      return service;
   }
}
