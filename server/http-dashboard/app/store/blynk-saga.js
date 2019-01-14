// import { LOCATION_CHANGE } from 'react-router-redux';

import { eventChannel } from 'redux-saga';
import { put, take, call, fork } from 'redux-saga/effects';

export const INIT_ACTION_TYPE = 'CONNECT';

let socket_reconnect_retry = 0;
const MAX_SOCKET_RETRY = 5;

/* eslint-disable no-constant-condition */

/* eslint-disable require-yield */
let socket;
let endpoint;

function connect() {
  return new Promise((resolve, reject) => {
    try {
      socket = new WebSocket(endpoint);
      socket.addEventListener('open', () => {
        resolve(socket);
      });
      socket.addEventListener('error', (err) => {
        reject(err);
      });
    } catch (err) {
      reject(err);
    }
  });
}

function socketSubscribe(emitter) {
  socket.addEventListener('message', message => {
    const object = JSON.parse(message.data);
    switch (object.type) {
      default:
        break;
    }
  });

  socket.addEventListener('close', () => {
    reconnect().then(() => {
      socketSubscribe(emitter);
    });
  });
}

function reconnect() {
  return new Promise((resolve, reject) => {
    retry(resolve, reject).then((sock) => {
      resolve(sock);
    });
  });
}

function retry(resolve, reject) {
  return new Promise(() => {
    setTimeout(() => {
      connect().then((sock) => {
        socket_reconnect_retry = 0;
        resolve(sock);
      }).catch((err) => {
        if (socket_reconnect_retry < MAX_SOCKET_RETRY) {
          socket_reconnect_retry += 1;
          retry(resolve, reject);
        } else {
          reject(err);
        }
      });
    }, 5000);
  });
}

function* subscribeToSocketEventChannel() {
  return eventChannel(emitter => {
    socketSubscribe(emitter);

    return () => {};
  });
}

function* handleInput() {
  while (true) {
    const action = yield take('*');
    console.log('AAAAAAAAAAAAAAAAAAAAAAAAAAAA',action)
    // if (action.type === LOCATION_CHANGE) {
    //   socket.send(JSON.stringify(action));
    // } else {
    //   socket.send(JSON.stringify(action));
    // }
  }
}

function* handleOutput() {
  const socketEventChannel = yield call(subscribeToSocketEventChannel, socket);
  while (true) {
    const message = yield take(socketEventChannel);
    yield put(message);
  }
}

function* handleSocket() {
  yield fork(handleOutput);
  yield fork(handleInput);
}

export function* blynkSaga() {
  while (true) {
    const result = yield take(INIT_ACTION_TYPE);
    endpoint = result.endpoint;
    yield call(connect);
    yield fork(handleSocket);
  }
}
