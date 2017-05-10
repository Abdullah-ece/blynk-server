import React from 'react';
import {Base} from '../../index';
import {Item, Input} from 'components/UI';
import {EVENT_TYPES} from 'services/Products';

class Online extends React.Component {

  render() {
    return (
      <Base type={EVENT_TYPES.ONLINE} form={this.props.form}>
        <Base.Content>
          <Item label="Online Event" offset="small">
            <Input placeholder="Event Name"/>
          </Item>
        </Base.Content>
      </Base>
    );
  }

}

export default Online;
