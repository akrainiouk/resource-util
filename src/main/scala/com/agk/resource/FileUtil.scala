package com.agk.resource

import java.io.File

import org.apache.commons.io.FileUtils.forceDelete

object FileUtil {

  def withTempDir[T](prefix: String)(code: File => T): T = {
    val file = File.createTempFile(prefix, "")
    file.delete()
    try {
      file.mkdirs()
      code(file)
    } finally {
      forceDelete(file)
    }
  }

}
