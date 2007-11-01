/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl.MoveableList;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.revision.CDOReferenceProxy;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.FSMUtil;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class CDOStore implements EStore
{
  private final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOStore.class);

  private CDOViewImpl view;

  public CDOStore(CDOViewImpl view)
  {
    this.view = view;
  }

  public CDOViewImpl getView()
  {
    return view;
  }

  public void setContainer(InternalEObject eObject, InternalEObject newContainer, int newContainerFeatureID)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("setContainer({0}, {1}, {2})", cdoObject, newContainer, newContainerFeatureID);
    }

    CDOID containerID = (CDOID)((CDOViewImpl)cdoObject.cdoView()).convertObjectToID(newContainer);

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    revision.setContainerID(containerID);
    revision.setContainingFeature(newContainerFeatureID);
  }

  public InternalEObject getContainer(InternalEObject eObject)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("getContainer({0})", cdoObject);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    CDOID id = revision.getContainerID();
    return (InternalEObject)((CDOViewImpl)cdoObject.cdoView()).convertIDToObject(id);
  }

  public int getContainingFeatureID(InternalEObject eObject)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("getContainingFeatureID({0})", cdoObject);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.getContainingFeatureID();
  }

  @Deprecated
  public EStructuralFeature getContainingFeature(InternalEObject eObject)
  {
    throw new UnsupportedOperationException("Use getContainingFeatureID() instead");
  }

  public Object get(InternalEObject eObject, EStructuralFeature eFeature, int index)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("get({0}, {1}, {2})", cdoObject, cdoFeature, index);
    }

    view.getFeatureAnalyzer().preTraverseFeature(cdoObject, cdoFeature, index);
    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    Object value = get(revision, cdoFeature, index);
    if (cdoFeature.isReference())
    {
      if (cdoFeature.isMany() && value instanceof CDOID)
      {
        CDOID id = (CDOID)value;
        loadAhead(revision, cdoFeature, id, index);
      }

      value = view.convertIDToObject(value);
    }

    view.getFeatureAnalyzer().postTraverseFeature(cdoObject, cdoFeature, index, value);
    return value;
  }

  private void loadAhead(CDORevisionImpl revision, CDOFeatureImpl cdoFeature, CDOID id, int index)
  {
    CDOSessionImpl session = view.getSession();
    CDORevisionManagerImpl revisionManager = session.getRevisionManager();

    int chunkSize = view.getLoadRevisionCollectionChunkSize();
    if (chunkSize > 1 && !revisionManager.containsRevision(id))
    {
      MoveableList list = revision.getList(cdoFeature);
      int fromIndex = index;
      int toIndex = Math.min(index + chunkSize, list.size()) - 1;

      Set<CDOID> notRegistered = new HashSet<CDOID>();
      for (int i = fromIndex; i <= toIndex; i++)
      {
        Object element = list.get(i);
        if (element instanceof CDOID)
        {
          CDOID idElement = (CDOID)element;
          if (!idElement.isTemporary())
          {
            if (!revisionManager.containsRevision(idElement))
            {
              if (!notRegistered.contains(idElement))
              {
                notRegistered.add(idElement);
              }
            }
          }
        }
      }

      if (!notRegistered.isEmpty())
      {
        int referenceChunk = session.getReferenceChunkSize();
        revisionManager.getRevisions(notRegistered, referenceChunk);
      }
    }
  }

  private Object get(CDORevisionImpl revision, CDOFeature cdoFeature, int index)
  {
    Object result = revision.get(cdoFeature, index);
    if (cdoFeature.isReference())
    {
      if (result instanceof CDOReferenceProxy)
      {
        result = ((CDOReferenceProxy)result).resolve();
      }
    }
    return result;
  }

  public boolean isSet(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("isSet({0}, {1})", cdoObject, cdoFeature);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.isSet(cdoFeature);
  }

  public int size(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("size({0}, {1})", cdoObject, cdoFeature);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.size(cdoFeature);
  }

  public boolean isEmpty(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("isEmpty({0}, {1})", cdoObject, cdoFeature);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.isEmpty(cdoFeature);
  }

  public boolean contains(InternalEObject eObject, EStructuralFeature eFeature, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("contains({0}, {1}, {2})", cdoObject, cdoFeature, value);
    }

    if (cdoFeature.isReference())
    {
      value = ((CDOViewImpl)cdoObject.cdoView()).convertObjectToID(value);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.contains(cdoFeature, value);
  }

  public int indexOf(InternalEObject eObject, EStructuralFeature eFeature, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("indexOf({0}, {1}, {2})", cdoObject, cdoFeature, value);
    }

    if (cdoFeature.isReference())
    {
      value = ((CDOViewImpl)cdoObject.cdoView()).convertObjectToID(value);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.indexOf(cdoFeature, value);
  }

  public int lastIndexOf(InternalEObject eObject, EStructuralFeature eFeature, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("lastIndexOf({0}, {1}, {2})", cdoObject, cdoFeature, value);
    }

    if (cdoFeature.isReference())
    {
      value = ((CDOViewImpl)cdoObject.cdoView()).convertObjectToID(value);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.lastIndexOf(cdoFeature, value);
  }

  public int hashCode(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("hashCode({0}, {1})", cdoObject, cdoFeature);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.hashCode(cdoFeature);
  }

  public Object[] toArray(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("toArray({0}, {1})", cdoObject, cdoFeature);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    Object[] result = revision.toArray(cdoFeature);
    if (cdoFeature.isReference())
    {
      for (int i = 0; i < result.length; i++)
      {
        if (result[i] instanceof CDOReferenceProxy)
        {
          result[i] = ((CDOReferenceProxy)result[i]).resolve();
        }

        result[i] = ((CDOViewImpl)cdoObject.cdoView()).convertIDToObject(result[i]);
      }
    }

    return result;
  }

  public <T> T[] toArray(InternalEObject eObject, EStructuralFeature eFeature, T[] a)
  {
    Object[] array = toArray(eObject, eFeature);
    int size = array.length;

    if (a.length < size) a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
    System.arraycopy(array, 0, a, 0, size);
    if (a.length > size) a[size] = null;
    return a;
  }

  public Object set(InternalEObject eObject, EStructuralFeature eFeature, int index, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("set({0}, {1}, {2}, {3})", cdoObject, cdoFeature, index, value);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    if (cdoFeature.isReference())
    {
      Object oldValue = revision.get(cdoFeature, index);
      if (oldValue instanceof CDOReferenceProxy)
      {
        ((CDOReferenceProxy)oldValue).resolve();
      }

      if (cdoFeature.isContainment())
      {
        handleContainmentAdd(cdoObject, cdoFeature, value);
      }

      value = ((CDOViewImpl)cdoObject.cdoView()).convertObjectToID(value);
    }

    Object result = revision.set(cdoFeature, index, value);
    if (cdoFeature.isReference())
    {
      result = ((CDOViewImpl)cdoObject.cdoView()).convertIDToObject(result);
    }

    return result;
  }

  public void unset(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("unset({0}, {1})", cdoObject, cdoFeature);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    revision.unset(cdoFeature);
  }

  public void add(InternalEObject eObject, EStructuralFeature eFeature, int index, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("add({0}, {1}, {2}, {3})", cdoObject, cdoFeature, index, value);
    }

    if (cdoFeature.isReference())
    {
      if (cdoFeature.isContainment())
      {
        handleContainmentAdd(cdoObject, cdoFeature, value);
      }

      value = ((CDOViewImpl)cdoObject.cdoView()).convertObjectToID(value);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    revision.add(cdoFeature, index, value);
  }

  public Object remove(InternalEObject eObject, EStructuralFeature eFeature, int index)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("remove({0}, {1}, {2})", cdoObject, cdoFeature, index);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    Object result = revision.remove(cdoFeature, index);
    if (cdoFeature.isReference())
    {
      if (result instanceof CDOReferenceProxy)
      {
        result = ((CDOReferenceProxy)result).resolve();
      }

      result = ((CDOViewImpl)cdoObject.cdoView()).convertIDToObject(result);
    }

    return result;
  }

  public void clear(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("clear({0}, {1})", cdoObject, cdoFeature);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    revision.clear(cdoFeature);
  }

  public Object move(InternalEObject eObject, EStructuralFeature eFeature, int target, int source)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("move({0}, {1}, {2}, {3})", cdoObject, cdoFeature, target, source);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    Object result = revision.move(cdoFeature, target, source);
    if (cdoFeature.isReference())
    {
      if (result instanceof CDOReferenceProxy)
      {
        result = ((CDOReferenceProxy)result).resolve();
      }

      result = ((CDOViewImpl)cdoObject.cdoView()).convertIDToObject(result);
    }

    return result;
  }

  public EObject create(EClass eClass)
  {
    throw new UnsupportedOperationException("Use the generated factory to create objects");
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOStore[{0}]", view);
  }

  private InternalCDOObject getCDOObject(Object object)
  {
    return FSMUtil.adapt(object, view);
  }

  private CDOFeatureImpl getCDOFeature(InternalCDOObject cdoObject, EStructuralFeature eFeature)
  {
    CDOViewImpl view = (CDOViewImpl)cdoObject.cdoView();
    if (view == null)
    {
      throw new IllegalStateException("view == null");
    }

    CDOSessionPackageManager packageManager = view.getSession().getPackageManager();
    return ModelUtil.getCDOFeature(eFeature, packageManager);
  }

  private static CDORevisionImpl getRevisionForReading(InternalCDOObject cdoObject)
  {
    CDOStateMachine.INSTANCE.read(cdoObject);
    return getRevision(cdoObject);
  }

  private static CDORevisionImpl getRevisionForWriting(InternalCDOObject cdoObject)
  {
    CDOStateMachine.INSTANCE.write(cdoObject);
    return getRevision(cdoObject);
  }

  private static CDORevisionImpl getRevision(InternalCDOObject cdoObject)
  {
    CDORevisionImpl revision = (CDORevisionImpl)cdoObject.cdoRevision();
    if (revision == null)
    {
      throw new IllegalStateException("revision == null");
    }

    return revision;
  }

  private void handleContainmentAdd(InternalCDOObject cdoObject, CDOFeatureImpl cdoFeature, Object value)
  {
    InternalCDOObject container = cdoObject;
    InternalCDOObject contained = getCDOObject(value);
    CDOViewImpl containerView = (CDOViewImpl)container.cdoView();
    CDOViewImpl containedView = (CDOViewImpl)contained.cdoView();
    if (containedView != containerView)
    {
      if (containedView != null)
      {
        CDOStateMachine.INSTANCE.detach(contained, contained.cdoResource(), containedView);
      }

      CDOStateMachine.INSTANCE.attach(contained, container.cdoResource(), containerView);
    }
  }
}
