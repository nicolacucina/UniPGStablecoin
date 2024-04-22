// SPDX-License-Identifier: MIT
pragma solidity >=0.4.22 <0.9.0;

contract PriceGenerator{

    /* Since floating point numbers are not supported in Solidity, 
    we have to use a fixed point number with 18 decimal places.
        1000000000000000000 = 1.0 ether(that for us is 1.0 euro)
    In javascript(truffle) this value will be returned as the array
        [56885248, 2993385, 222]
    which represents the number in intervals of 26 bits from the least significat (left) to the most significat (right).
    To prove that this is the case convert the three numbers in binary, extend them to 26 bits and concatenate them
        decimal => binary    
        56885248 => 11011001000000000000000000
        2993385 => 00001011011010110011101001
        222 => 00000000000000000011011110
    -----------------------------------------------------------------------------------------
        binary => decimal
        00000000000000000011011110 00001011011010110011101001 11011001000000000000000000 => 1e18
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