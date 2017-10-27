export const addThousandsSeparatorForNumber = (number) => {
  if (number === undefined) {
    return undefined;
  }

  number = number / 1000;

  // If input number is like 123.123 the result of dividing will be 0.123123, to avoid
  // such output we are checking the number of digits after decimal point and round it if need.
  return countDigitsAfterDecimalPoint(number) > 4 ? number.toFixed(4) : number;
};

const countDigitsAfterDecimalPoint = (number) => {
  if (Math.floor(number) === number) return 0;

  return number.toString().split(".")[1].length || 0;
};
