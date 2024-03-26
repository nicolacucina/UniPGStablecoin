var express = require('express');
var app = express();
var port = 5500;

app.use(express.static(__dirname + '/'));

app.listen(port, function () {
    console.log('Server is running on port ' + port);
})

app.get('/', (req, res) =>{
    res.sendFile(__dirname + '/index.html')
})