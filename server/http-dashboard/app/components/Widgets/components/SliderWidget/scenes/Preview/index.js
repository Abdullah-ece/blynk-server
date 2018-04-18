import React from 'react';
import PropTypes from 'prop-types';
import {
  Map,
} from 'immutable';
import {Preview} from '../../components';

export default class PreviewScene extends React.Component {

  static propTypes = {

    data: PropTypes.object,

    loading: PropTypes.oneOfType([
      PropTypes.bool,
      PropTypes.object,
    ]),

    history: PropTypes.instanceOf(Map),

    deviceId: PropTypes.number,
  };

  render() {

    const data = {
      ...this.props.data,
      height: 1,
    };

    return (
      <Preview data={data}
               deviceId={this.props.deviceId}
               history = {this.props.history}
               loading={this.props.loading}/>
    );
  }

}
