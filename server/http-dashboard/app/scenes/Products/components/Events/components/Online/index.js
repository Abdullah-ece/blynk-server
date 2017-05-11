import React from 'react';
import {Base} from '../../index';
import {Item, Input} from 'components/UI';
import {EVENT_TYPES} from 'services/Products';

class Online extends React.Component {

  static propTypes = {
    form: React.PropTypes.object,
    initialValues: React.PropTypes.object
  };

  render() {
    return (
      <Base type={EVENT_TYPES.ONLINE} form={this.props.form} initialValues={this.props.initialValues}>
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
