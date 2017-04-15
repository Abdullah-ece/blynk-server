export function SimpleMatch(a, b) {
  return String(b).toLowerCase().indexOf(String(a).toLowerCase()) >= 0;
}

export function SelectSimpleMatch(a, b) {
  return SimpleMatch(a, b.props.children);
}
