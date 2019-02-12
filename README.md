## resource util

Provides miscellaneous resource management utilities

Examples:

1. Using AutoCloseable in for comprehension (copying streams):
```scala
import com.agk.resource.CloseableUtil._
import java.io.{FileInputStream, FileOutputStream}
for {
  in <- new FileInputStream("input-file")
  out <- new FileOutputStream("output-file")
} {
  val buffer = new Array[Byte](1024)
  var n = in.read(buffer)
  while (n >= 0) {
    out.write(buffer, 0, n)
    n = in.read(buffer)
  }
}
```

2. Turning arbitrary class into AutoCloseable by providing an explicit cleanup.
See: [FileUtil.scala](src/main/scala/com/agk/resource/FileUtil.scala)