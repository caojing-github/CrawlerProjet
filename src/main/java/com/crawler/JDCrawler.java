package com.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pojo.Item;
import com.service.ItemService;
import com.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JDCrawler {

    @Autowired
    private HttpClientUtils httpClientUtils;

    @Autowired
    private ItemService itemService;

    /**
     * 设置定时任务执行完成后，再间隔100秒执行一次
     */
    @Scheduled(fixedDelay = 1000 * 100)
    public void process() {

        //分析页面发现访问的地址,页码page从1开始，下一页oage加2
        String url = "https://search.jd.com/Search?keyword=手机&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&wq=手机&cid2=653&cid3=655&s=178&click=0&page=";

        //遍历执行，获取所有的数据
        for (int i = 1; i < 10; i = i + 2) {
            //发起请求进行访问，获取页面数据,先访问第一页
            String html = httpClientUtils.getContent(url + i);

            //解析页面数据，保存数据到数据库中
            parseHtml(html);
        }
        log.info("执行完成");
    }

    /**
     * 解析页面，并把数据保存到数据库中
     */
    private void parseHtml(String html) {
        //使用jsoup解析页面
        Document document = Jsoup.parse(html);
        log.debug("document--->\n" + document);
        //获取商品数据
        Elements spus = document.select("div#J_goodsList > ul > li");
        log.debug("spus--->\n" + spus);
        //遍历商品spu数据
        for (Element spuEle : spus) {
            //获取商品spu
            Long spuId = Long.parseLong(spuEle.attr("data-spu"));
            //获取商品sku数据
            Elements skus = spuEle.select("li.ps-item img");
            for (Element skuEle : skus) {
                //获取商品sku
                Long skuId = Long.parseLong(skuEle.attr("data-sku"));

                //判断商品是否被抓取过，可以根据sku判断
                List<Item> list = itemService.findAll(new Item().setSku(skuId));
                //判断是否查询到结果
                if (!CollectionUtils.isEmpty(list)) {
                    //如果有结果，表示商品已下载，进行下一次遍历
                    continue;
                }

                //获取商品标题
                String url = "https://item.jd.com/" + skuId + ".html";
                String itemHtml = httpClientUtils.getContent(url);
                String title = Jsoup.parse(itemHtml).select("div.sku-name").text();

                //获取商品价格
                String priceUrl = "https://p.3.cn/prices/mgets?skuIds=J_" + skuId;
                String priceJson = httpClientUtils.getContent(priceUrl);
                //解析json数据获取商品价格
                double price = JSON.parseArray(priceJson, JSONObject.class).get(0).getDoubleValue("p");

                //获取图片地址
                String pic = "https:" + skuEle.attr("data-lazy-img").replace("/n9/", "/n1/");
                log.debug(pic);
                //下载图片
                String picName = httpClientUtils.getImage(pic);

                Date now = new Date();

                //保存商品数据，声明商品对象
                Item item = new Item()
                    .setSpu(spuId)
                    .setSku(skuId)
                    .setUrl(url)
                    .setTitle(title)
                    .setPrice(price)
                    .setPic(picName)
                    .setCreated(now)
                    .setUpdated(now);

                itemService.save(item);
            }
        }
    }
}

