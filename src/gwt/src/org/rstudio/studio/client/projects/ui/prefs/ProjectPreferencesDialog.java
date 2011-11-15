/*
 * ProjectPreferencesDialog.java
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
package org.rstudio.studio.client.projects.ui.prefs;

import org.rstudio.core.client.prefs.PreferencesDialogBase;
import org.rstudio.core.client.widget.Operation;
import org.rstudio.core.client.widget.ProgressIndicator;
import org.rstudio.studio.client.projects.model.ProjectsServerOperations;
import org.rstudio.studio.client.projects.model.RProjectConfig;
import org.rstudio.studio.client.projects.model.RProjectOptions;
import org.rstudio.studio.client.server.ServerError;
import org.rstudio.studio.client.server.ServerRequestCallback;
import org.rstudio.studio.client.server.Void;
import org.rstudio.studio.client.workbench.model.Session;
import org.rstudio.studio.client.workbench.prefs.model.UIPrefs;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class ProjectPreferencesDialog extends PreferencesDialogBase<RProjectOptions>
{
   @Inject
   public ProjectPreferencesDialog(ProjectsServerOperations server,
                                   Provider<UIPrefs> pUIPrefs,
                                   Session session,
                                   ProjectGeneralPreferencesPane general,
                                   ProjectEditingPreferencesPane editing,
                                   ProjectSourceControlPreferencesPane source)
   {
      super("Project Options",
            RES.styles().panelContainer(),
            false,
            new ProjectPreferencesPane[] {general, editing, source});
      
      server_ = server;
      pUIPrefs_ = pUIPrefs;  
   }
   
   public void activateSourceControl()
   {
      activatePane(2);
   }
   
   
   @Override
   protected RProjectOptions createEmptyPrefs()
   {
      return RProjectOptions.createEmpty();
   }
   
   
   @Override
   protected void doSaveChanges(final RProjectOptions options,
                                final Operation onCompleted,
                                final ProgressIndicator indicator)
   {
      
      server_.writeProjectOptions(
          options, 
          new ServerRequestCallback<Void>() {
             @Override
             public void onResponseReceived(Void response)
             {
                indicator.onCompleted();
                
                // update project ui prefs
                RProjectConfig config = options.getConfig();
                UIPrefs uiPrefs = pUIPrefs_.get();
                uiPrefs.useSpacesForTab().setProjectValue(
                                           config.getUseSpacesForTab());
                uiPrefs.numSpacesForTab().setProjectValue(
                                           config.getNumSpacesForTab());
                uiPrefs.defaultEncoding().setProjectValue(
                                           config.getEncoding());   
                
                if (onCompleted != null)
                   onCompleted.execute();
             }

             @Override
             public void onError(ServerError error)
             {
                indicator.onError(error.getUserMessage());
             }         
          });
      
   }

   
   private final ProjectsServerOperations server_;
   private final Provider<UIPrefs> pUIPrefs_;
   
   private static final ProjectPreferencesDialogResources RES =
                                 ProjectPreferencesDialogResources.INSTANCE;


  
}
