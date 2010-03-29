/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.opencmis.client.runtime.repository;

import java.io.Serializable;

import org.apache.opencmis.client.api.repository.RepositoryCapabilities;
import org.apache.opencmis.commons.enums.CapabilityAcl;
import org.apache.opencmis.commons.enums.CapabilityChanges;
import org.apache.opencmis.commons.enums.CapabilityContentStreamUpdates;
import org.apache.opencmis.commons.enums.CapabilityJoin;
import org.apache.opencmis.commons.enums.CapabilityQuery;
import org.apache.opencmis.commons.enums.CapabilityRendition;
import org.apache.opencmis.commons.provider.RepositoryCapabilitiesData;

public class RepositoryCapabilitiesImpl implements RepositoryCapabilities, Serializable {

  private static final long serialVersionUID = 1L;

  private RepositoryCapabilitiesData capabilities = null;

  /**
   * Constructor.
   */
  public RepositoryCapabilitiesImpl(RepositoryCapabilitiesData rcd) {
    this.capabilities = rcd;
  }

  public CapabilityAcl getAclSupport() {
    return this.capabilities.getCapabilityAcl();
  }

  public CapabilityChanges getChangesSupport() {
    return this.capabilities.getCapabilityChanges();
  }

  public CapabilityContentStreamUpdates getContentStreamUpdatabilitySupport() {
    return this.capabilities.getCapabilityContentStreamUpdatability();
  }

  public CapabilityJoin getJoinSupport() {
    return this.capabilities.getCapabilityJoin();
  }

  public CapabilityQuery getQuerySupport() {
    return this.capabilities.getCapabilityQuery();
  }

  public CapabilityRendition getRenditionsSupport() {
    return this.capabilities.getCapabilityRenditions();
  }

  public Boolean isAllVersionsSearchableSupported() {
    return this.capabilities.allVersionsSearchable();
  }

  public Boolean isGetDescendantsSupported() {
    return this.capabilities.supportsGetDescendants();
  }

  public Boolean isGetFolderTreeSupported() {
    return this.capabilities.supportsGetFolderTree();
  }

  public Boolean isMultifilingSupported() {
    return this.capabilities.supportsMultifiling();
  }

  public Boolean isPwcSearchableSupported() {
    return this.capabilities.isPwcSearchable();
  }

  public Boolean isPwcUpdatableSupported() {
    return this.capabilities.isPwcUpdatable();
  }

  public Boolean isUnfilingSupported() {
    return this.capabilities.supportsUnfiling();
  }

  public Boolean isVersionSpecificFilingSupported() {
    return this.capabilities.supportsVersionSpecificFiling();
  }
}