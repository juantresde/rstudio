/*
 * ActiveTerminalToolbarButton.java
 *
 * Copyright (C) 2009-17 by RStudio, Inc.
 *
 * Unless you have received this program directly from RStudio pursuant
 * to the terms of a commercial license agreement with RStudio, then
 * this program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */

package org.rstudio.studio.client.workbench.views.terminal;

import org.rstudio.core.client.command.AppCommand;
import org.rstudio.core.client.widget.ToolbarButton;
import org.rstudio.core.client.widget.ToolbarPopupMenu;
import org.rstudio.studio.client.RStudioGinjector;
import org.rstudio.studio.client.application.events.EventBus;
import org.rstudio.studio.client.common.icons.StandardIcons;
import org.rstudio.studio.client.workbench.commands.Commands;
import org.rstudio.studio.client.workbench.views.terminal.events.SwitchToTerminalEvent;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.inject.Inject;

/**
 * Drop-down menu used in terminal pane. Has commands, and a list of 
 * terminal sessions.
 */
public class TerminalPopupMenu extends ToolbarPopupMenu
{
   public TerminalPopupMenu(TerminalList terminals)
   {
      RStudioGinjector.INSTANCE.injectMembers(this);
      terminals_ = terminals;
   }

   @Inject
   private void initialize(Commands commands,
                           EventBus events)
   {
      commands_ = commands;
      eventBus_ = events;
   }
   
   @Override
   public void getDynamicPopupMenu(final DynamicPopupMenuCallback callback)
   { 
      // clean out existing entries
      clearItems(); 
      addItem(commands_.newTerminal().createMenuItem(false));
      addSeparator();

      if (terminals_.terminalCount() > 0)
      {
         for (final String handle : terminals_)
         {
            Scheduler.ScheduledCommand cmd = new Scheduler.ScheduledCommand()
            {
               @Override
               public void execute()
               {
                  eventBus_.fireEvent(new SwitchToTerminalEvent(handle));
               }
            };

            String caption = trimCaption(terminals_.getCaption(handle));
            if (caption == null)
            {
               continue;
            }

            // visual indicator that terminal has processes running
            if (terminals_.getHasSubprocs(handle))
            {
               caption = caption + "*";
            }

            String menuHtml = AppCommand.formatMenuLabel(
                  null,              /*icon*/
                  caption,           /*label*/
                  false,             /*html*/
                  null,              /*shortcut*/
                  null,              /*rightImage*/
                  null);             /*rightImageDesc*/
            addItem(new MenuItem(menuHtml, true, cmd));
         }
         addSeparator();
         addItem(commands_.renameTerminal().createMenuItem(false));
         addItem(commands_.clearTerminalScrollbackBuffer().createMenuItem(false));
         addSeparator();
         addItem(commands_.previousTerminal().createMenuItem(false));
         addItem(commands_.nextTerminal().createMenuItem(false));
         addSeparator();
         addItem(commands_.closeTerminal().createMenuItem(false));
      }

      callback.onPopupMenu(this);
   }
   
   public ToolbarButton getToolbarButton()
   {
      if (toolbarButton_ == null)
      {
         String buttonText = "Terminal";
         
         toolbarButton_ = new ToolbarButton(
                buttonText, 
                StandardIcons.INSTANCE.empty_command(),
                this, 
                false);

         setNoActiveTerminal();
      }
      return toolbarButton_;
   }
   
   /**
    *       
    * @param caption caption of the active terminal
    * @param handle handle of the active terminal
    */
   public void setActiveTerminal(String caption, String handle)
   {
      activeTerminalCaption_ = caption;
      activeTerminalHandle_ = handle;
      toolbarButton_.setText(trimCaption(activeTerminalCaption_));
   }
   
   /**
    * set state to indicate no active terminals
    */
   public void setNoActiveTerminal()
   {
      setActiveTerminal("Terminal", null);
   }

   /**
    * @return Handle of active terminal, or null if no active terminal.
    */
   public String getActiveTerminalHandle()
   {
      return activeTerminalHandle_;
   }

   /**
    * Switch to previous terminal tab.
    */
   public void previousTerminal()
   {
      if (terminals_.terminalCount() > 0 && activeTerminalHandle_ != null)
      {
         String prevHandle = null;
         for (final String handle : terminals_)
         {
            if (activeTerminalHandle_.equals(handle))
            {
               if (prevHandle == null)
               {
                  return;
               }
               eventBus_.fireEvent(new SwitchToTerminalEvent(prevHandle));
               return;
            }
            else
            {
               prevHandle = handle;
            }
         }
      }
   }

   /**
    * Switch to next terminal tab.
    */
   public void nextTerminal()
   {
      if (terminals_.terminalCount() > 0 && activeTerminalHandle_ != null)
      {
         boolean foundCurrent = false;
         for (final String handle : terminals_)
         {
            if (foundCurrent == true)
            {
               eventBus_.fireEvent(new SwitchToTerminalEvent(handle));
               return;
            }
            if (activeTerminalHandle_.equals(handle))
            {
               foundCurrent = true;
            }
         }
      }
   }

   private String trimCaption(String caption)
   {
      // TODO (gary) look at doing this via css text-overflow
      // when I do the theming work

      // Enforce a sane visual limit on terminal captions
      if (caption.length() > 32)
      {
         caption = caption.substring(0, 31) + "...";
      }
      return caption;
   }

   private ToolbarButton toolbarButton_;
   private Commands commands_;
   private EventBus eventBus_;
   private String activeTerminalCaption_;
   private String activeTerminalHandle_;
   private TerminalList terminals_;
}
