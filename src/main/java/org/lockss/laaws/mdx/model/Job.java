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

import java.util.Date;
import java.util.Objects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.lockss.metadata.extractor.job.JobAuStatus;

/**
 * An asynchronous task to be performed.
 **/
@ApiModel(description = "An asynchronous task to be performed")
public class Job   {
  
  private Au au = null;
  private String id = null;
  private String description = null;
  private Date creationDate = null;
  private Date startDate = null;
  private Date endDate = null;
  private Status status = null;

  /**
   * Default constructor.
   */
  public Job() {

  }

  /**
   * Full constructor.
   * 
   * @param jobAuStatus
   *          A JobAuStatus with the job status.
   */
  public Job(JobAuStatus jobAuStatus) {
    if (jobAuStatus != null) {
      au = new Au(jobAuStatus);
      id = jobAuStatus.getId();
      description = jobAuStatus.getDescription();
      creationDate = jobAuStatus.getCreationDate();
      startDate = jobAuStatus.getStartDate();
      endDate = jobAuStatus.getEndDate();
      status = new Status(jobAuStatus);
    }
  }

  public Au getAu() {
    return au;
  }
  public void setAu(Au au) {
    this.au = au;
  }

  /**
   * The identifier of this job.
   *
   * @return a String with the job identifier.
   */
  @ApiModelProperty(required = true, value = "The identifier of this job")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * A description of the task being performed by this job.
   *
   * @return a String with the job description.
   */
  @ApiModelProperty(value =
      "A description of the task being performed by this job")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * The timestamp when this job was created.
   *
   * @return a Date with the job creation timestamp.
   */
  @ApiModelProperty(required = true,
      value = "The timestamp when this job was created")
  public Date getCreationDate() {
    return creationDate;
  }
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * The timestamp when this job processing started.
   *
   * @return a Date with the job start timestamp.
   */
  @ApiModelProperty(value = "The timestamp when this job processing started")
  public Date getStartDate() {
    return startDate;
  }
  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  /**
   * The timestamp when this job processing ended.
   *
   * @return a Date with the job end timestamp.
   */
  @ApiModelProperty(value = "The timestamp when this job processing ended")
  public Date getEndDate() {
    return endDate;
  }
  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Status getStatus() {
    return status;
  }
  public void setStatus(Status status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Job job = (Job) o;
    return Objects.equals(au, job.au) &&
        Objects.equals(id, job.id) &&
        Objects.equals(description, job.description) &&
        Objects.equals(creationDate, job.creationDate) &&
        Objects.equals(startDate, job.startDate) &&
        Objects.equals(endDate, job.endDate) &&
        Objects.equals(status, job.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(au, id, description, creationDate, startDate, endDate,
	status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Job {\n");
    
    sb.append("    au: ").append(toIndentedString(au)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    description: ").append(toIndentedString(description))
    .append("\n");
    sb.append("    creationDate: ").append(toIndentedString(creationDate))
    .append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate))
    .append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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
