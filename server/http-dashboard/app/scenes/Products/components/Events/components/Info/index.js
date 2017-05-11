import React from 'react';
import Event from './../Event';
import {EVENT_TYPES} from 'services/Products';

class Info extends React.Component {

  static propTypes = {
    form: React.PropTypes.object,
    initialValues: React.PropTypes.object
  };

  render() {
    return (
      <Event type={EVENT_TYPES.INFO} form={this.props.form} initialValues={this.props.initialValues}/>
    );
  }

}

export default Info;
