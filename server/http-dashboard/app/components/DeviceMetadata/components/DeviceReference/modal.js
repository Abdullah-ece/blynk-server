import React from 'react';
import { ItemsGroup, Item } from 'components/UI';
import { MetadataSelect } from 'components/Form';
import PropTypes from 'prop-types';

class DeviceReference extends React.Component {

  static propTypes = {
    options: PropTypes.oneOfType([
      PropTypes.object,
      PropTypes.array,
    ]),
    metafieldId: PropTypes.number,
    deviceId: PropTypes.number,
    getDeviceByReferenceMetafield: PropTypes.func,
  };

  state = {
    options: []
  };

  componentDidMount() {
    const { deviceId, metafieldId } = this.props;
    this.props.getDeviceByReferenceMetafield({ deviceId, metafieldId })
      .then(
        (data) => {
          this.setState({
            options: data.payload.data.map((device) => ({
              key: String(device.id),
              value: String(device.name)
            }))
          });
        }).catch(err => console.errord(err));
  }

  render() {
    const notFoundContent = Object.keys(this.props.options).length > 0 ? 'No match' : 'No Devices';

    return (
      <div>
        <ItemsGroup>
          <Item label="Value">
            <MetadataSelect notFoundContent={notFoundContent} allowZero={false}
                            name="selectedDeviceId" type="text"
                            placeholder="Choose Device"
                            values={this.state.options}
                            style={{ width: '100%' }}/>
          </Item>
        </ItemsGroup>
      </div>
    );
  }

}

export default DeviceReference;
