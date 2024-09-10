/*

Copyright (c) 2000-2019 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package org.lockss.laaws.mdx;

import static org.lockss.app.ManagerDescs.*;
import org.lockss.app.LockssApp;
import org.lockss.app.LockssApp.AppSpec;
import org.lockss.app.LockssApp.ManagerDesc;
import org.lockss.app.LockssDaemon;
import org.lockss.app.ServiceDescr;
import org.lockss.crawler.CrawlManagerImpl;
import org.lockss.plugin.*;
import org.lockss.metadata.extractor.MetadataExtractorManager;
import org.lockss.metadata.extractor.job.JobDbManager;
import org.lockss.metadata.extractor.job.JobManager;
import org.lockss.metadata.query.MetadataQueryManager;
import org.lockss.spring.base.BaseSpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Spring-Boot application.
 */
@SpringBootApplication()
public class MdxApplication extends BaseSpringBootApplication
	implements CommandLineRunner {
  private static final Logger logger =
      LoggerFactory.getLogger(MdxApplication.class);

  // Manager descriptors.  The order of this table determines the order in
  // which managers are initialized and started.
  protected final static ManagerDesc[] myManagerDescs = {
    ACCOUNT_MANAGER_DESC,
    CONFIG_DB_MANAGER_DESC,
    // start plugin manager after generic services
    PLUGIN_MANAGER_DESC,
    STATE_MANAGER_DESC,
    CRAWL_MANAGER_DESC,
    REPOSITORY_MANAGER_DESC,
    // start database manager before any manager that uses it.
    METADATA_DB_MANAGER_DESC,
    // start metadata manager after plugin manager and database manager.
    METADATA_MANAGER_DESC,
    new ManagerDesc(LockssDaemon.managerKey(MetadataQueryManager.class),
	"org.lockss.metadata.query.MetadataQueryManager"),
    new ManagerDesc(LockssDaemon.managerKey(MetadataExtractorManager.class),
	"org.lockss.metadata.extractor.MetadataExtractorManager"),
    // Start the job database manager.
    new ManagerDesc(LockssDaemon.managerKey(JobDbManager.class),
	"org.lockss.metadata.extractor.job.JobDbManager"),
    // Start the job manager.
    new ManagerDesc(LockssDaemon.managerKey(JobManager.class),
	"org.lockss.metadata.extractor.job.JobManager"),
    // NOTE: Any managers that are needed to decide whether a servlet is to be
    // enabled or not (through ServletDescr.isEnabled()) need to appear before
    // the AdminServletManager on the next line.
    SERVLET_MANAGER_DESC,
    PLATFORM_CONFIG_STATUS_DESC,
    CONFIG_STATUS_DESC,
    ARCHIVAL_UNIT_STATUS_DESC,
    OVERVIEW_STATUS_DESC
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
      logger.info("Starting the LOCKSS Metadata Extractor Service");

      AppSpec spec = new AppSpec()
	.setService(ServiceDescr.SVC_MDX)
	.setArgs(args)
	.addAppConfig(LockssDaemon.PARAM_START_PLUGINS, "true")
	.addAppConfig(PluginManager.PARAM_START_ALL_AUS, "false")
	.addAppConfig(LockssDaemon.PARAM_CRAWL_MODE, "None")
	.addAppConfig(CrawlManagerImpl.PARAM_CRAWLER_ENABLED, "false")
	.addAppConfig(CrawlManagerImpl.PARAM_CRAWL_STARTER_ENABLED, "false")
	.setSpringApplicatonContext(getApplicationContext())
	.setAppManagers(myManagerDescs);
      LockssApp.startStatic(LockssDaemon.class, spec);
    } else {
      // No: Do nothing. This happens when a test is started and before the
      // test setup has got a chance to inject the appropriate command line
      // parameters.
    }
  }
}
