/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.juddi.uuidgen;

import java.math.BigInteger;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Used to create new universally unique identifiers or UUID's (sometimes called
 * GUID's).  UDDI UUID's are allways formmated according to DCE UUID conventions.
 *
 * @author Maarten Coene
 * @author Steve Viens (sviens@apache.org)
 */
public class DefaultUUIDGen implements UUIDGen
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(DefaultUUIDGen.class);

  private static final BigInteger COUNT_START = new BigInteger("-12219292800000");  // 15 October 1582
  private static final int CLOCK_SEQUENCE = (new Random()).nextInt(16384);
  
  private Random random;

  /**
   *
   */
  public DefaultUUIDGen()
  {
    try {
      random = new Random();
      random.setSeed(System.currentTimeMillis());
    } catch (Exception e) {
      random = new Random();
    }
  }

  /**
   *
   */
  public String uuidgen()
  {
    return nextUUID();
  }

  /**
   *
   */
  public String[] uuidgen(int nmbr)
  {
    String[] uuids = new String[nmbr];

    for (int i=0; i<uuids.length; i++)
      uuids[i] = nextUUID();

    return uuids;
  }

  /**
   * Creates a new UUID. The algorithm used is described by The Open Group.
   * See <a href="http://www.opengroup.org/onlinepubs/009629399/apdxa.htm">
   * Universal Unique Identifier</a> for more details.
   *
   * Due to a lack of functionality in Java, a part of the UUID is a secure
   * random. This results in a long processing time when this method is called
   * for the first time.
   */
  private String nextUUID()
  {
    // the number of milliseconds since 1 January 1970
    BigInteger current = BigInteger.valueOf(System.currentTimeMillis());

    // the number of milliseconds since 15 October 1582
    BigInteger countMillis = current.subtract(COUNT_START);

    // the count of 100-nanosecond intervals since 00:00:00.00 15 October 1582
    BigInteger count = countMillis.multiply(BigInteger.valueOf(10000));

    String bitString = count.toString(2);
    if (bitString.length() < 60)
    {
      int nbExtraZeros  = 60 - bitString.length();
      String extraZeros = new String();
      for (int i=0; i<nbExtraZeros; i++)
        extraZeros = extraZeros.concat("0");

      bitString = extraZeros.concat(bitString);
    }

    byte[] bits = bitString.getBytes();

    // the time_low field
    byte[] time_low = new byte[32];
    for (int i=0; i<32; i++)
      time_low[i] = bits[bits.length - i - 1];

    // the time_mid field
    byte[] time_mid = new byte[16];
    for (int i=0; i<16; i++)
      time_mid[i] = bits[bits.length - 32 - i - 1];

    // the time_hi_and_version field
    byte[] time_hi_and_version = new byte[16];
    for (int i=0; i<12; i++)
      time_hi_and_version[i] = bits[bits.length - 48 - i - 1];

    time_hi_and_version[12] = ((new String("1")).getBytes())[0];
    time_hi_and_version[13] = ((new String("0")).getBytes())[0];
    time_hi_and_version[14] = ((new String("0")).getBytes())[0];
    time_hi_and_version[15] = ((new String("0")).getBytes())[0];

    // the clock_seq_low field
    BigInteger clockSequence = BigInteger.valueOf(CLOCK_SEQUENCE);
    String clockString = clockSequence.toString(2);
    if (clockString.length() < 14)
    {
      int nbExtraZeros  = 14 - bitString.length();
      String extraZeros = new String();
      for (int i=0; i<nbExtraZeros; i++)
        extraZeros = extraZeros.concat("0");

      clockString = extraZeros.concat(bitString);
    }

    byte[] clock_bits = clockString.getBytes();
    byte[] clock_seq_low = new byte[8];
    for (int i=0; i<8; i++)
      clock_seq_low[i] = clock_bits[clock_bits.length - i - 1];

    // the clock_seq_hi_and_reserved
    byte[] clock_seq_hi_and_reserved = new byte[8];
    for (int i=0; i<6; i++)
      clock_seq_hi_and_reserved[i] = clock_bits[clock_bits.length - 8 - i - 1];

    clock_seq_hi_and_reserved[6] = ((new String("0")).getBytes())[0];
    clock_seq_hi_and_reserved[7] = ((new String("1")).getBytes())[0];

    String timeLow = Long.toHexString((new BigInteger(new String(reverseArray(time_low)), 2)).longValue());
    if (timeLow.length() < 8)
    {
      int nbExtraZeros = 8 - timeLow.length();
      String extraZeros = new String();
      for (int i=0; i<nbExtraZeros; i++)
        extraZeros = extraZeros.concat("0");

      timeLow = extraZeros.concat(timeLow);
    }

    String timeMid = Long.toHexString((new BigInteger(new String(reverseArray(time_mid)), 2)).longValue());
    if (timeMid.length() < 4)
    {
      int nbExtraZeros = 4 - timeMid.length();
      String extraZeros = new String();
      for (int i=0; i<nbExtraZeros; i++)
        extraZeros = extraZeros.concat("0");

      timeMid = extraZeros.concat(timeMid);
    }

    String timeHiAndVersion = Long.toHexString((new BigInteger(new String(reverseArray(time_hi_and_version)), 2)).longValue());
    if (timeHiAndVersion.length() < 4)
    {
      int nbExtraZeros = 4 - timeHiAndVersion.length();
      String extraZeros = new String();
      for (int i=0; i<nbExtraZeros; i++)
        extraZeros = extraZeros.concat("0");

      timeHiAndVersion = extraZeros.concat(timeHiAndVersion);
    }

    String clockSeqHiAndReserved = Long.toHexString((new BigInteger(new String(reverseArray(clock_seq_hi_and_reserved)), 2)).longValue());
    if (clockSeqHiAndReserved.length() < 2)
    {
      int nbExtraZeros = 2 - clockSeqHiAndReserved.length();
      String extraZeros = new String();
      for (int i=0; i<nbExtraZeros; i++)
        extraZeros = extraZeros.concat("0");

      clockSeqHiAndReserved = extraZeros.concat(clockSeqHiAndReserved);
    }

    String clockSeqLow = Long.toHexString((new BigInteger(new String(reverseArray(clock_seq_low)), 2)).longValue());
    if (clockSeqLow.length() < 2)
    {
      int nbExtraZeros = 2 - clockSeqLow.length();
      String extraZeros = new String();
      for (int i=0; i<nbExtraZeros; i++)
        extraZeros = extraZeros.concat("0");

      clockSeqLow = extraZeros.concat(clockSeqLow);
    }

    // problem: the node should be the IEEE 802 ethernet address, but can not
    // be retrieved in Java yet.
    // see bug ID 4173528
    // workaround (also suggested in bug ID 4173528)
    // If a system wants to generate UUIDs but has no IEE 802 compliant
    // network card or other source of IEEE 802 addresses, then this section
    // describes how to generate one.
    // The ideal solution is to obtain a 47 bit cryptographic quality random
    // number, and use it as the low 47 bits of the node ID, with the most
    // significant bit of the first octet of the node ID set to 1. This bit
    // is the unicast/multicast bit, which will never be set in IEEE 802
    // addresses obtained from network cards; hence, there can never be a
    // conflict between UUIDs generated by machines with and without network
    // cards.

    long nodeValue = random.nextLong();
    nodeValue = Math.abs(nodeValue);
    while (nodeValue > 140737488355328L)
    {
      nodeValue = random.nextLong();
      nodeValue = Math.abs(nodeValue);
    }

    BigInteger nodeInt = BigInteger.valueOf(nodeValue);
    String nodeString = nodeInt.toString(2);
    if (nodeString.length() < 47)
    {
      int nbExtraZeros = 47 - nodeString.length();
      String extraZeros = new String();
      for (int i=0; i<nbExtraZeros; i++)
        extraZeros = extraZeros.concat("0");

      nodeString = extraZeros.concat(nodeString);
    }

    byte[] node_bits = nodeString.getBytes();
    byte[] node = new byte[48];
    for (int i=0; i<47; i++)
      node[i] = node_bits[node_bits.length - i - 1];

    node[47] = ((new String("1")).getBytes())[0];
    String theNode = Long.toHexString((new BigInteger(new String(reverseArray(node)), 2)).longValue());
    if (theNode.length() < 12)
    {
      int nbExtraZeros = 12 - theNode.length();
      String extraZeros = new String();
      for (int i=0; i<nbExtraZeros; i++)
        extraZeros = extraZeros.concat("0");
      theNode = extraZeros.concat(theNode);
    }

    String result = timeLow + "-" + timeMid +"-" + timeHiAndVersion + "-" + clockSeqHiAndReserved + clockSeqLow + "-" + theNode;

    return result.toUpperCase();
  }

  /**
   *
   */
  private static byte[] reverseArray(byte[] bits)
  {
    byte[] result = new byte[bits.length];
    for (int i=0; i<result.length; i++)
      result[i] = bits[result.length - 1 - i];

    return result;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String argc[])
  {
    DefaultUUIDGen generator = new DefaultUUIDGen();

    long start = System.currentTimeMillis();

    for (int i = 1; i <= 100; ++i)
      generator.uuidgen();

    long end = System.currentTimeMillis();

    System.out.println("QuickUUIDGen: Generation of 100 UUID's took "+(end-start)+" milliseconds.");
  }
}