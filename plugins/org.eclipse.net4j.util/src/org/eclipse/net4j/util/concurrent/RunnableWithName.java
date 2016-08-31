/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.StringUtil;

/**
 * @author Eike Stepper
 * @since 3.6
 */
public abstract class RunnableWithName implements Runnable
{
  public abstract String getName();

  public final void run()
  {
    Thread thread = null;
    String oldName = null;

    String name = getName();
    if (!StringUtil.isEmpty(name))
    {
      thread = Thread.currentThread();
      oldName = thread.getName();
      if (name.equals(oldName))
      {
        thread = null;
        oldName = null;
      }
      else
      {
        thread.setName(name);
      }
    }

    try
    {
      doRun();
    }
    finally
    {
      if (thread != null)
      {
        thread.setName(oldName);
      }
    }
  }

  protected abstract void doRun();
}