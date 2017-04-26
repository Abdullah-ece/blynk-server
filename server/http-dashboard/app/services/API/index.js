export const applyTransformers = (transformers) => {
  return (data) => {
    return transformers.reduce((data, transformer) => {
      return JSON.stringify(transformer(JSON.parse(data)));
    }, JSON.stringify(data));
  };
};
