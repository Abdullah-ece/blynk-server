import React from 'react';
import {Row, Col} from 'antd';
import {Fieldset, DeviceStatus, DeviceAuthToken, Section, DeviceMetadata} from 'components';
import _ from 'lodash';
import './styles.less';

class DeviceInfo extends React.Component {

  static propTypes = {
    device: React.PropTypes.object
  };

  shouldComponentUpdate(nextProps) {
    return !(_.isEqual(nextProps.device, this.props.device));
  }

  getDeviceStatus() {
    if (this.props.device && this.props.device.status === 'OFFLINE') {
      return 'offline';
    } else if (this.props.device && this.props.device.status === 'ONLINE') {
      return 'online';
    }
  }

  render() {

    const metaFields = [
      {
        type: "Text",
        name: "Device Name",
        role: "ADMIN",
        value: "My Device 0"
      },
      {
        type: "Text",
        name: "Device Owner",
        role: "ADMIN",
        value: "ihor.bra@gmail.com"
      },
      {
        type: "Text",
        name: "Location Name",
        role: "ADMIN",
        value: "Trenton New York Farm"
      }
    ];

    return (
      <div className="device--device-info">
        <Row className="device--device-info-details">
          <Col span={8}>
            <Fieldset>
              <Fieldset.Legend>Status</Fieldset.Legend>
              <DeviceStatus status={this.getDeviceStatus()}/>
            </Fieldset>
            <Fieldset>
              <Fieldset.Legend>Auth Token</Fieldset.Legend>
              <DeviceAuthToken authToken={this.props.device.token}/>
            </Fieldset>
          </Col>
          <Col span={8}>
            <Fieldset>
              <Fieldset.Legend>Last Reported</Fieldset.Legend>
              Today, 12:35 AM
            </Fieldset>
            <Fieldset>
              <Fieldset.Legend>Organization</Fieldset.Legend>
              Blynk
            </Fieldset>
          </Col>
          <Col span={8}>
            <div className="device--device-info-logo">
              <img src="http://www.knightequip.com/images/product_warewash_nav/ump-hospitality.jpg"/>
            </div>
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            <Section title="Metadata">
              <div className="device--device-info-metadata-list">
                {
                  metaFields.map((field, key) => {
                    if (field.type === 'Text')
                      return (<DeviceMetadata.Text data={field} key={key}/>);
                  })
                }
              </div>
            </Section>
          </Col>
        </Row>
      </div>
    );
  }

}

export default DeviceInfo;
