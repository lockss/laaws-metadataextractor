/*

 Copyright (c) 2016-2017 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.laaws.mdx.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Startup;
import org.lockss.laaws.mdx.server.LaawsMdxApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The code to be executed on server startup.
 */
@Startup
public class StartUpEjbImpl implements StartUpEjbLocal {
  private static final Logger log =
      LoggerFactory.getLogger(StartUpEjbImpl.class);

  // The configuration files.
  @Resource(name="confFiles")
  private String confFiles;

  // The configuration REST service location.
  @Resource(name="serviceLocation")
  private String serviceLocation;

  // The configuration REST service user name.
  @Resource(name="serviceUser")
  private String serviceUser;

  // The configuration REST service user password.
  @Resource(name="servicePassword")
  private String servicePassword;

  // The configuration REST service connection timeout, in seconds.
  @Resource(name="serviceTimeout")
  private String serviceTimeout;

  /**
   * Run immediately after this EJB is fully constructed.
   */
  @PostConstruct
  public void runAfterConstruction() {
    if (log.isDebugEnabled()) {
      log.debug("confFiles = '" + confFiles + "'");
      log.debug("serviceLocation = '" + serviceLocation + "'");
      log.debug("serviceUser = '" + serviceUser + "'");
      log.debug("servicePassword = '" + servicePassword + "'");
      log.debug("serviceTimeout = '" + serviceTimeout + "'");
    }

    String[] options = null;

    // Check whether configuration files are used.
    if (confFiles != null && confFiles.trim().length() > 0) {
      // Yes.
      options = confFiles.split(" ");
    } else {
      // No.
      options = new String[5];
      options[0] = LaawsMdxApp.USE_REST_WEB_SERVICE;
      options[1] = serviceLocation;
      options[2] = serviceUser;
      options[3] = servicePassword;

      if (!"null".equalsIgnoreCase(serviceTimeout)) {
	options[4] = serviceTimeout;
      }
    }

    // Configure the LAAWS-specific part of the web services server.
    LaawsMdxApp.main(options);

    if (log.isDebugEnabled()) log.debug("Done.");
  }

  /**
   * Run immediately before this EJB is destroyed.
   */
  @PreDestroy
  public void runBeforeDestroy() {
    if (log.isDebugEnabled()) log.debug("Stopping StartUpEjbImpl");
  }
}
