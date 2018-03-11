import React from 'react';
import {
  Index
} from './scenes';
import _ from 'lodash';

import PropTypes from 'prop-types';

class Devices extends React.Component {

  static propTypes = {
    params: PropTypes.object,
    location: PropTypes.object,
  };

  shouldComponentUpdate(nextProps) {
    return (
      !_.isEqual(nextProps.params, this.props.params) ||
      !_.isEqual(nextProps.location, this.props.location)
    );
  }

  render() {

    const { params, location } = this.props;

    return (
      <Index params={params} location={location}/>
    );
  }

}

export default Devices;
