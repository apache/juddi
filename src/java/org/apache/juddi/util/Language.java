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
package org.apache.juddi.util;

import java.lang.reflect.Field;

/**
 * An utility class for dealing with language codes.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class Language
{
  public static final String ABKHAZIAN = "ab";
  public static final String AFAR = "aa";
  public static final String AFRIKAANS = "af";
  public static final String ALBANIAN = "sq";
  public static final String AMHARIC = "am";
  public static final String ARABIC = "ar";
  public static final String ARMENIAN = "hy";
  public static final String ASSAMESE = "as";
  public static final String AYMARA = "ay";
  public static final String AZERBAIJANI = "az";
  public static final String BASHKIR = "ba";
  public static final String BASQUE = "eu";
  public static final String BENGALI = "bn";
  public static final String BHUTANI = "dz";
  public static final String BIHARI = "bh";
  public static final String BISLAMA = "bi";
  public static final String BRETON = "br";
  public static final String BULGARIAN = "bg";
  public static final String BURMESE = "my";
  public static final String BYELORUSSIAN = "be";
  public static final String CAMBODIAN = "km";
  public static final String CATALAN = "ca";
  public static final String CHINESE = "zh";
  public static final String CORSICAN = "co";
  public static final String CROATION = "hr";
  public static final String CZECH = "cs";
  public static final String DANISCH = "da";
  public static final String DUTCH = "nl";
  public static final String ENGLISH = "en";
  public static final String ESPERANTO = "eo";
  public static final String ESTONIAN = "et";
  public static final String FAEROESE = "fo";
  public static final String FARSI = "fa";
  public static final String FIJI = "fj";
  public static final String FINNISH = "fi";
  public static final String FRENCH = "fr";
  public static final String FRISIAN = "fy";
  public static final String GALICIAN = "gl";
  public static final String GEORGIAN = "ka";
  public static final String GERMAN = "de";
  public static final String GREEK = "el";
  public static final String GREENLANDIC = "kl";
  public static final String GUARANI = "gn";
  public static final String GUJARATI = "gu";
  public static final String HAUSA = "ha";
  public static final String HEBREW = "iw";
  public static final String HEBREW_2 = "he";
  public static final String HINDI = "hi";
  public static final String HUNGARIAN = "hu";
  public static final String ICELANDIC = "is";
  public static final String INDONESIAN = "in";
  public static final String INDONESIAN_2 = "id";
  public static final String INTERLINGUA = "ia";
  public static final String INTERLINGUE = "ie";
  public static final String INUKTITUT = "iu";
  public static final String INUPIAK = "ik";
  public static final String IRISH = "ga";
  public static final String ITALIAN = "it";
  public static final String JAPANESE = "ja";
  public static final String JAVANESE = "jw";
  public static final String KANNADA = "kn";
  public static final String KASHMIRI = "ks";
  public static final String KAZAKH = "kk";
  public static final String KINYARWANDA = "rw";
  public static final String KIRGHIZ = "ky";
  public static final String KIRUNDI = "rn";
  public static final String KOREAN = "ko";
  public static final String KURDISH = "ku";
  public static final String LAOTHIAN = "lo";
  public static final String LATIN = "la";
  public static final String LATVIAN = "lv";
  public static final String LINGALA = "ln";
  public static final String LITHUANIAN = "lt";
  public static final String MACEDONIAN = "mk";
  public static final String MALAGASY = "mg";
  public static final String MALAY = "ms";
  public static final String MALAYALAM = "ml";
  public static final String MALTESE = "mt";
  public static final String MANX_GAELIC = "gv";
  public static final String MAORI = "mi";
  public static final String MARATHI = "mr";
  public static final String MOLDAVIAN = "mo";
  public static final String MONGOLIAN = "mn";
  public static final String NAURU = "na";
  public static final String NEPALI = "ne";
  public static final String NORWEGIAN = "no";
  public static final String OCCITAN = "oc";
  public static final String ORIYA = "or";
  public static final String OROMO = "om";
  public static final String PASHTO = "ps";
  public static final String POLISH = "pl";
  public static final String PORTUGUESE = "pt";
  public static final String PUNJABI = "pa";
  public static final String QUECHUA = "qu";
  public static final String RHAETO_ROMANCE = "rm";
  public static final String ROMANIAN = "ro";
  public static final String RUSSIAN = "ru";
  public static final String SAMOAN = "sm";
  public static final String SANGRO = "sg";
  public static final String SANSKRIT = "sa";
  public static final String SCOTS_GAELIC = "gd";
  public static final String SERBIAN = "sr";
  public static final String SERBO_CROATIAN = "sh";
  public static final String SESOTHO = "st";
  public static final String SETSWANA = "tn";
  public static final String SHONA = "sn";
  public static final String SINDHI = "sd";
  public static final String SINGHALESE = "si";
  public static final String SISWATI = "ss";
  public static final String SLOVAK = "sk";
  public static final String SLOVENIAN = "sl";
  public static final String SOMALI = "so";
  public static final String SPANISH = "es";
  public static final String SUNDANESE = "su";
  public static final String SWAHILI = "sw";
  public static final String SWEDISH = "sv";
  public static final String TAGALOG = "tl";
  public static final String TAJIK = "tg";
  public static final String TAMIL = "ta";
  public static final String TATAR = "tt";
  public static final String TELUGU = "te";
  public static final String THAI = "th";
  public static final String TIBETAN = "bo";
  public static final String TIGRINYA = "ti";
  public static final String TONGA = "to";
  public static final String TSONGA = "ts";
  public static final String TURKISH = "tr";
  public static final String TURKMEN = "tk";
  public static final String TWI = "tw";
  public static final String UIGHUR = "ug";
  public static final String UKRAINIAN = "uk";
  public static final String URDU = "ur";
  public static final String UZBEK = "uz";
  public static final String VIETNAMESE = "vi";
  public static final String VOLAPUK = "vo";
  public static final String WELSH = "cy";
  public static final String WOLOF = "wo";
  public static final String XHOSA = "xh";
  public static final String YIDDISH = "ji";
  public static final String YIDDISH_2 = "yi";
  public static final String YORUBA = "yo";
  public static final String ZULU = "zu";

  /**
   * Checks if the given code is a valid ISO language code.
   * @return True if the code is a valid language code, false otherwise.
   */
  public static boolean isLanguageCode(String code)
  {
    // check this using the reflection API
    try
    {
      Field[] codes = Language.class.getFields();

      for (int i=0; i<codes.length; i++)
      {
        if (codes[i].get(null).equals(code))
          return true;
      }
    }
    catch (IllegalAccessException e) { }

    return false;
  }
}