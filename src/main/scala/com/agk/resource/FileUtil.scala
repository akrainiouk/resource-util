package com.agk.resource

import java.io.File
import java.io.File.createTempFile

import com.agk.resource.CloseableUtil._
import org.apache.commons.io.FileUtils.forceDelete

object FileUtil {

  def withTempDir[T](prefix: String)(code: File => T): T = {
    for (file <- createTempFile(prefix, "").withCleanup(forceDelete)) yield {
      file.delete()
      file.mkdirs()
      code(file)
    }
  }
  
}
