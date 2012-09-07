/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer;

import org.eclipse.emf.cdo.transfer.CDOTransfer.ChildrenChangedEvent.Kind;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.2
 */
class CDOTransferMappingImpl implements CDOTransferMapping
{
  private final CDOTransfer transfer;

  private final CDOTransferElement source;

  private final CDOTransferMappingImpl parent;

  private List<CDOTransferMapping> children;

  private CDOTransferType transferType;

  private IPath relativePath;

  public CDOTransferMappingImpl(CDOTransfer transfer, CDOTransferElement source, CDOTransferMapping parent)
      throws IOException
  {
    this.transfer = transfer;
    this.source = source;
    this.parent = (CDOTransferMappingImpl)parent;
    relativePath = new Path(source.getName());

    CDOTransferType transferType = transfer.getTransferType(source);
    setTransferType(transferType);

    if (parent != null)
    {
      this.parent.addChild(this);
    }

    if (isDirectory())
    {
      for (CDOTransferElement child : source.getChildren())
      {
        transfer.map(child, this);
      }
    }
  }

  public CDOTransferMappingImpl(CDOTransfer transfer)
  {
    this.transfer = transfer;
    source = null;
    parent = null;
    relativePath = Path.EMPTY;
    setTransferType(CDOTransferType.FOLDER);
  }

  public CDOTransfer getTransfer()
  {
    return transfer;
  }

  public CDOTransferElement getSource()
  {
    return source;
  }

  public CDOTransferMapping getParent()
  {
    return parent;
  }

  public boolean isRoot()
  {
    return parent == null;
  }

  public boolean isDirectory()
  {
    if (source == null)
    {
      return true;
    }

    return source.isDirectory();
  }

  public String getName()
  {
    return relativePath.lastSegment();
  }

  public void setName(String name)
  {
    setRelativePath(relativePath.removeLastSegments(1).append(name));
  }

  public IPath getRelativePath()
  {
    return relativePath;
  }

  public void setRelativePath(IPath relativePath)
  {
    if (!ObjectUtil.equals(this.relativePath, relativePath))
    {
      IPath oldPath = this.relativePath;
      this.relativePath = relativePath;

      IListener[] listeners = transfer.notifier.getListeners();
      if (listeners != null)
      {
        transfer.notifier.fireEvent(new CDOTransfer.RelativePathChangedEvent(this, oldPath, relativePath), listeners);
      }
    }
  }

  public void setRelativePath(String path)
  {
    setRelativePath(new Path(path));
  }

  public void accept(Visitor visitor)
  {
    if (visitor.visit(this) && children != null)
    {
      for (CDOTransferMapping child : children)
      {
        child.accept(visitor);
      }
    }
  }

  public CDOTransferMapping[] getChildren()
  {
    if (children == null || children.isEmpty())
    {
      return NO_CHILDREN;
    }

    CDOTransferMapping[] result = children.toArray(new CDOTransferMapping[children.size()]);
    Arrays.sort(result);
    return result;
  }

  public CDOTransferMapping getChild(IPath path)
  {
    if (path.isEmpty())
    {
      return this;
    }

    String name = path.segment(0);
    for (CDOTransferMapping child : getChildren())
    {
      if (name.equals(child.getName()))
      {
        return child.getChild(path.removeFirstSegments(1));
      }
    }

    return null;
  }

  public CDOTransferMapping getChild(String path)
  {
    return getChild(new Path(path));
  }

  private void addChild(CDOTransferMapping child)
  {
    ensureChildrenList();
    if (!children.contains(child))
    {
      children.add(child);
      fireChildrenChangedEvent(child, CDOTransfer.ChildrenChangedEvent.Kind.MAPPED);
    }
  }

  private void removeChild(CDOTransferMapping child)
  {
    if (children != null && children.remove(child))
    {
      fireChildrenChangedEvent(child, CDOTransfer.ChildrenChangedEvent.Kind.UNMAPPED);
    }
  }

  private void ensureChildrenList()
  {
    if (children == null)
    {
      children = new ArrayList<CDOTransferMapping>();
    }
  }

  private void fireChildrenChangedEvent(CDOTransferMapping child, Kind kind)
  {
    IListener[] listeners = transfer.notifier.getListeners();
    if (listeners != null)
    {
      transfer.notifier.fireEvent(new CDOTransfer.ChildrenChangedEvent(this, child, kind), listeners);
    }
  }

  public void unmap()
  {
    transfer.unmap(this);
    if (parent != null)
    {
      parent.removeChild(this);
    }
  }

  public CDOTransferType getTransferType()
  {
    return transferType;
  }

  public void setTransferType(CDOTransferType transferType)
  {
    if (!ObjectUtil.equals(this.transferType, transferType))
    {
      // if (transferType == CDOTransferType.MODEL && !transfer.getModelTransferContext().hasResourceFactory(source))
      // {
      // throw new IllegalStateException("No resource factory registered for " + this);
      // }

      CDOTransferType oldType = this.transferType;
      this.transferType = transferType;

      IListener[] listeners = transfer.notifier.getListeners();
      if (listeners != null)
      {
        transfer.notifier.fireEvent(new CDOTransfer.TransferTypeChangedEvent(this, oldType, transferType), listeners);
      }
    }
  }

  public IPath getFullPath()
  {
    IPath relativePath = getRelativePath();
    if (isRoot())
    {
      return relativePath;
    }

    IPath path = parent.getFullPath();
    return path.append(relativePath);
  }

  public Status getStatus()
  {
    CDOTransferSystem targetSystem = transfer.getTargetSystem();

    IPath fullPath = getFullPath();
    int lastSegment = fullPath.segmentCount();
    for (int i = 1; i <= lastSegment; i++)
    {
      IPath path = fullPath.uptoSegment(i);
      CDOTransferElement target = targetSystem.getElement(path);
      if (target == null)
      {
        return Status.NEW;
      }

      boolean sourceDirectory = i == lastSegment ? isDirectory() : true;
      boolean targetDirectory = target.isDirectory();
      if (!(sourceDirectory && targetDirectory))
      {
        return Status.CONFLICT;
      }
    }

    return Status.MERGE;
  }

  public CDOTransferElement getTarget()
  {
    CDOTransferSystem targetSystem = transfer.getTargetSystem();
    IPath fullPath = getFullPath();
    return targetSystem.getElement(fullPath);
  }

  public int compareTo(CDOTransferMapping o)
  {
    boolean directory = isDirectory();
    boolean oDirectory = o.isDirectory();
    if (directory != oDirectory)
    {
      return directory ? -1 : 1;
    }

    return getName().compareTo(o.getName());
  }

  @Override
  public String toString()
  {
    return getFullPath().toString();
  }
}
