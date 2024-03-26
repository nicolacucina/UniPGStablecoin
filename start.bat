# Opens a new command prompt and starts ganache-cli with a mnemonic
start ganache-cli -m 'gippa' --wallet.accountKeysPath=./public/data/keys.json
# serve the html page
node server.js
cd TruffleTest
truffle migrate
start firefox localhost:5500

