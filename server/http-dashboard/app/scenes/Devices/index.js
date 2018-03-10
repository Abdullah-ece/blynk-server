import React from 'react';
import {
  Index
} from './scenes';

import PropTypes from 'prop-types';

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
