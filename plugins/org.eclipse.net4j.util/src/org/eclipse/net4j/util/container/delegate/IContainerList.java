/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container.delegate;

import org.eclipse.net4j.util.container.IContainer;

import java.util.List;

/**
 * A {@link IContainer container} that is a {@link List}.
 *
 * @author Eike Stepper
 */
public interface IContainerList<E> extends IContainerCollection<E>, List<E>
{
  public List<E> getDelegate();
}
