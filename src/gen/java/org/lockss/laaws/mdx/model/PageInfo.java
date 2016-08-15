package org.lockss.snafl.mdx.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * The information related to paging in the content
 **/

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public class PageInfo   {
  
  private Integer totalCount = null;
  private Integer resultsPerPage = null;
  private Integer currentPage = null;
  private String curLink = null;
  private String nextLink = null;

  
  /**
   * The total number of elements to be paged in.
   **/
  
  @JsonProperty("totalCount")
  public Integer getTotalCount() {
    return totalCount;
  }
  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
  }

  
  /**
   * number of results per page
   **/
  
  @JsonProperty("resultsPerPage")
  public Integer getResultsPerPage() {
    return resultsPerPage;
  }
  public void setResultsPerPage(Integer resultsPerPage) {
    this.resultsPerPage = resultsPerPage;
  }

  
  /**
   * The current page number
   **/
  
  @JsonProperty("currentPage")
  public Integer getCurrentPage() {
    return currentPage;
  }
  public void setCurrentPage(Integer currentPage) {
    this.currentPage = currentPage;
  }

  
  /**
   * The current link
   **/
  
  @JsonProperty("curLink")
  public String getCurLink() {
    return curLink;
  }
  public void setCurLink(String curLink) {
    this.curLink = curLink;
  }

  
  /**
   * The next link
   **/
  
  @JsonProperty("nextLink")
  public String getNextLink() {
    return nextLink;
  }
  public void setNextLink(String nextLink) {
    this.nextLink = nextLink;
  }

  

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PageInfo pageInfo = (PageInfo) o;
    return Objects.equals(totalCount, pageInfo.totalCount) &&
        Objects.equals(resultsPerPage, pageInfo.resultsPerPage) &&
        Objects.equals(currentPage, pageInfo.currentPage) &&
        Objects.equals(curLink, pageInfo.curLink) &&
        Objects.equals(nextLink, pageInfo.nextLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalCount, resultsPerPage, currentPage, curLink, nextLink);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PageInfo {\n");
    
    sb.append("    totalCount: ").append(toIndentedString(totalCount)).append("\n");
    sb.append("    resultsPerPage: ").append(toIndentedString(resultsPerPage)).append("\n");
    sb.append("    currentPage: ").append(toIndentedString(currentPage)).append("\n");
    sb.append("    curLink: ").append(toIndentedString(curLink)).append("\n");
    sb.append("    nextLink: ").append(toIndentedString(nextLink)).append("\n");
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

