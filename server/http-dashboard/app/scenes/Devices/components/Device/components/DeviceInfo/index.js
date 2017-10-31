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
    onMetadataChange: React.PropTypes.func
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

  onChange(metadata) {
    return this.props.onMetadataChange(metadata);
  }

  render() {

    let time = this.props.device.get('dataReceivedAt');
    let disconnectTime = this.props.device.get('disconnectTime');
    let activatedAt = this.props.device.get('activatedAt');
    let metadataUpdatedAt = this.props.device.get('metadataUpdatedAt');

    const timeConfig = {
      sameDay: '[Today], hh:mm A',
      lastDay: '[Yesterday], hh:mm A',
      lastWeek: 'dddd, hh:mm A',
      sameElse: 'hh:mm A, YYYY.MM.DD'
    };

    let lastReported = Number(time) ? moment(Number(time)).calendar(null, timeConfig) : 'Not reported yet';

    let lastOnlineTime = moment(Number(disconnectTime || 0)).calendar(null, timeConfig);

    let deviceActivatedTime = moment(Number(activatedAt || 0)).calendar(null, timeConfig);

    let metadataUpdatedTime = moment(Number(metadataUpdatedAt || 0)).calendar(null, timeConfig);

    return (
      <div className="device--device-info">
        <Row className="device--device-info-details">
          <Col span={8}>
            <Fieldset>
              <Fieldset.Legend>Status</Fieldset.Legend>
              <DeviceStatus status={this.getDeviceStatus()}/>
            </Fieldset>
            {!!Number(disconnectTime) && this.props.device.get('status') === 'OFFLINE' && (
              <Fieldset>
                <Fieldset.Legend>Last Online</Fieldset.Legend>
                {lastOnlineTime}
              </Fieldset>
            )}
            <Fieldset>
              <Fieldset.Legend>Device Activated</Fieldset.Legend>
              {deviceActivatedTime} by {this.props.device.get('activatedBy')}
            </Fieldset>
            <Fieldset>
              <Fieldset.Legend>Auth Token</Fieldset.Legend>
              <DeviceAuthToken authToken={this.props.device.get('token')}/>
            </Fieldset>
          </Col>
          <Col span={8}>
            <Fieldset>
              <Fieldset.Legend>Last Reported</Fieldset.Legend>
              {lastReported}
            </Fieldset>
            {this.props.device.has('metadataUpdatedAt') && metadataUpdatedAt > 0 && (
              <Fieldset>
                <Fieldset.Legend>Latest Metadata update</Fieldset.Legend>
                {metadataUpdatedTime} by {this.props.device.get('metadataUpdatedBy')}
              </Fieldset>
            )}
            {this.props.device.has('orgName') && (
              <Fieldset>
                <Fieldset.Legend>Organization</Fieldset.Legend>
                {this.props.device.get('orgName')}
              </Fieldset>
            )}
          </Col>
          <Col span={8}>
            <div className="device--device-info-logo">
              {this.props.device.has('productLogoUrl') && (
                <img src={this.props.device.get('productLogoUrl')}/>
              )}
            </div>
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            {this.props.device.has('metaFields') && this.props.device.get('metaFields').size !== 0 &&
            (<Section title="Metadata">
              <div className="device--device-info-metadata-list">
                {this.props.device.get('metaFields').map((field) => {

                  const form = `device${this.props.device.get('id')}metadataedit${field.get('name')}`;

                  const fieldProps = {
                    key: form,
                    form: form,
                    initialValues: field.toJS()
                  };

                  const props = {
                    form: form,
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
            </Section>)}
          </Col>
        </Row>
      </div>
    );
  }

}

export default DeviceInfo;
