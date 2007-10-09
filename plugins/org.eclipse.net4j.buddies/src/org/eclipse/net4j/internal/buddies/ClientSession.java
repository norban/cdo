/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.buddies;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.internal.protocol.BuddyContainer;
import org.eclipse.net4j.buddies.internal.protocol.BuddyStateNotification;
import org.eclipse.net4j.buddies.protocol.IAccount;
import org.eclipse.net4j.buddies.protocol.IBuddyStateChangedEvent;
import org.eclipse.net4j.internal.buddies.bundle.OM;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class ClientSession extends BuddyContainer implements IBuddySession, IListener
{
  private IChannel channel;

  private Self self;

  public ClientSession(IChannel channel)
  {
    this.channel = channel;
  }

  public IChannel getChannel()
  {
    return channel;
  }

  public Self getSelf()
  {
    return self;
  }

  public void setSelf(IAccount account, Set<String> facilityTypes)
  {
    self = new Self(this, account, facilityTypes);
    self.addListener(this);
  }

  public void close()
  {
    channel.close();
    deactivate();
  }

  /**
   * @see PlatformObject#getAdapter(Class)
   */
  @SuppressWarnings("unchecked")
  public Object getAdapter(Class adapter)
  {
    return Platform.getAdapterManager().getAdapter(this, adapter);
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event.getSource() == channel)
    {
      if (event instanceof ILifecycleEvent)
      {
        if (((ILifecycleEvent)event).getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          deactivate();
        }
      }
    }
    else if (event.getSource() == self)
    {
      if (event instanceof IBuddyStateChangedEvent)
      {
        try
        {
          new BuddyStateNotification(channel, self.getUserID(), ((IBuddyStateChangedEvent)event).getNewState()).send();
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  public void buddyAdded(String userID)
  {
    ClientBuddy buddy = new ClientBuddy(this, userID);
    addBuddy(buddy);
  }

  public void buddyRemoved(String userID)
  {
    removeBuddy(userID);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    channel.addListener(this);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    channel.removeListener(this);
    super.doDeactivate();
  }
}
