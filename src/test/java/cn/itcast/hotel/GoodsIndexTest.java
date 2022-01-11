package cn.itcast.hotel;

import cn.itcast.hotel.mapper.GoodsMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

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
        client = new RestHighLevelClient(RestClient.builder(HttpHost.create("http://192.168.131.31:9200")));
    }

    @AfterEach
    void t00() throws IOException {
        // 关闭连接
        client.close();
    }

}
