import React from 'react';
import {Base} from '../../index';
import {Item, Input} from 'components/UI';

class Online extends React.Component {

  render() {
    return (
      <Base type="online">
        <Base.Content>
          <Item label="Online Event" offset="small">
            <Input placeholder="Name"/>
          </Item>
        </Base.Content>
      </Base>
    );
  }

}

export default Online;
