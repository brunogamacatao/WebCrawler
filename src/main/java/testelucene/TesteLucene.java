package testelucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class TesteLucene {
  private static final String INDEX_DIR = "/home/bruno/dev/facisa/tap/TesteLucene/index";

  private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
    Document doc = new Document();
    doc.add(new TextField("title", title, Field.Store.YES));
    doc.add(new StringField("isbn", isbn, Field.Store.YES));
    w.addDocument(doc);
  }

  public static void main(String[] args) throws Exception {
    Path indexPath = FileSystems.getDefault().getPath(INDEX_DIR);

    StandardAnalyzer analyzer = new StandardAnalyzer();
    Directory index = new MMapDirectory(indexPath);

    IndexWriterConfig config = new IndexWriterConfig(analyzer);

    IndexWriter w = new IndexWriter(index, config);
    addDoc(w, "Lucene in Action", "193398817");
    addDoc(w, "Lucene for Dummies", "55320055Z");
    addDoc(w, "Managing Gigabytes", "55063554A");
    addDoc(w, "The Art of Computer Science", "9900333X");
    w.close();

    Query q = new QueryParser("title", analyzer).parse("lucene");

    int hitsPerPage = 10;
    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);
    TopDocs docs = searcher.search(q, hitsPerPage);
    ScoreDoc[] hits = docs.scoreDocs;

    System.out.println("Found " + hits.length + " hits.");
    for(int i = 0; i < hits.length; i++) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
    }
  }
}
