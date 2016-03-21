package org.lockss.snafl.mdx.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * The AU descriptor
 **/

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public class Au   {
  
  private String id = null;
  private String job = null;
  private String name = null;

  
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

