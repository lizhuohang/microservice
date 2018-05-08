var express = require('express');
var zookeeper = require('node-zookeeper-client');
var httpProxy = require('http-proxy');

var PORT = 1234;

var CONNECTION_STRING = '127.0.0.1:2181';
var REGISTRY_ROOT = '/registry';

// 可用服务地址缓存
var cache = {};

// 链接 zookeeper
var zk = zookeeper.createClient(CONNECTION_STRING);
zk.connect();

// 创建代理服务器对象并监听错误事件
var proxy = httpProxy.createProxyServer();
proxy.on('error', function (error, req, res) {
    res.end();
});

var app = express();
// app.use(express.static('public'));
app.all('*', function (req, res, next) {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Headers', 'Content-Type, Content-Length, Authorization, Accept, X-Requested-With , yourHeaderFeild , Service-Name');
    res.header('Access-Control-Allow-Methods', 'PUT, POST, GET, DELETE, OPTIONS');
    res.header("X-Powered-By", ' 3.2.1');
    if (req.method == 'OPTIONS') {
        res.send(200);
    } else {
        next();
    }
});

app.use(function (req, res, next) {
    // 处理图标请求
    if (req.path == '/favicon.ico') {
        res.end();
        return;
    }
    // 获取服务名称
    var serviceName = req.get('Service-Name');
    console.log('service name : %s', serviceName);
    if (!serviceName) {
        console.log('Service-Name request header is not exist!');
        res.end();
        return;
    }

    main(serviceName, req, res);

    // 获取服务路径
    // var servicePath = REGISTRY_ROOT + "/" + serviceName;
    // console.log('servicePath : %s', servicePath);
    //
    // zk.getChildren(servicePath, function (error, addressNodes) {
    //     if (error) {
    //         console.log(error.stack);
    //         res.end();
    //         return;
    //     }
    //     var size = addressNodes.length;
    //     if (size == 0) {
    //         console.log('address node is not exist');
    //         res.end();
    //         return;
    //     }
    //
    //     // 生成地址路径
    //     var addressPath = servicePath + "/";
    //     if (size == 1) {
    //         // 若只有一个地址，则获取该地址
    //         addressPath += addressNodes[0];
    //     } else {
    //         // 若存在多个地址，随机选取一个
    //         addressPath += addressNodes[parseInt(Math.random() * size)];
    //     }
    //     console.log('addressPath : %s', addressPath);
    //     // 获取服务地址
    //     zk.getData(addressPath, function (error, serviceAddress) {
    //         if (!serviceAddress) {
    //             console.log('service address is not exist');
    //             res.end();
    //             return;
    //         }
    //         console.log('service address: %s', serviceAddress);
    //         proxy.web(req, res, {
    //             target: 'http://' + serviceAddress
    //         });
    //     });
    // });
});

app.listen(PORT, function () {
    console.log('server is running at %d', PORT);
});

// 获取服务地址的主要功能实现
function main(serviceName, req, res) {
    var addressList = cache[serviceName];
    // 缓存中存在serviceName对应的地址信息
    if (addressList) {
        var serviceAddress;
        var addressSize = addressList.length;
        if (addressSize == 1) {
            // 若只有一个地址，则获取该地址
            serviceAddress = addressList[0];
        } else {
            // 若存在多个地址，随机选取一个
            serviceAddress = addressList[parseInt(Math.random() * addressSize)];
        }
        doProxy(serviceAddress, req, res);
        return;
    }

    // 缓存中不存在serviceName对应的地址信息
    refreshInfo(serviceName, req, res);
}

// 通过serviceName获取到servicePath
function getPath(serviceName) {
    return REGISTRY_ROOT + "/" + serviceName;
}

// 通过servicePath获取到serviceName
function getServiceName(servicePath) {
    return servicePath.substring(servicePath.lastIndexOf("/") + 1, servicePath.length);
}

// 统一代理功能
function doProxy(serviceAddress, req, res) {
    console.log('proxy service address: %s', serviceAddress + "/" + req.originalUrl);
    proxy.web(req, res, {
        target: 'http://' + serviceAddress
    });
}

// zookeeper路径下子节点变动监听器
function childrenChange(event) {
    var servicePath = event.path;
    var serviceName = getServiceName(servicePath);

    refreshInfo(serviceName);

}

// 从zk上获取信息的逻辑实现
function refreshInfo(serviceName, req, res) {
    cache[serviceName] = [];
    var servicePath = getPath(serviceName)
    console.log('zookeeper servicePath : %s', servicePath);
    zk.getChildren(servicePath, childrenChange, function (error, addressNodes) {
        if (error) {
            console.log(error.stack);
            if (res) {
                res.end();
            }
            return;
        }
        var size = addressNodes.length;
        if (size == 0) {
            console.log('address node is not exist');
            if (res) {
                res.end();
            }
            return;
        }

        var hasProxy = false;
        for (var i = 0; i < size; i++) {
            var addressPath = servicePath + "/" + addressNodes[i];
            console.log('addressPath : %s', addressPath);
            // 获取服务地址
            zk.getData(addressPath, function (error, serviceAddress) {
                if (!serviceAddress) {
                    console.log('service address is not exist');
                    return;
                }
                console.log('service address: %s', serviceAddress);
                cache[serviceName].push(serviceAddress);
                console.log("cache[%s] : ", serviceName);
                console.log(cache[serviceName]);
                if (res && !hasProxy) {
                    hasProxy = true;
                    doProxy(serviceAddress, req, res);
                }
            });
        }
    });
}