REM Opens a new command prompt and starts ganache-cli with a mnemonic
start ganache-cli -m 'gippa' --wallet.accountKeysPath=./public/data/keys.json

REM Serve the html page
start node server.js

REM Deploy contracts
cd TruffleTest
start truffle migrate