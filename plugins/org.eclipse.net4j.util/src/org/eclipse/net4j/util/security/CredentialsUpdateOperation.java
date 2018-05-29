/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

/**
 * @author Christian W. Damus (CEA LIST)
 * @since 3.4
 */
public enum CredentialsUpdateOperation
{
  CHANGE_PASSWORD, RESET_PASSWORD;

  @Override
  public String toString()
  {
    switch (this)
    {
    case CHANGE_PASSWORD:
      return "change"; //$NON-NLS-1$

    case RESET_PASSWORD:
      return "reset"; //$NON-NLS-1$
    }

    return super.toString();
  }
}
