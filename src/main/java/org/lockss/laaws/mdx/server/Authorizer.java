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
package org.lockss.laaws.mdx.server;

//import java.lang.reflect.Method;
import java.util.HashSet;
//import java.util.List;
import java.util.Set;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.ext.Provider;
//import javax.ws.rs.core.PathSegment;
import org.lockss.rs.auth.AccessControlFilter;
//import org.lockss.rs.auth.Roles;
import org.lockss.util.Logger;

/**
 * Access Control filter for this service.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class Authorizer extends AccessControlFilter {
  private static final Logger log = Logger.getLogger(Authorizer.class);

  /**
   * Provides the names of the roles permissible for the user to be able to
   * execute operations of this web service when no javax.annotation.security
   * annotations are specified for web service operations.
   * 
   * @param resourceInfo
   *          A ResourceInfo with information about the resource involved.
   * @param requestContext
   *          A ContainerRequestContext with the request context.
   * 
   * @return a Set<String> with the permissible roles.
   */
  @Override
  protected Set<String> getPermissibleRoles(ResourceInfo resourceInfo,
      ContainerRequestContext requestContext) {
    final String DEBUG_HEADER = "getPermissibleRoles(): ";
    if (log.isDebug2()) log.debug2(DEBUG_HEADER + "Invoked.");

    Set<String> permissibleRoles = new HashSet<String>();

//    Method method = resourceInfo.getResourceMethod();
//    if (log.isDebug3()) log.debug3(DEBUG_HEADER + "method = " + method);
//
//    // Get the request path segments.
//    List<PathSegment> pathSegments = requestContext.getUriInfo()
//	.getPathSegments();
//    if (log.isDebug3())
//      log.debug3(DEBUG_HEADER + "pathSegments = " + pathSegments);
//
//    if ("GET".equals(requestContext.getMethod().toUpperCase())) {
//      if (pathSegments.size() == 2
//	  && "au".equals(pathSegments.get(0).getPath().toLowerCase())) {
//	permissibleRoles.add(Roles.ROLE_CONTENT_ACCESS);
//      } else if (pathSegments.size() == 3
//	  && "url".equals(pathSegments.get(0).getPath().toLowerCase())
//	  && "doi".equals(pathSegments.get(1).getPath().toLowerCase())) {
//	permissibleRoles.add(Roles.ROLE_CONTENT_ACCESS);
//      } else if (pathSegments.size() == 2
//	  && "url".equals(pathSegments.get(0).getPath().toLowerCase())
//	  && "openurl".equals(pathSegments.get(1).getPath().toLowerCase())) {
//	permissibleRoles.add(Roles.ROLE_CONTENT_ACCESS);
//      }
//    }

    return permissibleRoles;
  }
}
