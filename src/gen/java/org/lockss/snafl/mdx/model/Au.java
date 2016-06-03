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
package org.lockss.snafl.mdx.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.lockss.job.JobAuStatus;

/**
 * The AU descriptor
 **/
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public class Au   {
  
  private String id = null;
  private String job = null;
  private String name = null;

  public Au(JobAuStatus jobAuStatus) {
    id = jobAuStatus.getAuId();
    job = jobAuStatus.getId();
    name = jobAuStatus.getAuName();
  }
  
  /**
   * The long fully qualified AU ID
   **/
    @JsonProperty("id")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * The job id associated with this au.
   **/
  @JsonProperty("job")
  public String getJob() {
    return job;
  }
  public void setJob(String job) {
    this.job = job;
  }

  /**
   * The name of the au in simple english for display.
   **/
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Au au = (Au) o;
    return Objects.equals(id, au.id) &&
        Objects.equals(job, au.job) &&
        Objects.equals(name, au.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, job, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Au {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    job: ").append(toIndentedString(job)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
