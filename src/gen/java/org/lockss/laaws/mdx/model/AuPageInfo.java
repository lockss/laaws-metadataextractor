package org.lockss.laaws.mdx.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;




@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public class AuPageInfo   {
  
  private List<Au> aus = new ArrayList<Au>();
  private PageInfo pageInfo = null;

  
  /**
   * The list of au in the db
   **/
  
  @JsonProperty("aus")
  public List<Au> getAus() {
    return aus;
  }
  public void setAus(List<Au> aus) {
    this.aus = aus;
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
    AuPageInfo auPageInfo = (AuPageInfo) o;
    return Objects.equals(aus, auPageInfo.aus) &&
        Objects.equals(pageInfo, auPageInfo.pageInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(aus, pageInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuPageInfo {\n");
    
    sb.append("    aus: ").append(toIndentedString(aus)).append("\n");
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

