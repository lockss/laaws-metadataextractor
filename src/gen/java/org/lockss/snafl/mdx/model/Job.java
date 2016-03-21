package org.lockss.snafl.mdx.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.LocalDate;



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
    return Objects.hash(au, id, description, creationDate, startDate, endDate, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Job {\n");
    
    sb.append("    au: ").append(toIndentedString(au)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    creationDate: ").append(toIndentedString(creationDate)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
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

