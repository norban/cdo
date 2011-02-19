/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.mongodb;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.internal.mongodb.bundle.OM;
import org.eclipse.emf.cdo.server.mongodb.IMongoDBStore;
import org.eclipse.emf.cdo.server.mongodb.IMongoDBStoreAccessor;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TODO:
 * <ul>
 * <li>Are indexes always unique? Do unique indexes exist?
 * <li>Are <code>_id</code> fields in embedded objects automatically indexed?
 * </ul>
 * 
 * @author Eike Stepper
 */
public class MongoDBStore extends Store implements IMongoDBStore
{
  public static final String TYPE = "mongodb"; //$NON-NLS-1$

  private ValueHandler[] valueHandlers = new ValueHandler[Byte.MAX_VALUE - Byte.MIN_VALUE + 1];

  private MongoURI mongoURI;

  private String dbName;

  private IDHandler idHandler = new IDHandler.LongValue(this);

  private DB db;

  private Props props;

  private Commits commits;

  private Classes classes;

  private boolean firstStart;

  private long creationTime;

  private boolean branching;

  public static Map<String, InternalRepository> REPOS = new HashMap<String, InternalRepository>();

  public MongoDBStore()
  {
    super(TYPE, null, set(ChangeFormat.DELTA), //
        set(RevisionTemporality.AUDITING, RevisionTemporality.NONE), //
        set(RevisionParallelism.NONE, RevisionParallelism.BRANCHING));
  }

  public ValueHandler getValueHandler(CDOType type)
  {
    return valueHandlers[type.getTypeID() - Byte.MIN_VALUE];
  }

  public MongoURI getMongoURI()
  {
    return mongoURI;
  }

  public void setMongoURI(MongoURI mongoURI)
  {
    checkInactive();
    this.mongoURI = mongoURI;
  }

  public String getDBName()
  {
    return dbName;
  }

  public void setDBName(String dbName)
  {
    checkInactive();
    this.dbName = dbName;
  }

  public IDHandler getIDHandler()
  {
    return idHandler;
  }

  public void setIDHandler(IDHandler idHandler)
  {
    checkInactive();
    this.idHandler = idHandler;
  }

  public DB getDB()
  {
    return db;
  }

  public Props getProps()
  {
    return props;
  }

  public Commits getCommits()
  {
    return commits;
  }

  public Classes getClasses()
  {
    return classes;
  }

  public Map<String, String> getPropertyValues(Set<String> names)
  {
    return props.get(names);
  }

  public void setPropertyValues(Map<String, String> properties)
  {
    props.set(properties);
  }

  public void removePropertyValues(Set<String> names)
  {
    props.remove(names);
  }

  public boolean isFirstStart()
  {
    return firstStart;
  }

  public long getCreationTime()
  {
    return creationTime;
  }

  public void setCreationTime(long creationTime)
  {
    this.creationTime = creationTime;

    Map<String, String> map = new HashMap<String, String>();
    map.put(Props.REPOSITORY_CREATED, Long.toString(creationTime));
    props.set(map);
  }

  public boolean isBranching()
  {
    return branching;
  }

  public CDOID createObjectID(String val)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public boolean isLocal(CDOID id)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  public IMongoDBStoreAccessor getReader(ISession session)
  {
    return (IMongoDBStoreAccessor)super.getReader(session);
  }

  @Override
  public IMongoDBStoreAccessor getWriter(ITransaction transaction)
  {
    return (IMongoDBStoreAccessor)super.getWriter(transaction);
  }

  @Override
  protected IStoreAccessor createReader(ISession session)
  {
    return new MongoDBStoreAccessor(this, session);
  }

  @Override
  protected IStoreAccessor createWriter(ITransaction transaction)
  {
    return new MongoDBStoreAccessor(this, transaction);
  }

  @Override
  protected StoreAccessorPool getReaderPool(ISession session, boolean forReleasing)
  {
    // No pooling needed
    return null;
  }

  @Override
  protected StoreAccessorPool getWriterPool(IView view, boolean forReleasing)
  {
    // No pooling needed
    return null;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(mongoURI, "mongoURI");
    checkState(dbName, "dbName");
  }

  @Override
  protected void doActivate() throws Exception
  {
    InternalRepository repository = getRepository();
    branching = repository.isSupportingBranches();

    REPOS.put(repository.getName(), repository);

    super.doActivate();

    Mongo mongo = new Mongo(mongoURI);
    db = mongo.getDB(dbName);

    Set<String> collectionNames = db.getCollectionNames();
    firstStart = !collectionNames.contains(Props.NAME);

    props = new Props(this);
    commits = new Commits(this);
    classes = new Classes();

    LifecycleUtil.activate(idHandler);
    setObjectIDTypes(idHandler.getObjectIDTypes());

    Arrays.fill(valueHandlers, new ValueHandler());
    initValueHandlers();

    if (firstStart)
    {
      firstStart();
    }
    else
    {
      reStart();
    }
  }

