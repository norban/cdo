/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.emf.spi.cdo.ObjectConflictResolver.MergeLocalChangesPerFeature;

/**
 * @author Simon McDuff
 */
public class ConflictResolverTest extends AbstractCDOTest
{
  public void testMergeLocalChangesPerFeature_Basic() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    CDOTransaction transaction = session.openTransaction();

    Address address = getModel1Factory().createAddress();

    transaction.getOrCreateResource("/res1").getContents().add(address);

    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().getConflictResolvers().add(new MergeLocalChangesPerFeature());
    Address address2 = (Address)transaction2.getOrCreateResource("/res1").getContents().get(0);

    address2.setCity("OTTAWA");

    address.setName("NAME1");

    transaction.commit();

    // Resolver should be triggered. Should we always used a timer ?
    Thread.sleep(1000);

    assertFalse(FSMUtil.isConflict(CDOUtil.getCDOObject(address2)));
    assertFalse(transaction2.hasConflict());

    assertEquals("NAME1", address2.getName());
    assertEquals("OTTAWA", address2.getCity());

    transaction2.commit();
  }

  public void testMergeLocalChangesPerFeature_BasicException() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    CDOTransaction transaction = session.openTransaction();

    Address address = getModel1Factory().createAddress();

    transaction.getOrCreateResource("/res1").getContents().add(address);

    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().getConflictResolvers().add(new MergeLocalChangesPerFeature());
    Address address2 = (Address)transaction2.getOrCreateResource("/res1").getContents().get(0);

    address2.setCity("OTTAWA");

    address.setCity("NAME1");

    transaction.commit();

    // Resolver should be triggered. Should we always used a timer ?
    Thread.sleep(1000);
    assertTrue(FSMUtil.isConflict(CDOUtil.getCDOObject(address2)));
    assertTrue(transaction2.hasConflict());
    assertEquals("OTTAWA", address2.getCity());

  }
}
