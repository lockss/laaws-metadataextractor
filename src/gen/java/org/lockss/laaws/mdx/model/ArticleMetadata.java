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
package org.lockss.laaws.mdx.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.lockss.metadata.AuMetadataDetail.ArticleMetadataDetail;

/**
 * The metadata generated from a single article
 **/
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public class ArticleMetadata   {
  
  private Map<String, String> scalarMap = new HashMap<String, String>();
  private Map<String, List<String>> listMap =
	new HashMap<String, List<String>>();

  private Map<String, Map<String, String>> mapMap =
	new HashMap<String, Map<String, String>>();

  public ArticleMetadata(ArticleMetadataDetail articleMd) {
    scalarMap = articleMd.getScalarMap();
    listMap = articleMd.getListMap();
    mapMap = articleMd.getMapMap();
  }

  /**
   * The map of scalar metadata elements for this article.
   **/
  @JsonProperty("scalarMap")
  public Map<String, String> getScalarMap() {
    return scalarMap;
  }
  public void setScalarMap(Map<String, String> scalarMap) {
    this.scalarMap = scalarMap;
  }

  /**
   * The map of listed metadata elements for this article.
   **/
  @JsonProperty("listMap")
  public Map<String, List<String>> getListMap() {
    return listMap;
  }
  public void setListMap(Map<String, List<String>> listMap) {
    this.listMap = listMap;
  }

  /**
   * The map of mapped metadata elements for this article.
   **/
  @JsonProperty("mapMap")
  public Map<String, Map<String, String>> getMapMap() {
    return mapMap;
  }
  public void setMapMap(Map<String, Map<String, String>> mapMap) {
    this.mapMap = mapMap;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArticleMetadata articleMetadata = (ArticleMetadata) o;
    return Objects.equals(scalarMap, articleMetadata.scalarMap)
	&& Objects.equals(listMap, articleMetadata.listMap)
	&& Objects.equals(mapMap, articleMetadata.mapMap);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scalarMap, listMap, mapMap);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ArticleMetadata {\n");
    sb.append("    scalarMap: ").append(toIndentedString(scalarMap)).
    append("\n");
    sb.append("    listMap: ").append(toIndentedString(listMap)).append("\n");
    sb.append("    mapMap: ").append(toIndentedString(mapMap)).append("\n");
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
