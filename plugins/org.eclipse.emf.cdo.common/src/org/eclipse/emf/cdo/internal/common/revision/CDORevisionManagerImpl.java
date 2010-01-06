/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 230832
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCacheUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDORevisionManagerImpl extends Lifecycle implements InternalCDORevisionManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CDORevisionManagerImpl.class);

  private RevisionLoader revisionLoader;

  private RevisionLocker revisionLocker;

  private CDORevisionFactory factory;

  private CDORevisionCache cache;

  @ExcludeFromDump
  private Object loadAndAddLock = new Object();

  @ExcludeFromDump
  private Object revisedLock = new Object();

  public CDORevisionManagerImpl()
  {
  }

  public RevisionLoader getRevisionLoader()
  {
    return revisionLoader;
  }

  public void setRevisionLoader(RevisionLoader revisionLoader)
  {
    this.revisionLoader = revisionLoader;
  }

  public RevisionLocker getRevisionLocker()
  {
    return revisionLocker;
  }

  public void setRevisionLocker(RevisionLocker revisionLocker)
  {
    this.revisionLocker = revisionLocker;
  }

  public CDORevisionFactory getFactory()
  {
    return factory;
  }

  public void setFactory(CDORevisionFactory factory)
  {
    this.factory = factory;
  }

  public CDORevisionCache getCache()
  {
    return cache;
  }

  public void setCache(CDORevisionCache cache)
  {
    this.cache = cache;
  }

  public boolean containsRevision(CDOID id, long timeStamp)
  {
    return cache.getRevisionByTime(id, timeStamp) != null;
  }

  public boolean containsRevisionByVersion(CDOID id, int version)
  {
    return cache.getRevisionByVersion(id, version) != null;
  }

  public EClass getObjectType(CDOID id)
  {
    return cache.getObjectType(id);
  }

  public void reviseLatest(CDOID id)
  {
    acquireAtomicRequestLock(revisedLock);

    try
    {
      // TODO Consider CDORevisionCache.removeLatestRevision()
      InternalCDORevision revision = (InternalCDORevision)cache.getRevisionByTime(id, CDORevision.UNSPECIFIED_DATE);
      if (revision != null)
      {
        cache.removeRevision(revision.getID(), revision.getVersion());
      }
    }
    finally
    {
      releaseAtomicRequestLock(revisedLock);
    }
  }

  public void reviseVersion(CDOID id, int version, long timeStamp)
  {
    acquireAtomicRequestLock(revisedLock);

    try
    {
      InternalCDORevision revision = (InternalCDORevision)cache.getRevisionByVersion(id, version);
      if (revision != null)
      {
        if (timeStamp == CDORevision.UNSPECIFIED_DATE)
        {
          cache.removeRevision(revision.getID(), revision.getVersion());
        }
        else
        {
          revision.setRevised(timeStamp - 1);
        }
      }
    }
    finally
    {
      releaseAtomicRequestLock(revisedLock);
    }
  }

  public InternalCDORevision getRevision(CDOID id, long timeStamp, int referenceChunk, int prefetchDepth,
      boolean loadOnDemand)
  {
    acquireAtomicRequestLock(loadAndAddLock);

    try
    {
      boolean prefetch = prefetchDepth != CDORevision.DEPTH_NONE;
      InternalCDORevision revision = prefetch ? null : (InternalCDORevision)cache.getRevisionByTime(id, timeStamp);
      if (revision == null || prefetchDepth != CDORevision.DEPTH_NONE)
      {
        if (loadOnDemand)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Loading revision {0} by time {1,date} {1,time}", id, timeStamp); //$NON-NLS-1$
          }

          revision = revisionLoader.loadRevision(id, timeStamp, referenceChunk, prefetchDepth);
          addCachedRevisionIfNotNull(revision);
        }
      }
      else
      {
        InternalCDORevision verified = verifyRevision(revision, referenceChunk);
        if (revision != verified)
        {
          addCachedRevisionIfNotNull(verified);
          revision = verified;
        }
      }

      return revision;
    }
    finally
    {
      releaseAtomicRequestLock(loadAndAddLock);
    }
  }

  public InternalCDORevision getRevisionByVersion(CDOID id, int version, int referenceChunk, int prefetchDepth,
      boolean loadOnDemand)
  {
    acquireAtomicRequestLock(loadAndAddLock);

    try
    {
      boolean prefetch = prefetchDepth != CDORevision.DEPTH_NONE;
      InternalCDORevision revision = prefetch ? null : (InternalCDORevision)cache.getRevisionByVersion(id, version);
      if (revision == null)
      {
        if (loadOnDemand)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Loading revision {0} by version {1}", id, version); //$NON-NLS-1$
          }

          revision = revisionLoader.loadRevisionByVersion(id, referenceChunk, prefetchDepth, version);
          addCachedRevisionIfNotNull(revision);
        }
      }

      return revision;
    }
    finally
    {
      releaseAtomicRequestLock(loadAndAddLock);
    }
  }

  public List<CDORevision> getRevisions(Collection<CDOID> ids, long timeStamp, int referenceChunk, int prefetchDepth,
      boolean loadOnDemand)
  {
    List<CDOID> missingIDs = loadOnDemand ? new ArrayList<CDOID>(0) : null;
    List<CDORevision> revisions = new ArrayList<CDORevision>(ids.size());
    for (CDOID id : ids)
    {
      InternalCDORevision revision = getRevision(id, timeStamp, referenceChunk, prefetchDepth, false);
      revisions.add(revision);
      if (revision == null && missingIDs != null)
      {
        missingIDs.add(id);
      }
    }

    if (missingIDs != null && !missingIDs.isEmpty())
    {
      acquireAtomicRequestLock(loadAndAddLock);

      try
      {
        List<InternalCDORevision> missingRevisions = revisionLoader.loadRevisions(missingIDs, timeStamp,
            referenceChunk, prefetchDepth);
        handleMissingRevisions(revisions, missingRevisions);
      }
      finally
      {
        releaseAtomicRequestLock(loadAndAddLock);
      }
    }

    return revisions;
  }

  private void addCachedRevisionIfNotNull(InternalCDORevision revision)
  {
    if (revision != null)
    {
      cache.addRevision(revision);
    }
  }

  protected InternalCDORevision verifyRevision(InternalCDORevision revision, int referenceChunk)
  {
    return revision;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (factory == null)
    {
      factory = CDORevisionFactory.DEFAULT;
    }

    if (cache == null)
    {
      cache = CDORevisionCacheUtil.createDefaultCache();
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    LifecycleUtil.activate(cache);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(cache);
    super.doDeactivate();
  }

  private void acquireAtomicRequestLock(Object key)
  {
    if (revisionLocker != null)
    {
      revisionLocker.acquireAtomicRequestLock(key);
    }
  }

  private void releaseAtomicRequestLock(Object key)
  {
    if (revisionLocker != null)
    {
      revisionLocker.releaseAtomicRequestLock(key);
    }
  }

  private void handleMissingRevisions(List<CDORevision> revisions, List<InternalCDORevision> missingRevisions)
  {
    Iterator<InternalCDORevision> it = missingRevisions.iterator();
    for (int i = 0; i < revisions.size(); i++)
    {
      CDORevision revision = revisions.get(i);
      if (revision == null)
      {
        InternalCDORevision missingRevision = it.next();
        revisions.set(i, missingRevision);
        addCachedRevisionIfNotNull(missingRevision);
      }
    }
  }
}
