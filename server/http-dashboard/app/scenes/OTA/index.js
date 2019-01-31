export * from './scenes';

import React, { Component } from 'react';
import NoCampaigns from './scenes/NoCampaigns';

import './styles.less';


class OTA extends Component {
  render() {
    return (<NoCampaigns/>);
  }
}

export default OTA;
