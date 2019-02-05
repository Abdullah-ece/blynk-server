export * from './scenes';

import React, { Component } from 'react';
import Index from './scenes/Index';

import './styles.less';


class OTA extends Component {
  render() {
    return (<Index/>);
  }
}

export default OTA;
