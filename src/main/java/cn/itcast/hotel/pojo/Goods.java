package cn.itcast.hotel.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("goods")
public class Goods {

    private Integer id;
    private String title;
    private Double price;
    private Integer stock;
    private Integer saleNum;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date createTime;
    private String categoryName;
    private String brandName;
    @TableField(exist = false)
    private HashMap spec;
    @TableField("spec")
    private String specStr;


    public HashMap getSpec() {
        spec = new HashMap<>();
        this.specStr = this.specStr.replace("\"", "");
        this.specStr = this.specStr.replace("}", "");
        this.specStr = this.specStr.replace("{", "");

        String[] split = this.specStr.split(",");
        for (int i = 0; i < split.length; i++) {

            String[] split1 = split[i].split(":");
            spec.put("specifications",split1[0]);
            spec.put("parameter",split1[1]);
        }
        return spec;
    }


}




/*

需求:
1. 使用MybatisPlus查询goods表
2. 在ES中创建索引并按照goods表添加映射 (title字段需要分词)
3. 将goods表中的数据导入到ES中
4. 查询品牌是三星的
5. 查询title中包含华为手机的
*/