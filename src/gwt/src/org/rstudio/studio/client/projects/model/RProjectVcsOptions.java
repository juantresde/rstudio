/*
 * RProjectVcsOptions.java
 *
 * Copyright (C) 2009-11 by RStudio, Inc.
 *
 * This program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */
package org.rstudio.studio.client.projects.model;

import com.google.gwt.core.client.JavaScriptObject;

public class RProjectVcsOptions extends JavaScriptObject
{
   protected RProjectVcsOptions()
   {
   }
   
   public native static final RProjectVcsOptions createEmpty() /*-{
      var options = new Object();
      return options;
   }-*/;
   
   public native final String getActiveVcsOverride() /*-{
      return this.active_vcs_override;
   }-*/;
   
   public native final void setActiveVcsOverride(String override) /*-{
      this.active_vcs_override = override;
   }-*/;

   public native final String getSshKeyPathOverride() /*-{
      return this.ssh_key_path_override;
   }-*/;

   public native final void setSshKeyPathOverride(String override) /*-{
      this.ssh_key_path_override = override;
   }-*/;
   
}
