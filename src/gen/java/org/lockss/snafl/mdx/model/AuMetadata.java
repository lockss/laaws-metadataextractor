package org.lockss.snafl.mdx.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * The metadata as extracted from the metadata db for this au
 **/

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-03-20T22:32:10.024-07:00")
public class AuMetadata   {
  
  private List<ArticleMetadata> auMetadataList = new ArrayList<ArticleMetadata>();

  
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
    
    sb.append("    auMetadataList: ").append(toIndentedString(auMetadataList)).append("\n");
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

