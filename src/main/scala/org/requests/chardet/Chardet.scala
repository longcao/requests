package org.requests.chardet

import org.mozilla.universalchardet.UniversalDetector

/**
 * Chardet wrapper object for the universalchardet java API.
 */
object Chardet {

  /**
   * Attempts to guess the encoding of bytes, if any.
   */
  def detectEncoding(bytes: Array[Byte]): Option[String] = {
    val detector: UniversalDetector = new UniversalDetector(null)

    detector.handleData(bytes, 0, bytes.length)
    detector.dataEnd()

    // getDetectedCharset can be 'null' so wrap in Option
    Option(detector.getDetectedCharset)
  }
}
