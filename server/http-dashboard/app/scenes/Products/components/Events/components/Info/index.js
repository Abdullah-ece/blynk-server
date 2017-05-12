import React from 'react';
import Event from './../Event';
import {EVENT_TYPES} from 'services/Products';

class Info extends React.Component {

  static propTypes = {
    form: React.PropTypes.string,
    initialValues: React.PropTypes.object,
    onChange: React.PropTypes.func,
    onClone: React.PropTypes.func,
    onDelete: React.PropTypes.func
  };

  render() {
    return (
      <Event type={EVENT_TYPES.INFO} form={this.props.form} initialValues={this.props.initialValues}
             onChange={this.props.onChange}
             onClone={this.props.onClone}
             onDelete={this.props.onDelete}/>
    );
  }

}

export default Info;
