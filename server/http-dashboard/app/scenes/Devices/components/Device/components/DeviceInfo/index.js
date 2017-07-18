import React from 'react';
import moment from 'moment';
import {Row, Col} from 'antd';
import {Fieldset, DeviceStatus, DeviceAuthToken, Section, DeviceMetadata, BackTop} from 'components';
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
    if (!this.props.device.has('status'))
      return 'offline';

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

    let time = this.props.device.get('dataReceivedAt');
    let disconnectTime = this.props.device.get('disconnectTime');

    let lastReported = Number(time) ? moment(Number(time)).calendar(null, {
      sameDay: '[Today], hh:mm A',
      lastDay: '[Yesterday], hh:mm A',
      lastWeek: 'dddd, hh:mm A',
      sameElse: 'hh:mm A, YYYY.MM.DD'
    }) : 'Not reported yet';

    let lastOnlineTime = Number(disconnectTime) ? `Last online: ` + moment(Number(disconnectTime)).calendar(null, {
        sameDay: '[Today], hh:mm A',
        lastDay: '[Yesterday], hh:mm A',
        lastWeek: 'dddd, hh:mm A',
        sameElse: 'hh:mm A, YYYY.MM.DD'
      }) : `Wasn't online yet`;

    return (
      <div className="device--device-info">
        <Row className="device--device-info-details">
          <Col span={8}>
            <Fieldset>
              <Fieldset.Legend>Status</Fieldset.Legend>
              <DeviceStatus status={this.getDeviceStatus()}/>
              { this.props.device.has('status') && this.props.device.get('status') === 'OFFLINE' && (
                <i> ({ lastOnlineTime })</i>
              )}
            </Fieldset>
            <Fieldset>
              <Fieldset.Legend>Auth Token</Fieldset.Legend>
              <DeviceAuthToken authToken={this.props.device.get('token')}/>
            </Fieldset>
          </Col>
          <Col span={8}>
            <Fieldset>
              <Fieldset.Legend>Last Reported</Fieldset.Legend>
              { lastReported }
            </Fieldset>
            { this.props.device.has('orgName') && (
              <Fieldset>
                <Fieldset.Legend>Organization</Fieldset.Legend>
                { this.props.device.get('orgName') }
              </Fieldset>
            )}
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

                  const fieldProps = {
                    key: key,
                    form: form,
                    initialValues: field.toJS()
                  };

                  const props = {
                    data: field,
                    onChange: this.onChange.bind(this)
                  };


                  if (field.get('type') === Metadata.Fields.TEXT)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Text {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.get('type') === Metadata.Fields.NUMBER)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Number {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.get('type') === Metadata.Fields.UNIT)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Unit {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.get('type') === Metadata.Fields.RANGE)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Range {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.get('type') === Metadata.Fields.CONTACT)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Contact {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.get('type') === Metadata.Fields.TIME)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Time {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.get('type') === Metadata.Fields.COST)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Cost {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.get('type') === Metadata.Fields.COORDINATES)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Coordinates {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.get('type') === Metadata.Fields.SWITCH)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Switch {...props}/>
                      </DeviceMetadata.Field>
                    );

                })
                }
                <BackTop/>
              </div>
            </Section>) }
          </Col>
        </Row>
      </div>
    );
  }

}

export default DeviceInfo;
