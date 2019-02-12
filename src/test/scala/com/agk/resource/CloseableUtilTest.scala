package com.agk.resource

import java.io.{File, FileInputStream, FileOutputStream}

import CloseableUtil._
import CloseableUtilTest._
import FileUtil.withTempDir
import org.apache.commons.io.FileUtils.{readFileToString, writeStringToFile}
import org.junit.Assert.assertEquals
import org.junit.Test
import org.scalatest.Assertions

class CloseableUtilTest extends Assertions {

  @Test def testCopyFiles(): Unit = withTempDir(getClass.getName) { rootDir =>
    val src = new File(rootDir, "src")
    val dst = new File(rootDir, "dst")
    writeStringToFile(src, "test data")
    for {
      in <- new FileInputStream(src)
      out <- new FileOutputStream(dst)
    } {
      val buffer = new Array[Byte](1024)
      var n = in.read(buffer)
      while (n >= 0) {
	out.write(buffer, 0, n)
	n = in.read(buffer)
      }
    }
    assertEquals("test data", readFileToString(dst))
  }

  @Test def testWithCleanup(): Unit = {
    var cleanupTrace = List.empty[String]
    val result = for {
      a <- "a" withCleanup (v => cleanupTrace = v :: cleanupTrace)
      b <- "b" withCleanup (v => cleanupTrace = v :: cleanupTrace)
    } yield a + b
    assertEquals("ab", result)
    assertEquals(List("a", "b"), cleanupTrace)
  }

  @Test def testNoFailure(): Unit = {
    assertEquals(12345, useResource())
  }

  @Test def testFailOnCreate(): Unit = {
    val e = intercept[AssertionError](useResource(failOnCreate = true))
    assertEquals("failed on create", e.getMessage)
  }

  @Test def testFailOnUse(): Unit = {
    val e = intercept[AssertionError](useResource(failOnUse = true))
    assertEquals("failed on use", e.getMessage)
  }

  @Test def testFailOnClose(): Unit = {
    val e = intercept[AssertionError](useResource(failOnClose = true))
    assertEquals("failed on close", e.getMessage)
  }

  @Test def testFailOnCreateUseAndClose(): Unit = {
    val e = intercept[AssertionError](useResource(failOnCreate = true, failOnUse = true, failOnClose = true))
    assertEquals("failed on create", e.getMessage)
    assertEquals(Seq.empty[Throwable], e.getSuppressed.toSeq)
  }

  @Test def testFailOnUseAndClose(): Unit = {
    val e = intercept[AssertionError](useResource(failOnUse = true, failOnClose = true))
    assertEquals("failed on use", e.getMessage)
    e.getSuppressed match {
      case Array(suppressed: AssertionError) =>
        assertEquals("failed on close", suppressed.getMessage)
      case other =>
        fail(s"Unexpected suppressed exception: $e")
    }
  }

}

object CloseableUtilTest {

  def useResource(
	  failOnCreate:Boolean = false,
	  failOnUse:   Boolean = false,
	  failOnClose: Boolean = false
  ): Int = {
    withCleanup(new Resource(failOnCreate, failOnUse, failOnClose))(_.use())
  }

  class Resource(
	  failOnCreate:Boolean = false,
	  failOnUse:   Boolean = false,
	  failOnClose: Boolean = false
  ) extends java.lang.AutoCloseable {

    if (failOnCreate) throw new AssertionError("failed on create")

    def use(): Int    =
      if (!failOnUse) 12345
      else throw new AssertionError("failed on use")

    def close(): Unit =
      if (failOnClose) throw new AssertionError("failed on close")

  }

}
