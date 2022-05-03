package com.my.spider.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.my.spider.common.SysConstant;
import com.my.spider.entity.GoodsInfo;
import com.my.spider.mapper.GoodsInfoMapper;
import com.my.spider.service.GoodsInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.spider.util.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author kun
 * @since 2022-05-03
 */
@Service
public class GoodsInfoServiceImpl extends ServiceImpl<GoodsInfoMapper, GoodsInfo> implements GoodsInfoService {
    private Logger logger = LoggerFactory.getLogger(GoodsInfoServiceImpl.class);

    @Autowired(required = false)
    private GoodsInfoMapper goodsInfoMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    //协议
    private static String HTTPS_PROTOCOL = "https:";


    synchronized public void spiderData(String url, Map<String,String> parms){
        String html = HttpClientUtil.sendGet(url, SysConstant.setHeader(), parms);
        if(!StringUtils.isBlank(html)){
            List<GoodsInfo> goodsInfos = parseHtml(html);
            //批量插入（可优化）
            for(GoodsInfo goodsInfo : goodsInfos){
                goodsInfoMapper.insert(goodsInfo);
            }
        }else{
            redisTemplate.opsForList().leftPush("page",parms.get("page"));
        }
    }

    /**
     * 解析html
     * @param html
     */
    private List<GoodsInfo> parseHtml(String html) {
        //商品集合
        List<GoodsInfo> goods = Lists.newArrayList();
        /**
         *  获取dom并解析
         */

        //使用jsoup工具解释dom
        Document document = Jsoup.parse(html);
        Elements elements = document.select("ul[class=gl-warp clearfix]").select("li[class=gl-item]");
        int index = 0;
        for (Element element : elements) {
            String goodsId = element.attr("data-sku");
            String goodsName = element.select("div[class=p-name p-name-type-2]").select("em").text();
            String goodsPrice = element.select("div[class=p-price]").select("strong").select("i").text();
            String imgUrl = HTTPS_PROTOCOL + element.select("div[class=p-img]").select("a").select("img").attr("src");
            GoodsInfo goodsInfo = new GoodsInfo(goodsId, goodsName, imgUrl, goodsPrice);
            goods.add(goodsInfo);
            String jsonStr = JSON.toJSONString(goodsInfo);
            logger.info("成功爬取【" + goodsName + "】的基本信息 ");
            logger.info(jsonStr);
            if (index++ == 9) {
                break;
            }
        }
        return goods;
    }
}
