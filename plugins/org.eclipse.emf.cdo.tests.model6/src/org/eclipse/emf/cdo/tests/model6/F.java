/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>F</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.F#getOwnedEs <em>Owned Es</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getF()
 * @model
 * @generated
 */
public interface F extends EObject
{
  /**
   * Returns the value of the '<em><b>Owned Es</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.E}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Owned Es</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Owned Es</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getF_OwnedEs()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<E> getOwnedEs();

} // F
