/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.workingsets.impl;

import org.eclipse.emf.cdo.releng.workingsets.ExclusionPredicate;
import org.eclipse.emf.cdo.releng.workingsets.WorkingSet;
import org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.core.resources.IProject;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Exclusion Predicate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.workingsets.impl.ExclusionPredicateImpl#getExcludedWorkingSets <em>Excluded Working Sets</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ExclusionPredicateImpl extends MinimalEObjectImpl.Container implements ExclusionPredicate
{
  /**
   * The cached value of the '{@link #getExcludedWorkingSets() <em>Excluded Working Sets</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExcludedWorkingSets()
   * @generated
   * @ordered
   */
  protected EList<WorkingSet> excludedWorkingSets;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ExclusionPredicateImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return WorkingSetsPackage.Literals.EXCLUSION_PREDICATE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<WorkingSet> getExcludedWorkingSets()
  {
    if (excludedWorkingSets == null)
    {
      excludedWorkingSets = new EObjectResolvingEList<WorkingSet>(WorkingSet.class, this,
          WorkingSetsPackage.EXCLUSION_PREDICATE__EXCLUDED_WORKING_SETS);
    }
    return excludedWorkingSets;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean matches(IProject project)
  {
    for (WorkingSet workingSet : getExcludedWorkingSets())
    {
      if (workingSet.matches(project))
      {
        return false;
      }
    }
    return true;
  }

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case WorkingSetsPackage.EXCLUSION_PREDICATE__EXCLUDED_WORKING_SETS:
      return getExcludedWorkingSets();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case WorkingSetsPackage.EXCLUSION_PREDICATE__EXCLUDED_WORKING_SETS:
      getExcludedWorkingSets().clear();
      getExcludedWorkingSets().addAll((Collection<? extends WorkingSet>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case WorkingSetsPackage.EXCLUSION_PREDICATE__EXCLUDED_WORKING_SETS:
      getExcludedWorkingSets().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case WorkingSetsPackage.EXCLUSION_PREDICATE__EXCLUDED_WORKING_SETS:
      return excludedWorkingSets != null && !excludedWorkingSets.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case WorkingSetsPackage.EXCLUSION_PREDICATE___MATCHES__IPROJECT:
      return matches((IProject)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

} // ExclusionPredicateImpl
