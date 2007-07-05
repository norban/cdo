/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.bundle.CDOProtocol;
import org.eclipse.emf.cdo.internal.protocol.revision.CDOReferenceConverter;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.stream.ExtendedDataInputStream;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;
import org.eclipse.emf.internal.cdo.CDOStore;
import org.eclipse.emf.internal.cdo.CDOTransactionImpl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CommitTransactionRequest extends CDOClientRequest<CommitTransactionResult>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(CDOProtocol.DEBUG_PROTOCOL,
      CommitTransactionRequest.class);

  private CDOTransactionImpl transaction;

  private CDOReferenceConverter converter;

  public CommitTransactionRequest(IChannel channel, final CDOTransactionImpl transaction)
  {
    super(channel, CDOProtocolConstants.COMMIT_TRANSACTION_SIGNAL);
    this.transaction = transaction;
    converter = new CDOReferenceConverter()
    {
      public CDOID convertToID(Object object)
      {
        try
        {
          CDOID id = (CDOID)CDOStore.convertToID(transaction.getView(), object);
          if (PROTOCOL.isEnabled())
          {
            PROTOCOL.format("Converted dangling reference: {0} --> {1}", object, id);
          }

          return id;
        }
        catch (ClassCastException ex)
        {
          throw new IllegalStateException("Encountered unconvertable dangling reference during commit: " + object);
        }
      }
    };
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    writeNewResources(out);
    writeNewObjects(out);
    writeDirtyObjects(out);
  }

  private void writeNewResources(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} new resources", transaction.getNewResources().size());
    }

    writeRevisions(out, transaction.getNewResources().values());
  }

  private void writeNewObjects(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} new objects", transaction.getNewObjects().size());
    }

    writeRevisions(out, transaction.getNewObjects().values());
  }

  private void writeDirtyObjects(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} dirty objects", transaction.getDirtyObjects().size());
    }

    writeRevisions(out, transaction.getDirtyObjects().values());
  }

  private void writeRevisions(ExtendedDataOutputStream out, Collection objects) throws IOException
  {
    out.writeInt(objects.size());
    for (Iterator it = objects.iterator(); it.hasNext();)
    {
      CDOObjectImpl object = (CDOObjectImpl)it.next();
      CDORevisionImpl revision = object.cdoRevision();
      revision.write(out, converter);
    }
  }

  @Override
  protected CommitTransactionResult confirming(ExtendedDataInputStream in) throws IOException
  {
    long timeStamp = in.readLong();

    Map<CDOID, CDOID> idMappings = new HashMap();
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      CDOID oldID = CDOIDImpl.read(in);
      CDOID newID = CDOIDImpl.read(in);
      idMappings.put(oldID, newID);
    }

    return new CommitTransactionResult(timeStamp, idMappings);
  }
}