  protected void initValueHandlers()
  {
    initValueHandler(CDOType.OBJECT, new ValueHandler()
    {
      @Override
      public Object toMongo(Object value)
      {
        if (value != null)
        {
          return idHandler.toValue((CDOID)value);
        }

        return null;
      }

      @Override
      public Object fromMongo(Object value)
      {
        if (value != null)
        {
          return idHandler.fromValue(value);
        }

        return null;
      }
    });

    initValueHandler(CDOType.CHAR, new ValueHandler()
    {
      @Override
      public Object toMongo(Object value)
      {
        return Character.toString((Character)value);
      }

      @Override
      public Object fromMongo(Object value)
      {
        if (value instanceof String)
        {
          return ((String)value).charAt(0);
        }

        return value;
      }
    });

    initValueHandler(CDOType.BYTE, new ValueHandler()
    {
      @Override
      public Object fromMongo(Object value)
      {
        if (value instanceof Integer)
        {
          return (byte)(int)(Integer)value;
        }

        return value;
      }
    });

    initValueHandler(CDOType.SHORT, new ValueHandler()
    {
      @Override
      public Object fromMongo(Object value)
      {
        if (value instanceof Integer)
        {
          return (short)(int)(Integer)value;
        }

        return value;
      }
    });

    initValueHandler(CDOType.LONG, new ValueHandler()
    {
      @Override
      public Object fromMongo(Object value)
      {
        if (value instanceof Integer)
        {
          return (long)(Integer)value;
        }

        return value;
      }
    });

    initValueHandler(CDOType.FLOAT, new ValueHandler()
    {
      @Override
      public Object fromMongo(Object value)
      {
        if (value instanceof Double)
        {
          return (float)(double)(Double)value;
        }

        return value;
      }
    });

    initValueHandler(CDOType.BIG_DECIMAL, new ValueHandler()
    {
      @Override
      public Object toMongo(Object value)
      {
        if (value != null)
        {
          return ((BigDecimal)value).toPlainString();
        }

        return null;
      }

      @Override
      public Object fromMongo(Object value)
      {
        if (value != null)
        {
          return new BigDecimal((String)value);
        }

        return null;
      }
    });

    initValueHandler(CDOType.BIG_INTEGER, new ValueHandler()
    {
      @Override
      public Object toMongo(Object value)
      {
        if (value != null)
        {
          return ((BigInteger)value).toString();
        }

        return null;
      }

      @Override
      public Object fromMongo(Object value)
      {
        if (value != null)
        {
          return new BigInteger((String)value);
        }

        return null;
      }
    });
  }

  protected void initValueHandler(CDOType type, ValueHandler valueHandler)
  {
    valueHandlers[type.getTypeID() - Byte.MIN_VALUE] = valueHandler;
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    REPOS.remove(getRepository().getName());

    Map<String, String> map = new HashMap<String, String>();
    map.put(Props.GRACEFULLY_SHUT_DOWN, Boolean.TRUE.toString());
    map.put(Props.REPOSITORY_STOPPED, Long.toString(getRepository().getTimeStamp()));
    map.put(Props.NEXT_LOCAL_CDOID, Store.idToString(idHandler.getNextLocalObjectID()));
    map.put(Props.LAST_CDOID, Store.idToString(idHandler.getLastObjectID()));
    map.put(Props.LAST_CLASSIFIERID, Integer.toString(classes.getLastClassifierID()));
    map.put(Props.LAST_BRANCHID, Integer.toString(getLastBranchID()));
    map.put(Props.LAST_LOCAL_BRANCHID, Integer.toString(getLastLocalBranchID()));
    map.put(Props.LAST_COMMITTIME, Long.toString(getLastCommitTime()));
    map.put(Props.LAST_NONLOCAL_COMMITTIME, Long.toString(getLastNonLocalCommitTime()));
    setPropertyValues(map);

    LifecycleUtil.activate(idHandler);

    if (db != null)
    {
      db.getMongo().close();
      db = null;
    }

    super.doDeactivate();
  }

  protected void firstStart()
  {
    setCreationTime(getRepository().getTimeStamp());
    OM.LOG.info("First start: " + CDOCommonUtil.formatTimeStamp(creationTime));
  }

  protected void reStart()
  {
    Set<String> names = new HashSet<String>();
    names.add(Props.REPOSITORY_CREATED);
    names.add(Props.GRACEFULLY_SHUT_DOWN);

    Map<String, String> map = getPropertyValues(names);
    creationTime = Long.valueOf(map.get(Props.REPOSITORY_CREATED));

    if (map.containsKey(Props.GRACEFULLY_SHUT_DOWN))
    {
      names.clear();
      names.add(Props.NEXT_LOCAL_CDOID);
      names.add(Props.LAST_CDOID);
      names.add(Props.LAST_CLASSIFIERID);
      names.add(Props.LAST_BRANCHID);
      names.add(Props.LAST_LOCAL_BRANCHID);
      names.add(Props.LAST_COMMITTIME);
      names.add(Props.LAST_NONLOCAL_COMMITTIME);
      map = getPropertyValues(names);

      idHandler.setNextLocalObjectID(stringToID(map.get(Props.NEXT_LOCAL_CDOID)));
      idHandler.setLastObjectID(stringToID(map.get(Props.LAST_CDOID)));
      classes.setLastClassifierID(Integer.valueOf(map.get(Props.LAST_CLASSIFIERID)));
      setLastBranchID(Integer.valueOf(map.get(Props.LAST_BRANCHID)));
      setLastLocalBranchID(Integer.valueOf(map.get(Props.LAST_LOCAL_BRANCHID)));
      setLastCommitTime(Long.valueOf(map.get(Props.LAST_COMMITTIME)));
      setLastNonLocalCommitTime(Long.valueOf(map.get(Props.LAST_NONLOCAL_COMMITTIME)));
    }
    else
    {
      repairAfterCrash();
    }

    removePropertyValues(Collections.singleton(Props.GRACEFULLY_SHUT_DOWN));
  }

  protected void repairAfterCrash()
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  /**
   * @author Eike Stepper
   */
  public static class ValueHandler
  {
    public Object toMongo(Object value)
    {
      return value;
    }

    public Object fromMongo(Object value)
    {
      return value;
    }
  }
}
