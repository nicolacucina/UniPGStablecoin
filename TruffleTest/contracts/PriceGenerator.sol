// SPDX-License-Identifier: MIT
pragma solidity >=0.4.22 <0.9.0;

contract PriceGenerator{

    uint256 public price = 1.0;
    address public minter;

    constructor() {
        minter = msg.sender;
    }

    function setPrice(uint256 _price) public {
        require(msg.sender == minter, "Only the contract can set the price");
        price = _price;
    }
}