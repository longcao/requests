package org.requests.chardet

import org.mozilla.universalchardet.UniversalDetector

object Chardet {
  def detectEncoding(bytes: Array[Byte]): Option[String] = {
    val detector: UniversalDetector = new UniversalDetector(null)

    detector.handleData(bytes, 0, bytes.length)
    detector.dataEnd()

    // getDetectedCharset can be 'null' so wrap in Option
    Option(detector.getDetectedCharset)
  }
}
