#!/bin/bash
#
# Copyright (c) 2018-2019 Board of Trustees of Leland Stanford Jr. University,
# all rights reserved.
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
# STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
# WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
# IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#
# Except as contained in this notice, the name of Stanford University shall not
# be used in advertising or otherwise to promote the sale, use or other dealings
# in this Software without prior written authorization from Stanford University.
#

# Fixes the code generated by Swagger Codegen.

# Edit StatusApiDelegate.java.
STATUS_API_DELEGATE=src/generated/java/org/lockss/laaws/mdx/api/StatusApiDelegate.java
sed -i.backup "s/import org.lockss.laaws.mdx.model.ApiStatus/import org.lockss.laaws.status.model.ApiStatus/" $STATUS_API_DELEGATE && rm $STATUS_API_DELEGATE.backup

# Edit StatusApi.java.
STATUS_API=src/generated/java/org/lockss/laaws/mdx/api/StatusApi.java
sed -i.backup "s/import org.lockss.laaws.mdx.model.ApiStatus/import org.lockss.laaws.status.model.ApiStatus/" $STATUS_API && rm $STATUS_API.backup

# Edit the ApiDelegate.
API_DELEGATE=src/generated/java/org/lockss/laaws/mdx/api/MdupdatesApiDelegate.java
sed -i.backup "s/import org.lockss.laaws.mdx.model.Job;/import org.lockss.metadata.extractor.job.Job;/" $API_DELEGATE && rm $API_DELEGATE.backup
sed -i.backup "s/import org.lockss.laaws.mdx.model.Status;/import org.lockss.metadata.extractor.job.Status;/" $API_DELEGATE && rm $API_DELEGATE.backup

# Edit the Api.
API=src/generated/java/org/lockss/laaws/mdx/api/MdupdatesApi.java
sed -i.backup "s/import org.lockss.laaws.mdx.model.Job;/import org.lockss.metadata.extractor.job.Job;/" $API && rm $API.backup
sed -i.backup "s/import org.lockss.laaws.mdx.model.Status;/import org.lockss.metadata.extractor.job.Status;/" $API && rm $API.backup

# Edit JobPageInfo.java.
CLASS=src/generated/java/org/lockss/laaws/mdx/model/JobPageInfo.java
sed -i.backup "s/import org.lockss.laaws.mdx.model.Job;/import org.lockss.metadata.extractor.job.Job;/" $CLASS && rm $CLASS.backup
