# UniPGStablecoin

This repository contains the implementation of an algorithmic stablecoin that uses the `rebase` logic.

- _simulation/_: contains a simple simulation written in Java to show the basic logic behind the project
- _TruffleTest/_ : contains the Smart Contract and everything needed to use the Truffle suite to test and debug the code
- _public/_ : contains some of the resources used for the user interface
- _index.html_ : is the web page used for the user interface and interaction with the Smart Contract
- _start.bat_ : windows script to launch the server

## Dependecies

Example of the installation of the required dependencies using a Centos 7 VM:

    # Update packages
    sudo yum update

    # Install Java
    sudo yum install java
    java -version

    # Install javac
    sudo yum install java-devel
    javac -version

    # Install pip
    sudo yum install epel-release
    sudo yum install python-pip
    pip install --upgrade pip
    pip --version

    # Install matplotlib
    pip install matplotlib

    # Install Nodejs
    sudo yum install epel-release
    sudo yum install nodejs
    node --version

    # Install NPM
    sudo yum install npm
    npm --version

    # Install Truffle
    sudo npm install -g truffle
    sudo npm install -g ganache
    truffle --version
    ganache --version

    # Install Git
    sudo yum install git
    git --version

    # Clone repo
    git clone https://github.com/nicolacucina/UniPGStablecoin

    # Firewall rules
    sudo firewall-cmd --permanent --add-service=http
    sudo firewall-cmd --reload

    # Serve Web Page
    ## Setup Apache Web Server
    sudo yum install httpd
    sudo yum update httpd
    sudo cp /UniPGStablecoin/index.html /var/www/html/
    sudo cp -r /UniPGStablecoin/public/ /var/www/html/
    sudo cp /UniPGStablecoin/TruffleTest/build/contracts/PriceGenerator.json /var/www/html/public/data/
    sudo cp /UniPGStablecoin/TruffleTest/build/contracts/UniPGStablecoin.json /var/www/html/public/data/
    sudo systemctl start httpd

    ## OR Install Express
    npm install express
    npm express --version

    curl localhost

Windows 11

    winget source update

    # Install Java and JDK
    winget install ojdkbuild.openjdk.11.jdk
    java --version
    javac --version

    # Install python
    winget install python.python.3.12
    pip install --upgrade pip
    pip --version

    # Install matplotlib
    pip install matplotlib

    # Install Nodejs, NPM, Truffle, Ganache, Express
    winget install nodejs
    npm install -g npm
    npm install -g truffle
    npm install -g ganache
    npm install -g express

    # Install Git
    winget install git.git

    # Clone repository
    git clone https://github.com/nicolacucina/UniPGStablecoin