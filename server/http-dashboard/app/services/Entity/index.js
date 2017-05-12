export const getNextId = (list) => {
  return list.reduce((acc, value) => (
      acc < value.id ? value.id : acc
    ), list.length ? list[0].id : 0) + 1;
};
