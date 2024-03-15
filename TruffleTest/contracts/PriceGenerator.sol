// SPDX-License-Identifier: MIT
pragma solidity >=0.4.22 <0.9.0;

contract PriceGenerator{

    /* Since floating point numbers are not supported in Solidity, 
    we have to use a fixed point number with 18 decimal places.
        1000000000000000000 = 1.0 euros
    */
    uint public price = 1 ether;  
    address public minter;

    constructor() {
        minter = msg.sender;
    }

    function setPrice(uint _price) public {
        require(msg.sender == minter, "Only the contract can set the price");
        price = _price;
    }
}