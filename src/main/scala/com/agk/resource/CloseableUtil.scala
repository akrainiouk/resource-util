package com.agk.resource

import CloseableUtilImpl.tryWithResources

object CloseableUtil {

  class AutoCloseableAdapter[A](val a: A, val cleanup: A => Unit) extends AutoCloseable {
    def foreach(f: A => Unit): Unit = tryWithResources[AutoCloseable, Unit](() => this, _ => f(a))
    def map[R](f: A => R): R = tryWithResources[AutoCloseable, R](() => this, _ => f(a))
    def flatMap[R](f: A => R): R = tryWithResources[AutoCloseable, R](() => this, _ => f(a))
    override def close(): Unit = cleanup(a)
  }

  implicit class CleanupSupport[A <: AnyRef](a: A) {
    def withCleanup(cleanup: A => Unit): AutoCloseableAdapter[A] = new AutoCloseableAdapter[A](a, cleanup)
  }

  implicit class RichAutoCloseable[A <: AutoCloseable](val a: A) extends AnyVal {
    def foreach(f: A => Unit): Unit = tryWithResources(() => a, f)
    def map[R](f: A => R): R = tryWithResources(() => a, f)
    def flatMap[R](f: A => R): R = tryWithResources(() => a, f)
  }

  def withCleanup[A <: AutoCloseable, R](a: => A)(code: A => R): R =
    tryWithResources(() => a, code)

  def withCleanup[A <: AutoCloseable, B <: AutoCloseable, R](
	  a: => A, b: => B
  )(
	  code: (A, B) => R
  ): R =
    tryWithResources(() => a, () => b, code)

  def withCleanup[A <: AutoCloseable, B <: AutoCloseable, C <: AutoCloseable, R](
	  a: => A, b: => B, c: => C
  )(
	  code: (A, B, C) => R
  ): R =
    tryWithResources(() => a, () => b, () => c, code)

  def withCleanup[A <: AutoCloseable, B <: AutoCloseable, C <: AutoCloseable, D <: AutoCloseable, R](
	  a: => A, b: => B, c: => C, d: => D
  )(
	  code: (A, B, C, D) => R
  ): R = tryWithResources(() => a, () => b, () => c, () => d, code)

}