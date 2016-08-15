package org.lockss.laaws.mdx.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;




@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public class JobPageInfo   {
  
  private List<Job> jobs = new ArrayList<Job>();
  private PageInfo pageInfo = null;

  
  /**
   * The list of jobs currently running
   **/
  
  @JsonProperty("jobs")
  public List<Job> getJobs() {
    return jobs;
  }
  public void setJobs(List<Job> jobs) {
    this.jobs = jobs;
  }

  
  /**
   **/
  
  @JsonProperty("pageInfo")
  public PageInfo getPageInfo() {
    return pageInfo;
  }
  public void setPageInfo(PageInfo pageInfo) {
    this.pageInfo = pageInfo;
  }

  

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JobPageInfo jobPageInfo = (JobPageInfo) o;
    return Objects.equals(jobs, jobPageInfo.jobs) &&
        Objects.equals(pageInfo, jobPageInfo.pageInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jobs, pageInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class JobPageInfo {\n");
    
    sb.append("    jobs: ").append(toIndentedString(jobs)).append("\n");
    sb.append("    pageInfo: ").append(toIndentedString(pageInfo)).append("\n");
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

