/*

 Copyright (c) 2017 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.laaws.mdx.security;

import java.io.IOException;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.lockss.account.UserAccount;
import org.lockss.app.LockssDaemon;
import org.lockss.rs.auth.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Custom Spring authentication filter.
 */
public class SpringAuthenticationFilter extends GenericFilterBean {
  public static final String noAuthorizationHeader = "No authorization header.";
  public static final String noCredentials = "No userid/password credentials.";
  public static final String badCredentials =
      "Bad userid/password credentials.";
  public static final String noUser = "User not found.";
  private final static Logger logger =
      LoggerFactory.getLogger(SpringAuthenticationFilter.class);

  /**
   * Authentication filter.
   *
   * @param request
   *          A ServletRequest with the incoming servlet request.
   * @param response
   *          A ServletResponse with the outgoing servlet response.
   * @param chain
   *          A FilterChain with the chain where this filter is set.
   * @throws IOException
   *           if there are problems.
   * @throws ServletException
   *           if there are problems.
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    HttpServletRequest httpRequest = (HttpServletRequest)request;
    HttpServletResponse httpResponse = (HttpServletResponse)response;

    try {
      // Check whether authentication is not required at all.
      if (!AuthUtil.isAuthenticationOn()) {
	// Yes: Continue normally.
	if (logger.isDebugEnabled())
	  logger.debug("Authorized (like everybody else).");

	SecurityContextHolder.getContext().setAuthentication(
	    getUnauthenticatedUserToken());

	// Continue the chain.
	chain.doFilter(request, response);
	return;
      }
    } catch (AccessControlException ace) {
      // Report the configuration problem.
      logger.error(ace.getMessage());

      SecurityContextHolder.clearContext();
      httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
	  ace.getMessage());
      return;
    }

    // No: Check whether this request is available to everybody.
    if (isWorldReachable(httpRequest)) {
      // Yes: Continue normally.
      if (logger.isDebugEnabled())
	logger.debug("Authenticated (like everybody else).");

      SecurityContextHolder.getContext().setAuthentication(
	  getUnauthenticatedUserToken());

      // Continue the chain.
      chain.doFilter(request, response);
      return;
    }

    // No: Get the authorization header.
    String authorizationHeader = httpRequest.getHeader("authorization");
    if (logger.isDebugEnabled())
      logger.debug("authorizationHeader = " + authorizationHeader);

    // Check whether no authorization header was found.
    if (authorizationHeader == null) {
      // Yes: Report the problem.
      logger.info(noAuthorizationHeader);

      SecurityContextHolder.clearContext();
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
	  noAuthorizationHeader);
      return;
    }

    // No: Get the user credentials in the authorization header.
    String[] credentials =
	AuthUtil.decodeBasicAuthorizationHeader(authorizationHeader);

    // Check whether no credentials were found.
    if (credentials == null) {
      // Yes: Report the problem.
      logger.info(noCredentials);

      SecurityContextHolder.clearContext();
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
	  noCredentials);
      return;
    }

    // No: Check whether the found credentials are not what was expected.
    if (credentials.length != 2) {
      // Yes: Report the problem.
      logger.info(badCredentials);
      logger.info("bad credentials = " + Arrays.toString(credentials));

      SecurityContextHolder.clearContext();
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
	  badCredentials);
      return;
    }

    if (logger.isDebugEnabled())
      logger.debug("credentials[0] = " + credentials[0]);

    // No: Get the user account.
    UserAccount userAccount = null;

    try {
      userAccount = LockssDaemon.getLockssDaemon().getAccountManager()
	  .getUser(credentials[0]);
    } catch (Exception e) {
      logger.error("credentials[0] = " + credentials[0]);
      logger.error("credentials[1] = " + credentials[1]);
      logger.error("LockssDaemon.getLockssDaemon().getAccountManager()."
	  + "getUser(credentials[0])", e);
    }

    // Check whether no user was found.
    if (userAccount == null) {
      // Yes: Report the problem.
      logger.info(noUser);

      SecurityContextHolder.clearContext();
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
	  badCredentials);
      return;
    }

    if (logger.isDebugEnabled())
      logger.debug("userAccount.getName() = " + userAccount.getName());

    // No: Verify the user credentials.
    boolean goodCredentials = userAccount.check(credentials[1]);
    if (logger.isDebugEnabled())
      logger.debug("goodCredentials = " + goodCredentials);

    // Check whether the user credentials are not good.
    if (!goodCredentials) {
      // Yes: Report the problem.
      logger.info(badCredentials);
      logger.info("userAccount.getName() = " + userAccount.getName());
      logger.info("bad credentials = " + Arrays.toString(credentials));

      SecurityContextHolder.clearContext();
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
	  badCredentials);
      return;
    }
  
    // No: Get the user roles.
    Collection<GrantedAuthority> roles = new HashSet<GrantedAuthority>();

    for (Object role : userAccount.getRoleSet()) {
      if (logger.isDebugEnabled()) logger.debug("role = " + role);
      roles.add(new SimpleGrantedAuthority((String)role));
    }

    if (logger.isDebugEnabled()) logger.debug("roles = " + roles);

    // Create the completed authentication details.
    UsernamePasswordAuthenticationToken authentication =
	new UsernamePasswordAuthenticationToken(credentials[0],
	    credentials[1], roles);
    if (logger.isDebugEnabled()) 
      logger.debug("authentication = " + authentication);

    // Provide the completed authentication details.
    SecurityContextHolder.getContext().setAuthentication(authentication);
    logger.debug("User successfully authenticated");

    // Continue the chain.
    chain.doFilter(request, response);

    if (logger.isDebugEnabled()) logger.debug("Done.");
  }

  /**
   * Provides the completed authentication for an unauthenticated user.
   * 
   * @return a UsernamePasswordAuthenticationToken with the completed
   *         authentication details.
   */
  private UsernamePasswordAuthenticationToken getUnauthenticatedUserToken() {
    Collection<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
    roles.add(new SimpleGrantedAuthority("unauthenticatedRole"));

    return new UsernamePasswordAuthenticationToken("unauthenticatedUser",
	"unauthenticatedPassword", roles);
  }

