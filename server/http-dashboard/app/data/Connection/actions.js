export function Connect() {
  return {
    type: 'CONNECTION_CONNECT'
  };
}

export function ConnectSuccess() {
  return {
    type: 'CONNECTION_CONNECT_SUCCESS'
  };
}

export function ConnectFailed() {
  return {
    type: 'CONNECTION_CONNECT_FAILED'
  };
}

export function ConnectionInterrupted() {
  return {
    type: 'CONNECTION_CONNECTION_INTERRUPTED'
  };
}
