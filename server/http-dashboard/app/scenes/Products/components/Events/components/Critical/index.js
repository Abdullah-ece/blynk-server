import React from 'react';
import Event from './../Event';
import {EVENT_TYPES} from 'services/Products';
import Static from './static';
import PropTypes from 'prop-types';
import {Map} from 'immutable';

class Critical extends React.Component {

  static propTypes = {
    field: PropTypes.instanceOf(Map),
    onClone: PropTypes.func,
    onDelete: PropTypes.func
  };

  render() {
    return (
      <Event type={EVENT_TYPES.CRITICAL}
             field={this.props.field}
             onClone={this.props.onClone}
             onDelete={this.props.onDelete}
      />
    );
  }

}

Critical.Static = Static;
export default Critical;
