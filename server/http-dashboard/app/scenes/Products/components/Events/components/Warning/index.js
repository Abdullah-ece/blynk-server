import React from 'react';
import Event from './../Event';
import {EVENT_TYPES} from 'services/Products';
import Static from './static';
import PropTypes from 'prop-types';
import {Map} from 'immutable';

class Warning extends React.Component {

  static propTypes = {
    field: PropTypes.instanceOf(Map),
    onClone: PropTypes.func,
    onDelete: PropTypes.func
  };

  render() {
    return (
      <Event type={EVENT_TYPES.WARNING}
             field={this.props.field}
             onClone={this.props.onClone}
             onDelete={this.props.onDelete}
      />
    );
  }

}

Warning.Static = Static;
export default Warning;
