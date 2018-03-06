/*

Copyright (c) 2000-2018 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.laaws.mdx.model;

import java.util.Objects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.lockss.metadata.extractor.job.JobAuStatus;

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
   * 
   * @return a String with the AU identifier.
   */
  @ApiModelProperty(required = true, value = "The identifier of this AU")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * The identifier of the job associated with this AU.
   *
   * @return a String with the job identifier.
   */
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
   *
   * @return a String with the AU name.
   */
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
