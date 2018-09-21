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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/**
 * The information related to pagination of content.
 **/
@ApiModel(description = "The information related to pagination of content")
public class PageInfo   {
  private Integer totalCount = null;
  private Integer resultsPerPage = null;
  private String continuationToken = null;
  private String curLink = null;
  private String nextLink = null;

  /**
   * The total number of elements to be paginated.
   * 
   * @return an Integer with the total number of elements to be paginated.
   */
  @ApiModelProperty(required = true,
      value = "The total number of elements to be paginated")
  public Integer getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
  }

  /**
   * The number of results per page.
   * 
   * @return an Integer with the number of results per page.
   */
  @ApiModelProperty(required = true, value = "The number of results per page")
  public Integer getResultsPerPage() {
    return resultsPerPage;
  }

  public void setResultsPerPage(Integer resultsPerPage) {
    this.resultsPerPage = resultsPerPage;
  }

  /**
   * The continuation token.
   * 
   * @return A String with the continuation token.
   **/
  @ApiModelProperty(required = true, value = "The continuation token")
  public String getContinuationToken() {
    return continuationToken;
  }

  public void setContinuationToken(String continuationToken) {
    this.continuationToken = continuationToken;
  }

  /**
   * The link to the current page.
   * 
   * @return a String with the link to the current page.
   */
  @ApiModelProperty(required = true, value = "The link to the current page")
  public String getCurLink() {
    return curLink;
  }

  public void setCurLink(String curLink) {
    this.curLink = curLink;
  }
  
  /**
   * The link to the next page.
   * 
   * @return a String with the link to the next page.
   */
  @ApiModelProperty(required = true, value = "The link to the next page")
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
        Objects.equals(continuationToken, pageInfo.continuationToken) &&
        Objects.equals(curLink, pageInfo.curLink) &&
        Objects.equals(nextLink, pageInfo.nextLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalCount, resultsPerPage, continuationToken, curLink,
	nextLink);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PageInfo {\n");
    sb.append("    totalCount: ").append(toIndentedString(totalCount))
    .append("\n");
    sb.append("    resultsPerPage: ").append(toIndentedString(resultsPerPage))
    .append("\n");
    sb.append("    continuationToken: ")
    .append(toIndentedString(continuationToken)).append("\n");
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
