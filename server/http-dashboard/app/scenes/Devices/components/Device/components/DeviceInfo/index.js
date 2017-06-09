import React from 'react';
import {Row, Col} from 'antd';
import {Fieldset, DeviceStatus, DeviceAuthToken, Section, DeviceMetadata} from 'components';
import {Metadata} from 'services/Products';
import _ from 'lodash';
import './styles.less';

class DeviceInfo extends React.Component {

  static propTypes = {
    device: React.PropTypes.object,
    onChange: React.PropTypes.func
  };

  shouldComponentUpdate(nextProps) {
    return !(_.isEqual(nextProps.device, this.props.device));
  }

  getDeviceStatus() {
    if (this.props.device && this.props.device.get('status') === 'OFFLINE') {
      return 'offline';
    } else if (this.props.device && this.props.device.get('status') === 'ONLINE') {
      return 'online';
    }
  }

  onChange(metafield) {

    const device = this.props.device.update('metaFields', (metafields) => metafields.map((value) => {
      if (metafield.get('name') === value.get('name'))
        return metafield;
      return value;
    }));

    return this.props.onChange(device);
  }

  render() {

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
              <DeviceAuthToken authToken={this.props.device.get('token')}/>
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
              { this.props.device.has('productLogoUrl') && (
                <img src={this.props.device.get('productLogoUrl')}/>
              )}
            </div>
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            { this.props.device.has('metaFields') && (<Section title="Metadata">
              <div className="device--device-info-metadata-list">
                { this.props.device.get('metaFields').map((field, key) => {

                    const form = `devicemetadataedit${field.get('name')}`;

                    const props = {
                      data: field,
                      key: key,
                      form: form,
                      onChange: this.onChange.bind(this)
                    };

                    if (field.get('type') === Metadata.Fields.TEXT)
                      return (<DeviceMetadata.Text {...props}/>);

                    if (field.get('type') === Metadata.Fields.NUMBER)
                      return (<DeviceMetadata.Number {...props}/>);

                    if (field.get('type') === Metadata.Fields.UNIT)
                      return (<DeviceMetadata.Unit {...props}/>);

                    if (field.get('type') === Metadata.Fields.RANGE)
                      return (<DeviceMetadata.Range {...props}/>);

                    if (field.get('type') === Metadata.Fields.CONTACT)
                      return (<DeviceMetadata.Contact {...props}/>);

                    if (field.get('type') === Metadata.Fields.TIME)
                      return (<DeviceMetadata.Time {...props}/>);

                    if (field.get('type') === Metadata.Fields.COST)
                      return (<DeviceMetadata.Cost {...props}/>);

                    if (field.get('type') === Metadata.Fields.COORDINATES)
                      return (<DeviceMetadata.Coordinates {...props}/>);

                  })
                }
              </div>
            </Section>) }
          </Col>
        </Row>
      </div>
    );
  }

}

export default DeviceInfo;
