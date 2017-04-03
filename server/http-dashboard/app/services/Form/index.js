export const transformJsonToFormUrlEncoded = (data) => {
  const str = [];
  for (let p in data)
    str.push(encodeURIComponent(p) + "=" + encodeURIComponent(data[p]));
  return str.join("&");
};
