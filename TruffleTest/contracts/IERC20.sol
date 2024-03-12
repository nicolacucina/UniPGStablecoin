// SPDX-License-Identifier: MIT
pragma solidity ^0.8.21;

interface IERC20 {
    /* Keep in mind that there are no floats in Solidity. Therefore most tokens adopt 18 decimals
    and will return the total supply and other results as followed 1000000000000000000 for 1 token*/
    function totalSupply() external view returns (uint256);

    /*Returns the amount of tokens owned by an address (account). 
    This function is a getter and does not modify the state of the contract.*/    
    function balanceOf(address account) external view returns (uint256);

    /*The ERC-20 standard allows an address to give an allowance to another address to be able to retrieve tokens from it. 
    This getter returns the remaining number of tokens that the spender will be allowed to spend on behalf of owner. 
    This function is a getter and does not modify the state of the contract and should return 0 by default.*/    
    function allowance(address owner, address spender)
        external
        view
        returns (uint256);
    
    /*Moves the amount of tokens from the function caller address (msg.sender) to the recipient address.
    This function emits the Transfer event defined later. It returns true if the transfer was possible.*/
    function transfer(address recipient, uint256 amount)
        external
        returns (bool);

    /*Set the amount of allowance the spender is allowed to transfer from the function caller (msg.sender) balance. 
    This function emits the Approval event. The function returns whether the allowance was successfully set.*/
    function approve(address spender, uint256 amount) external returns (bool);
    
    /*Moves the amount of tokens from sender to recipient using the allowance mechanism. 
    amount is then deducted from the caller’s allowance. This function emits the Transfer event.*/
    function transferFrom(address sender, address recipient, uint256 amount)
        external
        returns (bool);
}
