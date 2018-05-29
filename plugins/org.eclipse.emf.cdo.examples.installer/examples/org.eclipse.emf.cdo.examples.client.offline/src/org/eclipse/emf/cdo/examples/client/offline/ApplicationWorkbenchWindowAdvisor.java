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
package org.eclipse.emf.cdo.examples.client.offline;

import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * @author Eike Stepper
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{
  public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
  {
    super(configurer);
  }

  @Override
  public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer)
  {
    return new ApplicationActionBarAdvisor(configurer);
  }

  @Override
  public void preWindowOpen()
  {
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    Application.NODE.getType().configureWindow(configurer);
  }

}
