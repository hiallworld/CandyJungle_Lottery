pragma solidity 0.5.7;

import "./CandyPrincipalPool.sol";
import "./LotteryDataBase.sol";


contract Lottery{
    address private managerAddr; // manager address
    address payable public  candyPrinPoolAddr; // candyRpincipalPool contract address
    
    string public current_NFP;  //current  Number of period
    uint256 public total_NFP; // total lottery number 
    mapping(string => address) public periodContractAddrList; //Number Contract List
    
    mapping(string => mapping(uint8 => bool)) public lotteryNumberList;
    
    uint256 private LOTTERY_CEILING ; // 0-9 lottery random number 10
    
    constructor(address mAddr,address payable CPPAddr,uint256 lotteryCeli) onlyContract(CPPAddr) public{
        managerAddr = mAddr;
        candyPrinPoolAddr = CPPAddr;
        LOTTERY_CEILING = lotteryCeli;
    }
    
    modifier onlyContract(address _addr){
        uint size;
        assembly { size := extcodesize(_addr) }
        require(size > 0,"") ;
        _;
    }
    
    modifier onlyManager(){
        require(msg.sender == managerAddr,"");
        _;
    }
    
    //create a new lottery Game to store all lottery data
    function createLotteryGame(
        string memory _periodNum,
        address _coinAddr,
        uint256 _sTime,
        uint256 _eTime
    )
        onlyManager
        public
    {
        require(periodContractAddrList[_periodNum] == address(0),""); // alread create
        total_NFP++;
        LotteryDataBase newGame = new LotteryDataBase(_periodNum,_coinAddr,_sTime,_eTime);
        current_NFP = _periodNum;
        periodContractAddrList[_periodNum] = address(newGame);
    }
    
    // can get a lot of lottery number from 1 to 9
    function createRandomLotteryNumber(string memory _periodNum,uint8 _times) onlyManager public {
        require(periodContractAddrList[_periodNum] != address(0),""); // not create 
        require(LotteryDataBase(periodContractAddrList[_periodNum]).getLotteryList().length == 0,"");
        require(_times < 9 && _times > 0,"");
        
        uint256 current_totalN; // all extract number msum
        bool current_isActive; // time is in active line 
        (,,current_totalN,current_isActive) = LotteryDataBase(periodContractAddrList[_periodNum]).getStoreInfo();
        require(current_isActive == false,""); // must end 
        
        uint8[] memory lList = new uint8[](_times);
        
        uint256 resultNumber = uint256(keccak256(abi.encodePacked(blockhash(block.number),current_totalN)));
        uint256 tamp1 = resultNumber;
        uint256 tamp2 = 0;
        uint256 moveBit = 1;
        for(uint8 i = 0; i < _times; i++){
            tamp2 = tamp1 % 10;
            for(uint8 j = 1; j < 8; j++){
               tamp1 = tamp1 / 10 ;
               tamp2 = tamp2 + (tamp1 % 10);
            }
            lList[i] = uint8(tamp2) % 10;
            
            tamp1 = resultNumber / (moveBit++ * 10);
            if(i > 0){
                //check is or not same
                if(lotteryNumberList[_periodNum][lList[i]]){
                    i--;
                }else{
                    lotteryNumberList[_periodNum][lList[i]] = true;
                }  
            }else{
                lotteryNumberList[_periodNum][lList[i]] = true;
            }
        }
        LotteryDataBase(periodContractAddrList[_periodNum]).setLotteryList(lList);
        
    }
    
    // storage the Extract number to contract 
    function setExtractList(string memory _periodNum,uint256[] memory _extNumList) public onlyManager{
        require(periodContractAddrList[_periodNum] != address(0),""); // not create 
        require(_extNumList.length <= 100, "");
        LotteryDataBase(periodContractAddrList[_periodNum]).addExtractList(_extNumList);
    }

    //get the Lottery number 
    function getLotteryList(string memory _periodNum) public view returns(uint8[] memory _lotteryList){
        require(periodContractAddrList[_periodNum] != address(0),""); // not create 
        _lotteryList = LotteryDataBase(periodContractAddrList[_periodNum]).getLotteryList();
    }
}