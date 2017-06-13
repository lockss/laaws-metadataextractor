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
package org.lockss.laaws.mdx.model;

import java.util.Objects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.lockss.job.JobAuStatus;

/**
 * An Archival Unit.
 **/
@ApiModel(description = "An Archival Unit")
public class Au   {
  
  private String id = null;
  private String job = null;
  private String name = null;

  /**
   * Default constructor.
   */
  public Au() {

  }

  /**
   * Full constructor.
   * 
   * @param jobAuStatus
   *          A JobAuStatus with the job status.
   */
  public Au(JobAuStatus jobAuStatus) {
    id = jobAuStatus.getAuId();
    job = jobAuStatus.getId();
    name = jobAuStatus.getAuName();
  }
  
  /**
   * The identifier of this AU.
   **/
  @ApiModelProperty(required = true, value = "The identifier of this AU")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * The identifier of the job associated with this AU.
   **/
  @ApiModelProperty(value =
      "The identifier of the job associated with this AU")
  public String getJob() {
    return job;
  }
  public void setJob(String job) {
    this.job = job;
  }

  /**
   * The name of the AU, for display purposes.
   **/
  @ApiModelProperty(value = "The name of the AU, for display purposes")
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
