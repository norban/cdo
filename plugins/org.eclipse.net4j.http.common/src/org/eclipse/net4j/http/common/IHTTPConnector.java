/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.http.common;

import org.eclipse.net4j.connector.IConnector;

/**
 * A {@link IConnector connector} that implements polling HTTP transport.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IHTTPConnector extends IConnector
{
  public static final int DEFAULT_POLL_INTERVAL = 5 * 1000;// 5 seconds

  public static final int UNKNOWN_MAX_IDLE_TIME = -1;

  public String getConnectorID();

  public int getMaxIdleTime();
}
