/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.util.StringUtil;

import org.eclipse.spi.net4j.Acceptor;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class JVMAcceptor extends Acceptor implements IJVMAcceptor
{
  private String name;

  public JVMAcceptor()
  {
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public JVMServerConnector handleAccept(JVMClientConnector client)
  {
    JVMServerConnector connector = new JVMServerConnector(this, client);
    connector.setName(client.getName());
    connector.setConfig(getConfig());
    connector.activate();
    addConnector(connector);
    return connector;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("JVMAcceptor[{0}]", name); //$NON-NLS-1$
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (StringUtil.isEmpty(name))
    {
      throw new IllegalStateException("No name"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    JVMAcceptorManager.INSTANCE.registerAcceptor(this);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    JVMAcceptorManager.INSTANCE.deregisterAcceptor(this);
    super.doDeactivate();
  }
}
