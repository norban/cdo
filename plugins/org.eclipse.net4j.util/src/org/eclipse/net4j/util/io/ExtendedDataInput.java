/*
 * Copyright (c) 2007-2013, 2015, 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import org.eclipse.net4j.util.io.ExtendedIOUtil.ClassResolver;

import java.io.Closeable;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ExtendedDataInput extends DataInput
{
  /**
   * @since 3.7
   */
  public int readVarInt() throws IOException;

  /**
   * @since 3.7
   */
  public long readVarLong() throws IOException;

  public byte[] readByteArray() throws IOException;

  public Object readObject() throws IOException;

  public Object readObject(ClassLoader classLoader) throws IOException;

  public Object readObject(ClassResolver classResolver) throws IOException;

  public String readString() throws IOException;

  /**
   * @since 3.0
   */
  public <T extends Enum<?>> T readEnum(Class<T> type) throws IOException;

  /**
   * @since 3.4
   */
  public Throwable readException() throws IOException;

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public static class Delegating implements ExtendedDataInput, Closeable
  {
    private ExtendedDataInput delegate;

    public Delegating(ExtendedDataInput delegate)
    {
      this.delegate = delegate;
    }

    public ExtendedDataInput getDelegate()
    {
      return delegate;
    }

    public boolean readBoolean() throws IOException
    {
      return delegate.readBoolean();
    }

    public byte readByte() throws IOException
    {
      return delegate.readByte();
    }

    public byte[] readByteArray() throws IOException
    {
      return delegate.readByteArray();
    }

    public char readChar() throws IOException
    {
      return delegate.readChar();
    }

    public double readDouble() throws IOException
    {
      return delegate.readDouble();
    }

    public float readFloat() throws IOException
    {
      return delegate.readFloat();
    }

    public void readFully(byte[] b, int off, int len) throws IOException
    {
      delegate.readFully(b, off, len);
    }

    public void readFully(byte[] b) throws IOException
    {
      delegate.readFully(b);
    }

    public int readInt() throws IOException
    {
      return delegate.readInt();
    }

    public String readLine() throws IOException
    {
      return delegate.readLine();
    }

    public long readLong() throws IOException
    {
      return delegate.readLong();
    }

    public Object readObject() throws IOException
    {
      return delegate.readObject();
    }

    public Object readObject(ClassLoader classLoader) throws IOException
    {
      return delegate.readObject(classLoader);
    }

    public Object readObject(ClassResolver classResolver) throws IOException
    {
      return delegate.readObject(classResolver);
    }

    public short readShort() throws IOException
    {
      return delegate.readShort();
    }

    public String readString() throws IOException
    {
      return delegate.readString();
    }

    /**
     * @since 3.0
     */
    public <T extends Enum<?>> T readEnum(Class<T> type) throws IOException
    {
      return delegate.readEnum(type);
    }

    /**
     * @since 3.4
     */
    public Throwable readException() throws IOException
    {
      return delegate.readException();
    }

    public int readUnsignedByte() throws IOException
    {
      return delegate.readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException
    {
      return delegate.readUnsignedShort();
    }

    public String readUTF() throws IOException
    {
      return delegate.readUTF();
    }

    /**
     * @since 3.7
     */
    public int readVarInt() throws IOException
    {
      return delegate.readVarInt();
    }

    /**
     * @since 3.7
     */
    public long readVarLong() throws IOException
    {
      return delegate.readVarLong();
    }

    public int skipBytes(int n) throws IOException
    {
      return delegate.skipBytes(n);
    }

    /**
     * @since 3.6
     */
    public void close() throws IOException
    {
      if (delegate instanceof Closeable)
      {
        ((Closeable)delegate).close();
      }
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public static class Stream extends InputStream
  {
    private ExtendedDataInput delegate;

    public Stream(ExtendedDataInput delegate)
    {
      this.delegate = delegate;
    }

    public ExtendedDataInput getDelegate()
    {
      return delegate;
    }

    @Override
    public int read() throws IOException
    {
      try
      {
        return delegate.readUnsignedByte();
      }
      catch (EOFException ex)
      {
        return -1;
      }
    }

    @Override
    public void close() throws IOException
    {
      if (delegate instanceof Closeable)
      {
        ((Closeable)delegate).close();
      }

      super.close();
    }
  }
}
