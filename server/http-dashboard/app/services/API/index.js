export const applyRequestTransformers = (transformers) => {
  return (data) => {
    return transformers.reduce((data, transformer) => {
      return JSON.stringify(transformer(JSON.parse(data)));
    }, JSON.stringify(data));
  };
};

export const applyResponseTransformers = (transformers) => {
  return (data) => {
    return transformers.reduce((data, transformer) => {
      return transformer(data);
    }, JSON.parse(data));
  };
};

