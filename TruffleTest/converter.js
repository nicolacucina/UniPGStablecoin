/*
    This function implements the convertion logic explained in the PriceGenerator.sol file.
*/
function converter(numbers){
    var result = '';
    for(var i = numbers.length -1; i >= 0; i--){
        var temp = Number(numbers[i]).toString(2);
        console.log(temp);
        while(temp.length < 26){
            temp = '0' + temp;
        }
        console.log(temp);
        result += temp;
    }
    console.log(result);
    return parseInt(result, 2);
}
/* Inline version to be used in the console
let myfunction = (numbers)=>{var result = '';for(var i = numbers.length -1; i >= 0; i--){var temp = Number(numbers[i]).toString(2);console.log(temp);while(temp.length < 26){temp = '0' + temp;}console.log(temp);result += temp;}console.log(result);return parseInt(result, 2);};
*/