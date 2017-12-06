package com.lzh.micro.framework.registry;

import com.lzh.micro.framework.annotation.ServiceName;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: lizhuohang
 *
 * @Date: Created in 17:28 17/12/5
 */
@Aspect
@Component
public class ServiceRegistry implements ApplicationListener<ContextRefreshedEvent>, Watcher {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    private static CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zk;

    @Value("${msa.registry.zk.address}")
    private String zkAddress;

    @Value("${msa.registry.zk.timeout}")
    private int zkTimeout;

    @Value("${msa.registry.zk.registry.path}")
    private String registryPath;

    /**
     * 此处本地服务地址写入业务配置文件中，可以考虑扩展为动态获取
     * 动态获取方式多种
     * 1.获取网络信息
     * 2.linux系统变量中写入
     * 3.通过第三方，或者自己开发的ip获取服务获取本地ip
     * 4. ...
     * 端口可以直接使用业务代码中的配置文件里面的端口号
     */
    @Value("${msa.registry.zk.registry.serviceAddress}")
    private String serviceAddress;

    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            latch.countDown();
        }
    }

    private synchronized void initZk() {
        if (zk != null) {
            return;
        }
        try {
            zk = new ZooKeeper(zkAddress, zkTimeout, this);
            latch.await();
            logger.info("connected to zookeeper");
        } catch (Exception e) {
            logger.error("create zookeeper client failure", e);
        }
    }

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Map<String, Object> beans = contextRefreshedEvent.getApplicationContext()
                .getBeansWithAnnotation(Controller.class);
        initZk();
        for (String key : beans.keySet()) {
            Class<?> serviceClass = beans.get(key).getClass();
            Method[] methods = serviceClass.getMethods();
            if (methods == null) {
                continue;
            }
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].isAnnotationPresent(ServiceName.class)) {
                    String serviceName = methods[i].getAnnotation(ServiceName.class).value();
                    if (serviceName == null || serviceName.trim().isEmpty()) {
                        continue;
                    }
                    register(serviceName, serviceAddress);
                }
            }
        }
    }

    private void register(String serviceName, String serviceAddress) {
        try {
            // 创建根节点
            if (this.zk.exists(registryPath, false) == null) {
                zk.create(registryPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL.PERSISTENT);
                logger.info("create registry node: {}", registryPath);
            }
            // 创建服务节点
            String servicePath = registryPath + "/" + serviceName;
            if (zk.exists(servicePath, false) == null) {
                zk.create(servicePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.info("create service node: {}", servicePath);
            }
            // 创建地址节点
            String addressPath = servicePath + "/address-";
            String addressNode = zk.create(addressPath, serviceAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("create address node: {} => {}", addressNode, serviceAddress);
        } catch (Exception e) {
            logger.error("create Znode failure", e);
        }
    }
}
