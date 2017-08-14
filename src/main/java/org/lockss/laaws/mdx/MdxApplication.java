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
package org.lockss.laaws.mdx;

import org.lockss.laaws.mdx.server.LaawsMdxApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.UrlPathHelper;

/**
 * The Spring-Boot application.
 */
@SpringBootApplication
public class MdxApplication extends WebMvcConfigurerAdapter implements CommandLineRunner {
  private static final Logger logger =
      LoggerFactory.getLogger(MdxApplication.class);

  /**
   * The entry point of the application.
   *
   * @param args A String[] with the command line arguments.
   */
  public static void main(String[] args) {
    logger.info("Starting the application");
    System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
    // Start the REST service.
    SpringApplication.run(MdxApplication.class, args);
  }

  /**
   * Starts the LOCKSS daemon.
   */
  public void run(String... args) {
    // Check whether there are command line arguments available.
    if (args != null && args.length > 0) {
      // Yes: Start the LOCKSS daemon.
      logger.info("Starting the LOCKSS daemon");
      LaawsMdxApp.main(args);
    } else {
      // No: Do nothing. This happens when a test is started and before the
      // test setup has got a chance to inject the appropriate command line
      // parameters.
    }
  }

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
      UrlPathHelper urlPathHelper = new UrlPathHelper();
      urlPathHelper.setUrlDecode(false);
      configurer.setUrlPathHelper(urlPathHelper);
  }
}