pragma solidity 0.5.7;

contract LotteryDataBase{
    address private owner;
    string private periodNumber; // this only flage
    uint256 public participantsNumber; // participants Number
    uint256 public totalNumber; // all extract number add  
    address private coinAddress; // TERC20 contract coin 
    uint256 public startTime;// the game start time 
    uint256 public endTime; // the game end time 
    uint8[] public lotteryList;  // lottery List 
    
    mapping(uint256 => uint256) public extractNumber;// ectract number 
    
    constructor(string memory _pNumb,address _coinAddr,uint256 _sTime,uint256 _eTime) public {
        require(_sTime < _eTime,"");
        owner = msg.sender;
        periodNumber = _pNumb;
        coinAddress = _coinAddr;
        startTime = _sTime;
        endTime = _eTime;
    }
    
    modifier onlyOwner(){
        require(msg.sender == owner,"");
        _;
    }
    
    //add extract number 
    function addExtractList(uint256[] calldata _extNum) external onlyOwner{
        require(now <= endTime && now >= startTime,"");
        require(_extNum.length <= 100,"");
        for(uint8 i=0; i < _extNum.length; i++){
           addExtractNumber(_extNum[i]); 
        }
    }
    
    // true is Alread in , false not exit
    function isAlreadIn(uint256 _extNum) external view onlyOwner returns(bool){ extractNumber[_extNum] > 0;}
    
    function getStoreInfo() external view returns(uint256 _totalP, address _cAddr, uint256 _totalN, bool isActive){
        _totalP  = participantsNumber;
        _cAddr = coinAddress;
        _totalN = totalNumber;
        if (now <= endTime && now >= startTime){
            isActive = true;
        }else{ 
            isActive = false;
        }
    }
    
    //storage Lottery number 
    function setLotteryList(uint8[] calldata _lList) external onlyOwner{
        require(_lList.length < 9 && _lList.length > 0,"");
        lotteryList = _lList;
    }
    
    function getLotteryList() public view returns(uint8[] memory _lList){
        _lList = lotteryList;
    } 
    
    function addExtractNumber(uint256 _extNum) internal {
        require(now <= endTime,"");
        participantsNumber++;
        totalNumber = totalNumber + _extNum;//Allow crossing

        extractNumber[_extNum] = participantsNumber;
    }
}
