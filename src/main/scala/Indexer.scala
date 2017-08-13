
/**
  * Created by junyoung on 2017. 8. 13..
  */


object Indexer {
  def main(args: Array[String]): Unit = {

    // check arguments number
    if (args.length != 2) {
      throw new IllegalArgumentException("Usage: scalac Indexer.scala <index dir> <data dir>")
    }

    // set arguments
    /*
     args(0) : make index to this directory
     args(1) : folder that include .txt file
     */
    var indexDir = args(0)
    var dataDir = args(1)

    var start = System.currentTimeMillis()
    var indexer = new Worker(indexDir)
    var numIndexed = 0

    try {
      numIndexed = indexer.index(dataDir, new TextFilesFilter())
    } finally {
      indexer.close()
    }

    val end:Long = System.currentTimeMillis()
    val term = end - start
    println(s"Indexing $numIndexed files took $term milliseconds")
  }


}
