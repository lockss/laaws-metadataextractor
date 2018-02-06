/*

 Copyright (c) 2016-2018 Board of Trustees of Leland Stanford Jr. University,
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

import java.util.Date;
import java.util.Objects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.lockss.job.JobAuStatus;

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
