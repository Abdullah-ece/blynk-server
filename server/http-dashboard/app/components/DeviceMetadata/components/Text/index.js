import React from 'react';
import {Fieldset, DeviceMetadata} from 'components';

class Text extends React.Component {

  static propTypes = {
    data: React.PropTypes.object
  };

  render() {

    const field = this.props.data;

    return (
      <DeviceMetadata.Item>
        <Fieldset>
          <Fieldset.Legend type="dark">{field.name}</Fieldset.Legend>
          {field.value}
        </Fieldset>
      </DeviceMetadata.Item>
    );
  }

}

export default Text;
