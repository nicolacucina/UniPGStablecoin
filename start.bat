# Opens a new command prompt and starts ganache-cli with a mnemonic
start ganache-cli -m 'gippa' --wallet.accountKeysPath=./public/data/keys.json
cd TruffleTest
truffle migrate
cd ..
# open html page using live server on VSCode for now
# node server.js
# start firefox localhost:5500
