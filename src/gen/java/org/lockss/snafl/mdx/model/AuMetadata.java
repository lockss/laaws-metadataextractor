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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.lockss.metadata.AuMetadataDetail;
import org.lockss.metadata.AuMetadataDetail.ArticleMetadataDetail;

/**
 * The metadata as extracted from the metadata db for this au
 **/
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public class AuMetadata   {
  
  private List<ArticleMetadata> auMetadataList =
      new ArrayList<ArticleMetadata>();

  public AuMetadata(AuMetadataDetail auMd) {
    for (ArticleMetadataDetail articleMd : auMd.getArticles()) {
      auMetadataList.add(new ArticleMetadata(articleMd));
    }
  }
  
  /**
   * The list of article metadata for this au
   **/
  @JsonProperty("auMetadataList")
  public List<ArticleMetadata> getAuMetadataList() {
    return auMetadataList;
  }
  public void setAuMetadataList(List<ArticleMetadata> auMetadataList) {
    this.auMetadataList = auMetadataList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuMetadata auMetadata = (AuMetadata) o;
    return Objects.equals(auMetadataList, auMetadata.auMetadataList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(auMetadataList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuMetadata {\n");
    
    sb.append("    auMetadataList: ").append(toIndentedString(auMetadataList))
    .append("\n");
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
