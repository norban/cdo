/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.lissome;

import org.eclipse.emf.cdo.server.IStoreChunkReader;

/**
 * A {@link IStoreChunkReader chunk reader} for CDO's proprietary object/relational mapper.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ILissomeStoreChunkReader extends IStoreChunkReader
{
  /**
   * @since 2.0
   */
  public ILissomeStoreAccessor getAccessor();
}
