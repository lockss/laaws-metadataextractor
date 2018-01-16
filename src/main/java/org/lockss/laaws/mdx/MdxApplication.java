/*

 Copyright (c) 2017-2018 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.laaws.mdx;

import static org.lockss.app.ManagerDescs.*;
import org.lockss.app.LockssApp;
import org.lockss.app.LockssApp.AppSpec;
import org.lockss.app.LockssApp.ManagerDesc;
import org.lockss.app.LockssDaemon;
import org.lockss.rs.base.BaseSpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Spring-Boot application.
 */
@SpringBootApplication
public class MdxApplication extends BaseSpringBootApplication
	implements CommandLineRunner {
  private static final Logger logger =
      LoggerFactory.getLogger(MdxApplication.class);

  // Manager descriptors.  The order of this table determines the order in
  // which managers are initialized and started.
  protected final static ManagerDesc[] myManagerDescs = {
    ACCOUNT_MANAGER_DESC,
    // start plugin manager after generic services
    PLUGIN_MANAGER_DESC,
    CRAWL_MANAGER_DESC,
    // start database manager before any manager that uses it.
    METADATA_DB_MANAGER_DESC,
    // start metadata manager after pluggin manager and database manager.
    METADATA_MANAGER_DESC,
    // Start the job manager.
    JOB_DB_MANAGER_DESC,
    JOB_MANAGER_DESC,
    // Start the COUNTER reports manager.
    COUNTER_REPORTS_MANAGER_DESC,
    // NOTE: Any managers that are needed to decide whether a servlet is to be
    // enabled or not (through ServletDescr.isEnabled()) need to appear before
    // the AdminServletManager on the next line.
    SERVLET_MANAGER_DESC,
  };

  /**
   * The entry point of the application.
   *
   * @param args
   *          A String[] with the command line arguments.
   */
  public static void main(String[] args) {
    logger.info("Starting the application");
    configure();

    // Start the REST service.
    SpringApplication.run(MdxApplication.class, args);
  }

  /**
   * Callback used to run the application starting the LOCKSS daemon.
   *
   * @param args
   *          A String[] with the command line arguments.
   */
  public void run(String... args) {
    // Check whether there are command line arguments available.
    if (args != null && args.length > 0) {
      // Yes: Start the LOCKSS daemon.
      logger.info("Starting the LOCKSS daemon");
      AppSpec spec = new AppSpec()
	.setName("Metadate Extractor Service")
	.setArgs(args)
	.addAppConfig(LockssDaemon.PARAM_START_PLUGINS, "true")
	.setAppManagers(myManagerDescs);
      LockssApp.startStatic(LockssDaemon.class, spec);
    } else {
      // No: Do nothing. This happens when a test is started and before the
      // test setup has got a chance to inject the appropriate command line
      // parameters.
    }
  }
}
