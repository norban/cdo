/*
 * Copyright (c) 2007, 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.net4j.util.security;

/**
 * @author Eike Stepper
 */
public interface IPasswordCredentialsProvider extends ICredentialsProvider
{
  public IPasswordCredentials getCredentials();

  /**
   * Interface implemented by protocol objects that can supply an
   * {@link IPasswordCredentialsProvider}.
   * 
   * @author Christian W. Damus (CEA LIST)
   * @since 3.4
   */
  public static interface Provider
  {
    public IPasswordCredentialsProvider getCredentialsProvider();
  }
}
