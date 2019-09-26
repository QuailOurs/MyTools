package com.bj58.sec.test.demo;

public interface KafkaProperties {

//    String BOOTSTRAP_ERVERS = "10.135.9.4:9092,10.135.9.5:9092,10.135.9.6:9092,10.135.9.7:9092,10.135.9.8:9092";
//    String TOPIC_NAME = "hdp_teu_ops_nginx_log";

//    String BOOTSTRAP_ERVERS = "op.hdp.kakfa-1.58dns.org:9092,op.hdp.kakfa-2.58dns.org:9092,op.hdp.kakfa-3.58dns.org:9092,op.hdp.kakfa-4.58dns.org:9092,op.hdp.kakfa-5.58dns.org:9092";
//    String TOPIC_NAME = "hdp_teu_ops_nginx_log";
	
//    String TOPIC_NAME = "hdp_teu_spat_im_access_logdispatch";
//    String BOOTSTRAP_ERVERS = "10.135.9.4:9092,10.135.9.5:9092,10.135.9.6:9092,10.135.9.7:9092,10.135.9.8:9092";
    
//    String TOPIC_NAME = "hdp_teu_op_skymirror";
//    String BOOTSTRAP_ERVERS = "10.135.9.4:9092,10.135.9.5:9092,10.135.9.6:9092,10.135.9.7:9092,10.135.9.8:9092";
    
    String TOPIC_NAME = "hdp_teu_op_thirdservice_log";
    String BOOTSTRAP_ERVERS = "10.135.9.4:9092,10.135.9.5:9092,10.135.9.6:9092,10.135.9.7:9092,10.135.9.8:9092";
    
    String RES_TOPIC_NAME = "hdp_teu_dpd_test_apache_logs_ctp_target";
    // hadoop账号-groupId groupId体现出生产或者消费的Topic和所属的项目名
    String GROUP_ID = "hdp_teu_op_mysql_audit_group";
    // clientId使用创建Topic时工单生成的clientId，也可以在NightFury申请
//    String CLIENT_ID_CONSUMER = "hdp_teu_op-hdp_teu_op_mysql_audit-zt72S";
//    String CLIENT_ID_CONSUMER = "hdp_teu_spat_im-hdp_teu_spat_im_access_logdispatch-zANjQ";
//    String CLIENT_ID_CONSUMER = "hdp_teu_op-hdp_teu_op_skymirror-bRe09";
    String CLIENT_ID_CONSUMER = "hdp_teu_op-hdp_teu_op_thirdservice_log-GlZYR";
    // clientId使用创建Topic时工单生成的clientId，也可以在NightFury申请
    String CLIENT_ID_PRODUCER = "hdp_teu_dpd-test_apache_logs_ctp_target";
    // hadoop账号-transactionId
    String TRANSACTION_ID = "hdp_teu_dpd-ctp_demo";

    Long  CONSUMER_POLL_TIMEOUT = 200L;
}
