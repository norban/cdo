/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

/**
 * @author Eike Stepper
 */
public class LockingManagerRestartSessionTest extends LockingManagerRestartTransactionTest
{
  @Override
  protected void restart(String durableLockingID)
  {
    session.close();
    doBetweenSessionCloseAndOpen();
    session = openSession();
    super.restart(durableLockingID);
  }

  protected void doBetweenSessionCloseAndOpen()
  {
  }
}