  /**
   * Provides an indication of whether this request is available to everybody.
   * 
   * @param httpRequest
   *          A HttpServletRequest with the incoming request.
   * @return <code>true</code> if this request is available to everybody,
   *         <code>false</code> otherwise.
   */
  private boolean isWorldReachable(HttpServletRequest httpRequest) {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    // Get the HTTP request method name.
    String httpMethodName = httpRequest.getMethod().toUpperCase();
    if (logger.isDebugEnabled())
      logger.debug("httpMethodName = " + httpMethodName);

    // Get the HTTP request URI.
    String requestUri = httpRequest.getRequestURI().toLowerCase();
    if (logger.isDebugEnabled()) logger.debug("requestUri = " + requestUri);

    // Determine whether it is world-reachable.
    boolean result = ("GET".equals(httpMethodName)
	&& ("/v2/api-docs".equals(requestUri)
	    || "/swagger-ui.html".equals(requestUri)
	    || requestUri.startsWith("/swagger-resources")
	    || requestUri.startsWith("/webjars/springfox-swagger-ui"))
	);

    if (logger.isDebugEnabled()) logger.debug("result = " + result);
    return result;
  }

  /**
   * Checks whether the current user has the role required to fulfill a set of
   * roles. Throws AccessControlException if the check fails.
   * 
   * @param permissibleRoles
   *          A String... with the roles permissible for the user to be able to
   *          execute an operation.
   */
  public static void checkAuthorization(String... permissibleRoles) {
    if (logger.isDebugEnabled())
      logger.debug("permissibleRoles = " + Arrays.toString(permissibleRoles));

    AuthUtil.checkAuthorization(SecurityContextHolder.getContext()
	.getAuthentication().getName(), permissibleRoles);

    if (logger.isDebugEnabled()) logger.debug("Done.");
  }
}
