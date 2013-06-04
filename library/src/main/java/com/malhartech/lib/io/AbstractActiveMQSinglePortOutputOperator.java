/*
 *  Copyright (c) 2012 Malhar, Inc.
 *  All Rights Reserved.
 */
package com.malhartech.lib.io;

import com.malhartech.api.annotation.InputPortFieldAnnotation;
import com.malhartech.api.DefaultInputPort;
import javax.jms.JMSException;
import javax.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ActiveMQ output adapter operator with only one input port, which produce data into ActiveMQ message bus.<p><br>
 *
 * <br>
 * Ports:<br>
 * <b>Input</b>: Have only one input port<br>
 * <b>Output</b>: No output port<br>
 * <br>
 * Properties:<br>
 * None<br>
 * <br>
 * Compile time checks:<br>
 * Class derived from this has to implement the abstract method createMessage() <br>
 * <br>
 * Run time checks:<br>
 * None<br>
 * <br>
 * Benchmarks:<br>
 * TBD<br>
 * <br>
 * @author Locknath Shil <locknath@malhar-inc.com>
 *
 */
public abstract class AbstractActiveMQSinglePortOutputOperator<T> extends AbstractActiveMQOutputOperator
{
  private static final Logger logger = LoggerFactory.getLogger(AbstractActiveMQSinglePortOutputOperator.class);
  long countMessages = 0;  // Number of messages produced so far

  /**
   * Convert tuple into JMS message. Tuple can be any Java Object.
   * @param tuple
   * @return Message
   */
  protected abstract Message createMessage(T tuple);

  /**
   * The single input port.
   */
  @InputPortFieldAnnotation(name = "ActiveMQInputPort")
  public final transient DefaultInputPort<T> inputPort = new DefaultInputPort<T>(this)
  {
    @Override
    public void process(T tuple)
    {
      countMessages++;

      if (countMessages > maxSendMessage && maxSendMessage != 0) {
        if (countMessages == maxSendMessage + 1) {
          logger.warn("Reached maximum send messages of {}", maxSendMessage);
        }
        return; // Stop sending messages after max limit.
      }

      try {
        Message msg = createMessage(tuple);
        getProducer().send(msg);
        //logger.debug("process message {}", tuple.toString());
      }
      catch (JMSException ex) {
        logger.debug(ex.getLocalizedMessage());
      }
    }
  };
}
