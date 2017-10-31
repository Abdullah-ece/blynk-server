export const addThousandsSeparatorForNumber = (number = 0) => {
  const splitedNumber = number.toString().split('.');

  if(splitedNumber[0].length > 3) {
    splitedNumber[0] = (Number(splitedNumber[0]) / 1000).toFixed(3);
    splitedNumber[0] = splitedNumber[0].toString().split('.').join(',');
  }

  return splitedNumber.join('.');
};
