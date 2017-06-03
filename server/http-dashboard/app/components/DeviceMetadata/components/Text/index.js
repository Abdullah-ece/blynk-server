import React from 'react';
import {Fieldset, DeviceMetadata} from 'components';

class Text extends React.Component {

  render() {
    return (
      <DeviceMetadata.Item>
        <Fieldset>
          <Fieldset.Legend type="dark">Device Owner</Fieldset.Legend>
          ihor.bra@gmail.com<br/>
          ihor.bra@gmail.com<br/>
          ihor.bra@gmail.com
        </Fieldset>
      </DeviceMetadata.Item>
    );
  }

}

export default Text;
