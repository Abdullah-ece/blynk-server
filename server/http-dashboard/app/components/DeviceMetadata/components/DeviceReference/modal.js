import React from 'react';
import {ItemsGroup, Item} from 'components/UI';
import {MetadataSelect} from 'components/Form';
import PropTypes from 'prop-types';

class DeviceReference extends React.Component {

  static propTypes = {
    options: PropTypes.oneOfType([
      PropTypes.object,
      PropTypes.array,
    ])
  };

  render() {
    return (
      <div>
        <ItemsGroup>
          <Item label="Value">
            <MetadataSelect name="value" type="text" placeholder="Choose Device" values={this.props.options} style={{width: '100%'}}/>
          </Item>
        </ItemsGroup>
      </div>
    );
  }

}

export default DeviceReference;
