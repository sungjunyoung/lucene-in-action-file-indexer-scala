import java.io.{File, FileFilter, FileReader}

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Field.Store
import org.apache.lucene.document.{Document, TextField}
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.{Directory, FSDirectory}
import org.apache.lucene.util.Version

/**
  * Created by junyoung on 2017. 8. 14..
  */
class Worker(indexDir: String) {

  val dir: Directory = FSDirectory.open(new File(indexDir))

  // Make lucene IndexWriter
  val writerConfig: IndexWriterConfig =
    new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(Version.LUCENE_43))
  val writer: IndexWriter = new IndexWriter(dir, writerConfig)

  // Close IndexWriter
  def close(): Unit = {
    writer.close()
  }

  def index(dataDir: String, filter: FileFilter): Int = {
    val files: Array[File] = new File(dataDir).listFiles()

    println(files)

    for (f <- files) {
      if (!f.isDirectory &&
        !f.isHidden &&
        f.exists() &&
        f.canRead) {
        if(filter == null || filter.accept(f))
          indexFile(f)
      }
    }

    // return indexed document number
    writer.numDocs()
  }

  def indexFile(f: File): Unit = {
    val canoicalPath: String = f.getCanonicalPath
    println(s"Indexing $canoicalPath")
    val doc: Document = getDocument(f)
    // Add document to lucene index
    writer.addDocument(doc)
  }

  def getDocument(f: File): Document = {
    val doc: Document = new Document()
    // Add file contents
    doc.add(new TextField("contents", new FileReader(f)))
    // Add file name
    doc.add(new TextField("filename", f.getName, Store.YES))
    // Add file canonical path
    doc.add(new TextField("fullpath", f.getCanonicalPath, Store.YES))
    doc
  }

}
