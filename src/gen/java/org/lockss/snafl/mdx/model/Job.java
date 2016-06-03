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
import org.joda.time.LocalDate;
import org.lockss.job.JobAuStatus;

/**
 * A job or task to be performed
 **/
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public class Job   {
  
  private Au au = null;
  private String id = null;
  private String description = null;
  private LocalDate creationDate = null;
  private LocalDate startDate = null;
  private LocalDate endDate = null;
  private Status status = null;

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

  /**
   **/
  @JsonProperty("au")
  public Au getAu() {
    return au;
  }
  public void setAu(Au au) {
    this.au = au;
  }
  
  /**
   * The id for this job
   **/
  @JsonProperty("id")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * A short description of job being performed
   **/
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * The date the job entered the queue
   **/
  @JsonProperty("creationDate")
  public LocalDate getCreationDate() {
    return creationDate;
  }
  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * The date-time the job began processing
   **/
  @JsonProperty("startDate")
  public LocalDate getStartDate() {
    return startDate;
  }
  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  /**
   * The date-time for when the job ended with or without error
   **/
  @JsonProperty("endDate")
  public LocalDate getEndDate() {
    return endDate;
  }
  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  /**
   **/
  @JsonProperty("status")
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
