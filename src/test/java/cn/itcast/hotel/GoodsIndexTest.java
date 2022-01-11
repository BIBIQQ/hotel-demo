package cn.itcast.hotel;

import cn.itcast.hotel.mapper.GoodsMapper;
import cn.itcast.hotel.pojo.Goods;
import cn.itcast.hotel.pojo.User;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

/**
 * @author FF
 * @date 2022/1/11
 * @TIME:23:13
 */
@SpringBootTest
class GoodsIndexTest {

    @Autowired
    private GoodsMapper goodsMapper;

    private RestHighLevelClient client;

    @BeforeEach
    void t0() {
        // 连接到ES的对象
        client = new RestHighLevelClient(RestClient.builder(HttpHost.create("http://175.24.175.153:9200")));
    }

    @AfterEach
    void t00() throws IOException {
        // 关闭连接
        client.close();
    }

    @Test
    void t1() throws IOException {
        // 创建索引
        // ES 是一个web服务器  给ES发请求
        // RequestOptions 是不是往请求头中添加信息
        CreateIndexResponse response = client.indices().create(new CreateIndexRequest("goods"), RequestOptions.DEFAULT);
        if (!response.isAcknowledged()) {
            System.out.println("索引创建失败");
        }
    }

    @Test
    void t4() throws IOException {
        // 给索引设置映射
        // 给哪个索引添加映射  映射是啥
        PutMappingRequest request = new PutMappingRequest("goods");
        request.source("{\n" +
                "    \"properties\":{\n" +
                "      \"id\":{\n" +
                "        \"type\":\"integer\",\n" +
                "        \"index\":\"true\"\n" +
                "      },\n" +
                "      \"title\":{\n" +
                "        \"type\":\"text\",\n" +
                "        \"analyzer\":\"ik_max_word\"\n" +
                "      },\n" +
                "      \"price\":{\n" +
                "        \"type\":\"double\",\n" +
                "        \"index\":\"false\"\n" +
                "      },\n" +
                "      \"stock\":{\n" +
                "        \"type\":\"integer\",\n" +
                "        \"index\":\"false\"\n" +
                "      },\n" +
                "      \"saleNum\":{\n" +
                "        \"type\":\"integer\",\n" +
                "        \"index\":\"false\"\n" +
                "      },\n" +
                "      \"createTime\":{\n" +
                "        \"type\":\"keyword\",\n" +
                "        \"index\":\"true\"\n" +
                "      },\n" +
                "      \"categoryName\":{\n" +
                "        \"type\":\"keyword\",\n" +
                "        \"index\":\"true\"\n" +
                "      },\n" +
                "      \"brandName\":{\n" +
                "        \"type\":\"keyword\",\n" +
                "        \"index\":\"true\"\n" +
                "      },\n" +
                "      \"spec\":{\n" +
                "       \"type\": \"object\"\n" +
                "      }\n" +
                "    }\n" +
                "  }", XContentType.JSON);
        AcknowledgedResponse response = client.indices().putMapping(request, RequestOptions.DEFAULT);
        System.out.println("exists = " + response.isAcknowledged());
    }


    @Test
    void  t5() throws IOException {

        List<Goods> goods = goodsMapper.selectList(null);
        BulkRequest request = new BulkRequest();
        for (Goods good : goods) {
            request.add(new IndexRequest("goods").id(good.getId().toString()).source(JSON.toJSONString(good),XContentType.JSON));

        }
        client.bulk(request, RequestOptions.DEFAULT);
    }

    @Test
    void  t7() throws IOException {

        List<Goods> goods = goodsMapper.selectList(null);
        for (Goods good : goods) {
            System.out.println("good = " + good);
        }

    }

    @Test
    void t6() throws IOException {
         SearchRequest searchR = new SearchRequest("goods");
         /*  SearchResponse response = client.search(searchR, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            //System.out.println(hit.getSourceAsMap());
            ObjectMapper objectMapper = new ObjectMapper();
            Goods user = objectMapper.readValue(hit.getSourceAsString(), Goods.class);
            System.out.println("user = " + user);
        }*/


        // term查询
        // brandName=三星
        /*searchR.source().query(QueryBuilders.termQuery("brandName", "三星")).size(100);
        SearchResponse response = client.search(searchR, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            //System.out.println(hit.getSourceAsMap());
            ObjectMapper objectMapper = new ObjectMapper();
            Goods user = objectMapper.readValue(hit.getSourceAsString(), Goods.class);
            System.out.println("user = " + user);
        }*/

        // mathch查询
        // title = 华为
        searchR.source().query(QueryBuilders.matchQuery("title","华为手机")).size(100);
        SearchResponse search = client.search(searchR, RequestOptions.DEFAULT);

        for (SearchHit hit : search.getHits().getHits()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Goods goods = objectMapper.readValue(hit.getSourceAsString(), Goods.class);
            System.out.println("goods = " + goods);
        }
    }

}
