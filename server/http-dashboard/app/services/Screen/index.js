export const getOffset = (elem, type) => {
  let offsetLeft = 0;

  let offsetType = type === 'left' ? 'offsetLeft' : 'offsetTop';

  do {
    elem = elem.offsetParent;
    if (!isNaN(elem[offsetType])) {
      offsetLeft += elem[offsetType];
    }
  } while (elem.offsetParent);
  return offsetLeft;
};
