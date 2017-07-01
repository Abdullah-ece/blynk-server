export const getOptionByAmount = (amount, texts) => {
  /*
   * The First argument is an amount
   * The second argument is options of texts like ['%s Apple','%s Apples'] */
  if (isNaN(Number(amount)))
    throw new Error('First argument should be a number');

  if (!Array.isArray(texts) || texts.length !== 2)
    throw new Error('Second argument should be an array and has two options of text');

  if (Number(amount) === 1)
    return String(texts[0]).replace('%s', amount);

  return String(texts[1]).replace('%s', amount);
};
