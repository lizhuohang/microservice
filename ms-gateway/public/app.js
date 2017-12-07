var express = require('express');

var PORT = 1234;

var app = express();
app.use(express.static('public'));
app.all('*', function (req, res, next) {

    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Headers', 'Content-Type, Content-Length, Authorization, Accept, X-Requested-With , yourHeaderFeild , Service-Name');
    res.header('Access-Control-Allow-Methods', 'PUT, POST, GET, DELETE, OPTIONS');
    res.header("X-Powered-By", ' 3.2.1');
    if (req.method == 'OPTIONS') {
        res.send(200);
        return;
    } else {
        next();
    }

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
    // TODO 代理
});

app.listen(PORT, function () {
    console.log('server is running at %d', PORT);
});
