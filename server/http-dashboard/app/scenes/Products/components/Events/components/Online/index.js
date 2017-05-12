import React from 'react';
import {Base} from '../../index';
import {Item, Input} from 'components/UI';
import {EVENT_TYPES} from 'services/Products';

class Online extends React.Component {

  static propTypes = {
    form: React.PropTypes.string,
    initialValues: React.PropTypes.object,
    onChange: React.PropTypes.func,
    onDelete: React.PropTypes.func,
    validate: React.PropTypes.func
  };

  render() {
    return (
      <Base type={EVENT_TYPES.ONLINE} form={this.props.form} initialValues={this.props.initialValues}
            onChange={this.props.onChange}
            validate={this.props.validate}
            onDelete={this.props.onDelete}>
        <Base.Content>
          <Item label="Online Event" offset="small">
            <Input name="name" placeholder="Event Name"/>
          </Item>
        </Base.Content>
      </Base>
    );
  }

}

export default Online;
