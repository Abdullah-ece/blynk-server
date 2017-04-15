export function alphabetSort(a, b) {

  a = String(a).toLowerCase();
  b = String(b).toLowerCase();

  if (a < b) return -1;
  if (a > b) return 1;
  return 0;
}
