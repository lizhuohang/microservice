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
    // 获取服务路径
    var servicePath = REGISTRY_ROOT + "/" + serviceName;
    console.log('servicePath : %s', servicePath);
    zk.getChildren(servicePath, function (error, addressNodes) {
        if (error) {
            console.log(error.stack);
            res.end();
            return;
        }
        var size = addressNodes.length;
        if (size == 0) {
            console.log('address node is not exist');
            res.end();
            return;
        }

        // 生成地址路径
        var addressPath = servicePath + "/";
        if (size == 1) {
            // 若只有一个地址，则获取该地址
            addressPath += addressNodes[0];
        } else {
            // 若存在多个地址，随机选取一个
            addressPath += addressNodes[parseInt(Math.random() * size)];
        }
        console.log('addressPath : %s', addressPath);
        // 获取服务地址
        zk.getData(addressPath, function (error, serviceAddress) {
            if (!serviceAddress) {
                console.log('service address is not exist');
                res.end();
                return;
            }
            console.log('service address: %s', serviceAddress);
            proxy.web(req, res, {
                target: 'http://' + serviceAddress
            });
        });
    });
});

app.listen(PORT, function () {
    console.log('server is running at %d', PORT);
});