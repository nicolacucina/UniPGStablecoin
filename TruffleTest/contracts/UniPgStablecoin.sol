// SPDX-License-Identifier: MIT
pragma solidity >=0.4.22 <0.9.0;

import "./IERC20.sol";
import "./PriceGenerator.sol";

contract UniPGStablecoin is IERC20 {
    /* This event is emitted when the amount of tokens (value) is sent from the from address to the to address.
    In the case of minting new tokens, the transfer is usually from the 0x00..0000 address while in the case of burning tokens the transfer is to 0x00..0000.*/
    event Transfer(address indexed from, address indexed to, uint256 value);
    
    /*This event is emitted when the amount of tokens (value) is approved by the owner to be used by the spender.*/
    event Approval(
        address indexed owner, address indexed spender, uint256 value
    );

    /*
    ERC20InsufficientAllowance(spender, allowance, needed)

    ERC20InvalidApprover(approver)

    ERC20InvalidSpender(spender)

    error RebaseInProgress();

    error InitialMintingOver();

    error InvalidSender(address sender);

    error InvalidReceiver(address receiver);*/

    error InsufficientBalance(uint requested, uint available);

    PriceGenerator public priceGen;
    
    string public name;
    string public symbol;
    uint8 public decimals;
    uint256 public totalSupply;
    bool public isRebasing;
    bool public initialMinting;
    
    address public minter;
    mapping(address => uint256) public balanceOf;
    address[] public walletAddresses;
    mapping(address => mapping(address => uint256)) public allowance;
     
    constructor(string memory _name, string memory _symbol, uint8 _decimals, PriceGenerator _priceGenerator) {
        name = _name;
        symbol = _symbol;
        decimals = _decimals;
        priceGen = _priceGenerator;
        minter = msg.sender;
        isRebasing = false;
        initialMinting = true;
    }
        
    /* this is used only in the inital phase of the contract where tokens are given to initial backers.*/
    function mint(address to, uint256 amount) external {
        require(msg.sender == minter, "Only the contract can mint tokens");
        require(initialMinting == true, "Initial minting is over");
        walletAddresses.push(to);
        _mint(to, amount);
    }

    function blockMinting() external {
        require(msg.sender == minter, "Only the contract can block minting");
        require(initialMinting == true, "Initial minting is over");
        initialMinting = false;
    }

    function _mint(address to, uint256 amount) internal {
        balanceOf[to] += amount;
        totalSupply += amount;
        emit Transfer(address(this), to, amount);
    }

    function _burn(address from, uint256 amount) internal {
        balanceOf[from] -= amount;
        totalSupply -= amount;
        emit Transfer(from, address(this), amount);
    }

    /* this is used only in testing, the internal method should be called periodically each day */
    function rebase() external {
        require(msg.sender == minter, "Only the contract can rebase");
        _rebase();
    }

    function _rebase() internal {
        isRebasing = true;
        uint price = priceGen.price();
        
        /*for loops are not defined for mappings, the workaround is that when a key is added to a mapping,
        it also added to an array */
        for(uint i = 0; i < walletAddresses.length; i++) {
            /* fastest method, but using mint and burn triggers Transfer event
            balanceOf[account] = balanceOf[account] * price; */
            uint256 temp = balanceOf[walletAddresses[i]];
            uint256 diff = temp - (temp*price/1 ether); 
            if(price > 1e18){
                _mint(walletAddresses[i], diff);
            }else if(price < 1e18){
                _burn(walletAddresses[i], diff);
            }else{
                // do nothing
            }
        }
        isRebasing = false;
    }

    function transfer(address recipient, uint256 amount)
        external
        returns (bool)
    {   
        require(isRebasing == false);
        if (amount > balanceOf[msg.sender]){
            revert InsufficientBalance({
                requested: amount,
                available: balanceOf[msg.sender]
            });
        }                
        balanceOf[msg.sender] -= amount;
        balanceOf[recipient] += amount;
        walletAddresses.push(recipient);
        emit Transfer(msg.sender, recipient, amount);
        return true;        
    }

    function approve(address spender, uint256 amount) external returns (bool) {
        allowance[msg.sender][spender] = amount;
        emit Approval(msg.sender, spender, amount);
        return true;
    }

    function transferFrom(address sender, address recipient, uint256 amount)
        external
        returns (bool)
    {
        if(isRebasing){
            return false; 
        }else{
            if (amount > balanceOf[msg.sender]){
                revert InsufficientBalance({
                    requested: amount,
                    available: balanceOf[msg.sender]
                });
            }
            allowance[sender][msg.sender] -= amount;
            balanceOf[sender] -= amount;
            balanceOf[recipient] += amount;
            emit Transfer(sender, recipient, amount);
            return true;
        }
    }
}