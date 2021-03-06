package com.xcar360.kafka.config;

import com.alibaba.fastjson.JSON;
import com.xcar360.mongo.MessageTemplate;
import com.xcar360.util.KafkaConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Date;

/**
 * @author xulaing
 * @date 2018年10月11日 14:44:55
 */
public class Listener {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());



    @Autowired
    private MongoTemplate mongoTemplate;

    @KafkaListener(id = "demo",topics = {KafkaConstants.KAFKA_TEST1_TOPIC_MESSAGE},containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<?, ?> record,Acknowledgment ack) {

        logger.info("Listener  kafka的key: " + record.key());
        logger.info("Listener  kafka的offset: " + record.offset());
        logger.info("Listener  kafka的partition: " + record.partition());
        logger.info("Listener  kafka的value: " + record.value().toString());

        //获取数据
        MessageTemplate messageTemplate = JSON.toJavaObject((JSON) JSON.parse(record.value().toString()), MessageTemplate.class);;

        //记录访问时间
        messageTemplate.setAcceptTime(new Date());

        //记录日志
        saveConsumeredMessageLog(messageTemplate);


        ack.acknowledge();

//        //EditVo editVo = JSON.toJavaObject((JSON) JSON.parse(record.value().toString()), EditVo.class);
//        //System.out.println("editVo type111:" + editVo.getType());
//        //System.out.println("editVo getTableName:" + editVo.getTableName());
//        //System.out.println("editVo getDataStr :" + editVo.getDataStr());
//
//        JSONObject jsonObject = JSON.parseObject(record.value().toString());
//        String type = jsonObject.getString("type");
//        try {
//            if("EDIT".equals(type) || "ADD".equals(type)){
//                EditVo editVo = JSON.toJavaObject((JSON) JSON.parse(record.value().toString()), EditVo.class);
//                Class aClass = Class.forName("cn.sinobest.jzpt.send.model." + editVo.getTableName());
//                Object o = JSON.toJavaObject((JSON) JSON.parse(editVo.getDataStr()), aClass);
//
//                CrudRepository crudRepository = (CrudRepository) SpringUtil.getBean(editVo.getTableName() + "Repository");
//                crudRepository.save(o);
//            }else if("DELETE".equals(type )){
//                DeleteVo deleteVo = JSON.toJavaObject((JSON) JSON.parse(record.value().toString()),DeleteVo.class);
//                System.out.println("删除");
//                String deleteType = deleteVo.getDeleteType();
//                String deleteField = deleteVo.getLogicDeleteField();
//                String deleteFlagValue = deleteVo.getDeleteFlagValue();
//                String idField = deleteVo.getIdField();
//                String idValues = deleteVo.getIdValues();
//                String tableName = deleteVo.getTableName();
//
//                if("LOGIC".equals(deleteType)){//逻辑删除
//
//                    if(deleteVo.isBatch()){
//                        jdbcTemplate.update("UPDATE " + tableName + " SET " + deleteField + " = " + deleteFlagValue + " WHERE " + idField + " in( " + idValues + ")");
//                    }else {
//                        jdbcTemplate.update("UPDATE " + tableName + " SET " + deleteField + " = " + deleteFlagValue + " WHERE " + idField + " = ?", idValues);
//                    }
//                }else {//物理删除
//                    if(deleteVo.isBatch()){
//                        jdbcTemplate.update("DELETE from " + tableName + " where " + idField + " in(" + idValues + ")");
//                    }else {
//                        jdbcTemplate.update("DELETE from " + tableName + " where " + idField + " = ?", idValues );
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//        }
/*
        try {
            Class aClass = Class.forName("cn.sinobest.jzpt.send.model.B_JWRQ_APPMENU");
            System.out.println("反射--11111------------");
            Object obj1 = JSON.toJavaObject((JSON) JSON.parse(record.value().toString()), aClass);

            CrudRepository crudRepository = (CrudRepository)SpringUtil.getBean("B_JWRQ_APPMENURepository11");
            crudRepository.save(obj1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
*/

        /*B_JWRQ_APPMENU b_JWRQ_APPMENU = JSON.toJavaObject((JSON) JSON.parse(record.value().toString()), B_JWRQ_APPMENU.class);
        System.out.println("menuname:" + b_JWRQ_APPMENU.getMenuname());
        System.out.println("systemid:" + b_JWRQ_APPMENU.getSystemid());
        B_JWRQ_APPMENU app2 = b_jwrq_appmenuRepository.save(b_JWRQ_APPMENU);*/

    }

    void saveConsumeredMessageLog(Object obj){
        mongoTemplate.insert(obj);
    }
}
