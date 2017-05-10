import React from 'react';
import {Base} from '../../index';

class Online extends React.Component {

  render() {
    return (
      <Base type="online">
        <Base.Preview>
          Preview is there
        </Base.Preview>
        <Base.Content>
          Content is there
        </Base.Content>
      </Base>
    )
  }

}

export default Online;
