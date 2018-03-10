import React from 'react';
import {
  Index
} from './scenes';

import PropTypes from 'prop-types';

import wdu from 'why-did-you-update';

wdu(React, {
  groupByComponent: true
});

class Devices extends React.Component {

  static propTypes = {
    params: PropTypes.object,
    location: PropTypes.object,
  };

  render() {

    const { params, location } = this.props;

    return (
      <Index params={params} location={location}/>
    );
  }

}

export default Devices;
