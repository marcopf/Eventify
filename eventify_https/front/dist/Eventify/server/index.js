const https = require('https');
const fs = require('fs');
const path = require('path')
const express = require('express');
const app = express();

const options = {
    key: fs.readFileSync('key.pem'),
    cert: fs.readFileSync('cert.pem'),
    passphrase: 'test',
};

app.use(express.static("./.."));

const PORT = process.env.PORT || 4200;

app.get("*", (req, res) => {
    res.sendFile(path.join(__dirname, "../index.html"));
});

const server = https.createServer(options, app);

server.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});