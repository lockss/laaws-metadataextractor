package org.lockss.snafl.mdx.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * The metadata generated from a single article
 **/

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public class ArticleMetadata   {
  
  private List<MetadataElement> articleMetadataList = new ArrayList<MetadataElement>();

  
  /**
   * The list of metadata elements for this article
   **/
  
  @JsonProperty("articleMetadataList")
  public List<MetadataElement> getArticleMetadataList() {
    return articleMetadataList;
  }
  public void setArticleMetadataList(List<MetadataElement> articleMetadataList) {
    this.articleMetadataList = articleMetadataList;
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
    return Objects.equals(articleMetadataList, articleMetadata.articleMetadataList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(articleMetadataList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ArticleMetadata {\n");
    sb.append("    articleMetadataList: ").append(toIndentedString(articleMetadataList)).append("\n");
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

