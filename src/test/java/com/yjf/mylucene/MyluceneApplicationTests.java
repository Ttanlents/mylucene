package com.yjf.mylucene;

import com.yjf.mylucene.dao.GoodsDao;
import com.yjf.mylucene.entity.Goods;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MyluceneApplicationTests {
    @Autowired
    GoodsDao goodsDao;

    @Test
    public void created() throws IOException {
        //1.获取数据
        List<Goods> goods = goodsDao.findAll();

        //2.对应的分词器策略
        //StandardAnalyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //3. 建立索引配置对象（选着索引版本，和对应的分词器）
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        //索引库不存在就创建，存在则不创建，直接追加索引内容
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        //设置写目录对象
        FSDirectory directory = FSDirectory.open(new File("D:\\index"));
        //通过索引写对象  写出数据，编程文档对象
        List<Document> docs = new ArrayList<>();
        for (Goods good : goods) {
            Document document = new Document();
            //装换java对象属性到文档对象属性
            //like 索引（查询）  存储   分词
            StringField id = new StringField("id", good.getId() + "", Field.Store.YES);
            TextField name = new TextField("name", good.getName(), Field.Store.YES);
            TextField title = new TextField("title", good.getName(), Field.Store.YES);
            DoubleField price = new DoubleField("price", good.getPrice(), Field.Store.YES);
            StoredField pic = new StoredField("pic", good.getPrice());
            document.add(id);
            document.add(name);
            document.add(title);
            document.add(price);
            document.add(pic);
            docs.add(document);
        }
        IndexWriter indexWriter = new IndexWriter(directory, config);
        indexWriter.addDocuments(docs);
        indexWriter.commit();
        indexWriter.close();
    }

    //1.lucene 的查询操作一：解析器查询   会把查询条件分词
    @Test
    public void testQuery1() throws IOException, ParseException {
        //1.打开索引库，创建索引目录
        FSDirectory directory = FSDirectory.open(new File("D:\\index"));
        //2.创建目录读对象  读取索引
        DirectoryReader reader = DirectoryReader.open(directory);
        //3.创建索引搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        //queryParser解析器
        QueryParser queryParser = new QueryParser(Version.LATEST, "name", new IKAnalyzer());
        Query query = queryParser.parse("小米");
        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] docs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : docs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println(doc.get("id"));
            System.out.println(doc.get("name"));
            System.out.println(doc.get("title"));
            System.out.println(doc.get("price"));
            System.out.println(scoreDoc.score); //击中率
        }
        reader.close();


    }

    @Test  //2.固定词条查询对象 查询  不会把查询条件分词
    public void testQuery2() throws IOException, ParseException {
        //1.打开索引库，创建索引目录
        FSDirectory directory = FSDirectory.open(new File("D:\\index"));
        //2.创建目录读对象  读取索引
        DirectoryReader reader = DirectoryReader.open(directory);
        //3.创建索引搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(reader);

        TermQuery termQuery = new TermQuery(new Term("id", "20"));
        TopDocs topDocs = indexSearcher.search(termQuery, 10);
        ScoreDoc scoreDoc = topDocs.scoreDocs[0];
        Document doc = indexSearcher.doc(scoreDoc.doc);
        System.out.println(doc.get("id"));
        System.out.println(doc.get("name"));
        System.out.println(doc.get("title"));
    }

    @Test  //3.范围查询 numericRangeQuery  查询价格范围
    public void testQuery3() throws IOException, ParseException {
        //1.打开索引库，创建索引目录
        FSDirectory directory = FSDirectory.open(new File("D:\\index"));
        //2.创建目录读对象  读取索引
        DirectoryReader reader = DirectoryReader.open(directory);
        //3.创建索引搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(reader);

        NumericRangeQuery<Double> query = NumericRangeQuery.newDoubleRange("price", 2999d, 8999d, false, false);

        TopDocs topDocs = indexSearcher.search(query, 1);
        ScoreDoc scoreDoc = topDocs.scoreDocs[0];
        Document doc = indexSearcher.doc(scoreDoc.doc);
        System.out.println(doc.get("id"));
        System.out.println(doc.get("name"));
        System.out.println(doc.get("title"));
    }

    @Test  //4.多条件查询  BooleanQuery
    public void testQuery4() throws IOException, ParseException {
        //1.打开索引库，创建索引目录
        FSDirectory directory = FSDirectory.open(new File("D:\\index"));
        //2.创建目录读对象  读取索引
        DirectoryReader reader = DirectoryReader.open(directory);
        //3.创建索引搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(reader);

        QueryParser queryParser = new QueryParser(Version.LATEST, "name", new IKAnalyzer());
        Query query = queryParser.parse("小米");

        NumericRangeQuery<Double> query2 = NumericRangeQuery.newDoubleRange("price", 2999d, 8999d, false, false);

        BooleanQuery booleanQuery = new BooleanQuery();

        booleanQuery.add(query, BooleanClause.Occur.MUST);
        booleanQuery.add(query2, BooleanClause.Occur.MUST);

        TopDocs topDocs = indexSearcher.search(booleanQuery, 10);
        ScoreDoc[] docs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : docs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println(doc.get("id"));
            System.out.println(doc.get("name"));
            System.out.println(doc.get("title"));
            System.out.println(scoreDoc.score); //击中率
        }
    }
    @Test //索引修改
            //如果不存在  就新增
            //存在就修改
    public void testUpdate() throws IOException {
        IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LATEST, new IKAnalyzer());
        FSDirectory directory = FSDirectory.open(new File("D:\\index"));
        IndexWriter writer = new IndexWriter(directory, writerConfig);
        Term term = new Term("id", "29");
        Document document = new Document();
        document.add(new StringField("id","29", Field.Store.YES));
        document.add(new TextField("name","东方标准手机11", Field.Store.YES));
        document.add(new TextField("price","2999.0", Field.Store.YES));
        writer.updateDocument(term,document);  //修改或新增
        writer.commit();
        writer.close();

    }

        @Test //删除
    public void testDelete() throws IOException {
        IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LATEST, new IKAnalyzer());
        FSDirectory directory = FSDirectory.open(new File("D:\\index"));
        IndexWriter writer = new IndexWriter(directory, writerConfig);
        Term term = new Term("id", "29");

        //writer.deleteDocuments(term);  根据词条删除
        //writer.deleteAll();   删除全部
        writer.commit();
        writer.close();

    }
}
