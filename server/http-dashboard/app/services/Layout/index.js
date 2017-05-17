export const getChildrenByType = (type, children = []) => {
  if (!children) return null;

  let element;
  if (Array.isArray(children)) {
    element = children.filter((child) => !!getChildrenByType(type, child));
  } else if (children.type && children.type.displayName === type) {
    return children;
  }

  return element || null;
};
