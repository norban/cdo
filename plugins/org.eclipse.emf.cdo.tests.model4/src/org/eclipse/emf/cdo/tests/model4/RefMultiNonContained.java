/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Ref Multi Non Contained</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.RefMultiNonContained#getElements <em>Elements</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefMultiNonContained()
 * @model
 * @generated
 */
public interface RefMultiNonContained extends EObject
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Elements</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Elements</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefMultiNonContained_Elements()
   * @see org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement#getParent
   * @model opposite="parent"
   * @generated
   */
  EList<MultiNonContainedElement> getElements();

} // RefMultiNonContained
