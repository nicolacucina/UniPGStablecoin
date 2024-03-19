
var express = require('express');
var app = express();
var port = 5500;

app.use(express.static(__dirname + '/'));

app.listen(port, function () {
    console.log('Server is running on port ' + port);
})

app.get('/public/js/coinLineChart.js', function(req, res) {
    res.sendFile(__dirname + '/public/js/coinLineChart.js');
});

app.get('/public/js/tokenLineChart.js', function(req, res) {
    res.sendFile(__dirname + '/public/js/tokenLineChart.js');
});

app.get('/public/js/main.js', function(req, res) {
    res.sendFile(__dirname + '/public/js/main.js');
});

app.get('/', (req, res) =>{
    res.sendFile(__dirname + '/index.html')
})