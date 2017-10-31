export const addThousandsSeparatorForNumber = (num) =>{
  if(typeof num === "undefined") throw new Error("Argument is missed");
  if(isNaN(Number(num))) throw new Error("Argument should be a number");

  return Number(num).toLocaleString();
};
