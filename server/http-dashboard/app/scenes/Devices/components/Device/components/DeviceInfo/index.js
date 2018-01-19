import React from 'react';
import {Row, Col} from 'antd';
import {Fieldset, DeviceStatus, DeviceAuthToken, Section, DeviceMetadata, BackTop} from 'components';
import {Metadata} from 'services/Products';
import _ from 'lodash';
import {getCalendarFormatDate} from 'services/Date';
import {DeviceDelete} from 'scenes/Devices/scenes';
import './styles.less';

class DeviceInfo extends React.Component {

  static propTypes = {
    device: React.PropTypes.object,
    onMetadataChange: React.PropTypes.func,
    account: React.PropTypes.object,
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
    let metadataUpdatedAt = this.props.device.get('metadataUpdatedAt');

    let lastReported = Number(time) ? getCalendarFormatDate(time) : 'Not reported yet';

    let lastOnlineTime = getCalendarFormatDate(disconnectTime);

    let deviceActivatedTime = getCalendarFormatDate(this.props.device.get('activatedAt'));

    let metadataUpdatedTime = getCalendarFormatDate(metadataUpdatedAt);

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
              {deviceActivatedTime} <br/> by {this.props.device.get('activatedBy')}
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
                {metadataUpdatedTime} <br/> by {this.props.device.get('metadataUpdatedBy')}
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

            <DeviceDelete deviceId={this.props.device.get('id')} />

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
                    initialValues: field.toJS(),
                  };

                  const props = {
                    form: form,
                    data: field,
                    onChange: this.onChange.bind(this),
                    account: this.props.account,
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
