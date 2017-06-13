/*

 Copyright (c) 2017 Board of Trustees of Leland Stanford Jr. University,
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

import java.util.Objects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * The information defining an AU metadata update operation.
 */
@ApiModel(description =
"The information defining an AU metadata update operation")
public class MetadataUpdateSpec {

  private String auid = null;
  private String updateType = null;

  /**
   * The identifier of the AU for which the metadata is to be reindexed.
   **/
  @ApiModelProperty(required = true, value =
      "The identifier of the AU for which the metadata update is to be performed")
  public String getAuid() {
    return auid;
  }
  public void setAuid(String auid) {
    this.auid = auid;
  }

  /**
   * The type of metadata update to be performed.
   **/
  @ApiModelProperty(required = true,
      value = "The type of metadata update to be performed")
  public String getUpdateType() {
    return updateType;
  }
  public void setUpdateType(String updateType) {
    this.updateType = updateType;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetadataUpdateSpec reindexParams = (MetadataUpdateSpec) o;
    return Objects.equals(this.auid, reindexParams.auid) &&
        Objects.equals(this.updateType, reindexParams.updateType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(auid, updateType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReindexParams {\n");
    
    sb.append("    auid: ").append(toIndentedString(auid)).append("\n");
    sb.append("    updateType: ").append(toIndentedString(updateType))
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
