import React from 'react';
import Event from './../Event';
import {EVENT_TYPES} from 'services/Products';
import Static from './static';

class Warning extends React.Component {

  static propTypes = {
    form: React.PropTypes.string,
    initialValues: React.PropTypes.object,
    onChange: React.PropTypes.func,
    onClone: React.PropTypes.func,
    onDelete: React.PropTypes.func,
    validate: React.PropTypes.func
  };

  render() {
    return (
      <Event type={EVENT_TYPES.WARNING} form={this.props.form} initialValues={this.props.initialValues}
             onChange={this.props.onChange}
             onClone={this.props.onClone}
             validate={this.props.validate}
             onDelete={this.props.onDelete}/>
    );
  }

}

Warning.Static = Static;
export default Warning;
