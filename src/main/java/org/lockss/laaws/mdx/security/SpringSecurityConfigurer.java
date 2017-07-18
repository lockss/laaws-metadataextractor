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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

/**
 * Custom Spring security configurator.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfigurer extends WebSecurityConfigurerAdapter {
  private final static Logger logger =
      LoggerFactory.getLogger(SpringSecurityConfigurer.class);

  /**
   * Configures the authentication strategy and filter.
   * 
   * @param http
   *          An HttpSecurity to be configured.
   * @throws Exception
   *           if there are problems.
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    if (logger.isDebugEnabled()) logger.debug("Invoked.");

    // Force each and every request to be authenticated.
    http.csrf().disable().authorizeRequests().anyRequest().authenticated();

    // The Basic authentication filter to be used.
    http.addFilterBefore(new SpringAuthenticationFilter(),
	BasicAuthenticationFilter.class);
    if (logger.isDebugEnabled()) logger.debug("Done.");
  }

  @Bean
  public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
      DefaultHttpFirewall firewall = new DefaultHttpFirewall();
      firewall.setAllowUrlEncodedSlash(true);
      return firewall;
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
      web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
  }}
