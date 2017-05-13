import React from 'react';
import Event from './../Event';
import {EVENT_TYPES} from 'services/Products';

class Static extends React.Component {

  static propTypes = {
    fields: React.PropTypes.object
  };

  render() {
    return (
      <Event.Static type={EVENT_TYPES.CRITICAL} fields={this.props.fields}/>
    );
  }

}

export default Static;
