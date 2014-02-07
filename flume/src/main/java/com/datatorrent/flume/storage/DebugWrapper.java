/*
 *  Copyright (c) 2014 DataTorrent, Inc. ALL Rights Reserved.
 */
package com.datatorrent.flume.storage;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.flume.Context;
import org.apache.flume.conf.Configurable;

import com.datatorrent.api.Component;

/**
 *
 * @author Chetan Narsude <chetan@datatorrent.com>
 */
public class DebugWrapper implements Storage, Configurable, Component<com.datatorrent.api.Context>
{
  HDFSStorage storage = new HDFSStorage();

  @Override
  public byte[] store(byte[] bytes)
  {
    byte[] ret = null;

    try {
      ret = storage.store(bytes);
    }
    finally {
      logger.debug("storage.store(new byte[]{{}});", Arrays.toString(bytes));
    }

    return ret;
  }

  @Override
  public byte[] retrieve(byte[] identifier)
  {
    byte[] ret = null;

    try {
      ret = storage.retrieve(identifier);
    }
    finally {
      logger.debug("storage.retrieve(new byte[]{{}});", Arrays.toString(identifier));
    }

    return ret;
  }

  @Override
  public byte[] retrieveNext()
  {
    byte[] ret = null;
    try {
      ret = storage.retrieveNext();
    }
    finally {
      logger.debug("storage.retrieveNext();");
    }

    return ret;
  }

  @Override
  public void clean(byte[] identifier)
  {
    try {
      storage.clean(identifier);
    }
    finally {
      logger.debug("storage.clean(new byte[]{{}});", identifier);
    }
  }

  @Override
  public void flush()
  {
    try {
      storage.flush();
    }
    finally {
      logger.debug("storage.flush();");
    }
  }

  @Override
  public void configure(Context cntxt)
  {
    try {
      storage.configure(cntxt);
    }
    finally {
      logger.debug("storage.configure({});", cntxt);
    }
  }

  @Override
  public void setup(com.datatorrent.api.Context t1)
  {
    try {
      storage.setup(t1);
    }
    finally {
      logger.debug("storage.setup({});", t1);
    }

  }

  @Override
  public void teardown()
  {
    try {
      storage.teardown();
    }
    finally {
      logger.debug("storage.teardown();");
    }
  }

  private static final Logger logger = LoggerFactory.getLogger(DebugWrapper.class);
}
