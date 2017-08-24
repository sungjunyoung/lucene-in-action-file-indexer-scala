import java.io.IOException

import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import org.apache.lucene.analysis.core.WhitespaceAnalyzer
import org.apache.lucene.document.Field.Store
import org.apache.lucene.document.{Document, TextField}
import org.apache.lucene.index._
import org.apache.lucene.search.{IndexSearcher, Query, TermQuery, TopDocs}
import org.apache.lucene.store.{Directory, RAMDirectory}
import org.apache.lucene.util.Version
import org.scalatest.junit.AssertionsForJUnit


class IndexerTest extends AssertionsForJUnit{
  protected val ids: Array[String] = Array("1", "2")
  protected val unindexed: Array[String] = Array("Netherlands", "Italy")
  protected val unstored: Array[String] =
    Array("Amsterdam hs lots of bridges", "Venice has lots of canals")
  protected val text: Array[String] = Array("Amsterdam", "Venice")

  var directory: Directory = new RAMDirectory()

  @Before
  def setUp(): Unit = {
    val writer: IndexWriter = getWriter()
    for (i <- ids.indices) {
      val doc: Document = new Document()
      doc.add(new TextField("id", ids(i), Store.YES))
      doc.add(new TextField("country", unindexed(i), Store.YES))
      doc.add(new TextField("contents", unstored(i), Store.NO))
      doc.add(new TextField("city", text(i), Store.YES))

      writer.addDocument(doc)
    }
    writer.close()
  }


  def getWriter(): IndexWriter = {
    val writerConfig: IndexWriterConfig =
      new IndexWriterConfig(Version.LUCENE_43, new WhitespaceAnalyzer(Version.LUCENE_43))
    new IndexWriter(directory, writerConfig)
  }

  def getHintCount(fieldName: String, searchString: String): Int = {
    val ir: IndexReader = DirectoryReader.open(directory)
    val searcher: IndexSearcher = new IndexSearcher(ir)
    val t: Term = new Term(fieldName, searchString)
    val query: Query = new TermQuery(t)

    val hits: TopDocs = searcher.search(query, 10)

    val hitCount: Int = hits.totalHits
    hitCount
  }
  @Test
  def testIndexWriter(): Unit = {
    val writer: IndexWriter = getWriter()
    assertEquals(ids.length, writer.numDocs())
    writer.close()
  }
  @Test
  def testIndexReader(): Unit ={
    val reader:IndexReader  = DirectoryReader.open(directory)
    assertEquals(ids.length, reader.maxDoc())
    assertEquals(ids.length, reader.numDocs())
  }

}
