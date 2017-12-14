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
package org.lockss.laaws.mdx.server;

import org.lockss.app.LockssDaemon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Startup code.
 */
public class LaawsMdxApp extends LockssDaemon {
  private static final Logger log = LoggerFactory.getLogger(LaawsMdxApp.class);

  // Manager descriptors.  The order of this table determines the order in
  // which managers are initialized and started.
  protected final static ManagerDesc[] myManagerDescs = {
      new ManagerDesc(ACCOUNT_MANAGER, "org.lockss.account.AccountManager"),
      // start plugin manager after generic services
      new ManagerDesc(PLUGIN_MANAGER, "org.lockss.plugin.PluginManager"),
      new ManagerDesc(CRAWL_MANAGER, "org.lockss.crawler.CrawlManagerImpl"),
      // start database manager before any manager that uses it.
      new ManagerDesc(METADATA_DB_MANAGER,
	  "org.lockss.metadata.MetadataDbManager"),
      // start metadata manager after pluggin manager and database manager.
      new ManagerDesc(METADATA_MANAGER, "org.lockss.metadata.MetadataManager"),
      // Start the job manager.
      new ManagerDesc(JOB_DB_MANAGER,"org.lockss.job.JobDbManager"),
      new ManagerDesc(JOB_MANAGER, "org.lockss.job.JobManager"),
      // Start the COUNTER reports manager.
      new ManagerDesc(COUNTER_REPORTS_MANAGER,
	  "org.lockss.exporter.counter.CounterReportsManager"),
      // NOTE: Any managers that are needed to decide whether a servlet is to be
      // enabled or not (through ServletDescr.isEnabled()) need to appear before
      // the AdminServletManager on the next line.
      new ManagerDesc(SERVLET_MANAGER, "org.lockss.servlet.AdminServletManager")
  };

  public static void main( String[] args ) {
    AppSpec spec = new AppSpec()
      .setName("Metadate Extractor Service")
      .setArgs(args)
//       .addAppConfig(PluginManager.PARAM_START_ALL_AUS, "true")
      .addAppConfig(LockssDaemon.PARAM_START_PLUGINS, "true")
      .setAppManagers(myManagerDescs);
    startStatic(LaawsMdxApp.class, spec);
  }

  public LaawsMdxApp() throws Exception {
    super();
  }
}
