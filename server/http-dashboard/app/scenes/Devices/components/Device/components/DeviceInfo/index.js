import React from 'react';
import { Row, Col } from 'antd';
import {
  Fieldset,
  DeviceStatus,
  DeviceAuthToken,
  Section,
  DeviceMetadata, /*BackTop*/
} from 'components';
import { Metadata } from 'services/Products';
// import _ from 'lodash';
import { getCalendarFormatDate } from 'services/Date';
import { DeviceDelete } from 'scenes/Devices/scenes';
import './styles.less';

class DeviceInfo extends React.Component {

  static propTypes = {
    device: React.PropTypes.object,
    onMetadataChange: React.PropTypes.func,
    onDeviceDelete: React.PropTypes.func,
    account: React.PropTypes.object,
  };

  constructor(props){
    super(props);

    this.showMore = this.showMore.bind(this);

    this.state = {
      showMore: false
    };
  }

  // shouldComponentUpdate(nextProps) {
  //   return !(_.isEqual(nextProps.device, this.props.device));
  // }

  getDeviceStatus() {
    if (!this.props.device || !this.props.device.status)
      return 'offline';

    if (this.props.device && this.props.device.status === 'OFFLINE') {
      return 'offline';
    } else if (this.props.device && this.props.device.status === 'ONLINE') {
      return 'online';
    }
  }

  showMore() {
    this.setState({ showMore: !this.state.showMore });
  }

  onChange(metadata) {
    return this.props.onMetadataChange(metadata);
  }

  render() {

    let time = this.props.device.lastReportedAt;
    let disconnectTime = this.props.device.disconnectTime;
    let metadataUpdatedAt = this.props.device.metadataUpdatedAt;

    let lastReported = Number(time) ? getCalendarFormatDate(time) : 'Not reported yet';

    let lastOnlineTime = getCalendarFormatDate(disconnectTime);

    let deviceActivatedTime = getCalendarFormatDate(this.props.device.activatedAt);

    let metadataUpdatedTime = getCalendarFormatDate(metadataUpdatedAt);

    const metadataList = this.props.device.metaFields;
    // const metadataList = metafields;

    const filterDisabledLocations = (metadataList) => {
      return metadataList.filter((metadataField) => {
        return metadataField.type !== Metadata.Fields.LOCATION || (metadataField.type === Metadata.Fields.LOCATION && metadataField.isLocationEnabled);
      });
    };

    return (
      <div className="device--device-info">
        <Row className="device--device-info-details">
          <Col span={6}>
            <Fieldset>
              <Fieldset.Legend>Status</Fieldset.Legend>
              <DeviceStatus status={this.getDeviceStatus()}/>
            </Fieldset>
            {!!Number(disconnectTime) && this.props.device.status === 'OFFLINE' && (
              <Fieldset>
                <Fieldset.Legend>Last Online</Fieldset.Legend>
                {lastOnlineTime}
              </Fieldset>
            )}
            <Fieldset>
              <Fieldset.Legend>Device Activated</Fieldset.Legend>
              {deviceActivatedTime} <br/> by {this.props.device.activatedBy}
            </Fieldset>
            {this.props.device.token && (
              <Fieldset>
                <Fieldset.Legend>Auth Token</Fieldset.Legend>
                <DeviceAuthToken authToken={this.props.device.token} deviceId={this.props.device.id}/>
              </Fieldset>
            ) || null}
          </Col>
          <Col span={6}>
            <Fieldset>
              <Fieldset.Legend>Last Reported</Fieldset.Legend>
              {lastReported}
            </Fieldset>
            {this.props.device.metadataUpdatedAt && metadataUpdatedAt > 0 && (
              <Fieldset>
                <Fieldset.Legend>Latest Metadata update</Fieldset.Legend>
                {metadataUpdatedTime}
                <br/> by {this.props.device.metadataUpdatedBy}
              </Fieldset>
            ) || null}
            {this.props.device.orgName && (
              <Fieldset>
                <Fieldset.Legend>Organization</Fieldset.Legend>
                {this.props.device.orgName}
              </Fieldset>
            )}
            {this.props.device.hardwareInfo && this.props.device.hardwareInfo.version && (
              <Fieldset>
                <Fieldset.Legend>Firmware version</Fieldset.Legend>
                {this.props.device.hardwareInfo.version}
              </Fieldset>
            )}
            {this.props.device.hardwareInfo && (
            <Fieldset>
              <a href="#" onClick={this.showMore}>{this.state && this.state.showMore? 'Show less' : 'Show more'}</a>
            </Fieldset>
            )}
            {this.state.showMore && this.props.device.hardwareInfo && this.props.device.hardwareInfo.blynkVersion && (
              <Fieldset>
                <Fieldset.Legend>Blynk version</Fieldset.Legend>
                {this.props.device.hardwareInfo.blynkVersion}
              </Fieldset>
            )}
            {this.state.showMore && this.props.device.hardwareInfo && this.props.device.hardwareInfo.boardType && (
              <Fieldset>
                <Fieldset.Legend>Board Type</Fieldset.Legend>
                {this.props.device.hardwareInfo.boardType}
              </Fieldset>
            )}
            {this.state.showMore && this.props.device.hardwareInfo && this.props.device.hardwareInfo.cpuType && (
              <Fieldset>
                <Fieldset.Legend>CPU Type</Fieldset.Legend>
                {this.props.device.hardwareInfo.cpuType}
              </Fieldset>
            )}
            {this.state.showMore && this.props.device.hardwareInfo && this.props.device.hardwareInfo.connectionType && (
              <Fieldset>
                <Fieldset.Legend>Connection Type</Fieldset.Legend>
                {this.props.device.hardwareInfo.connectionType}
              </Fieldset>
            )}
            {this.state.showMore && this.props.device.hardwareInfo && this.props.device.hardwareInfo.build && (
              <Fieldset>
                <Fieldset.Legend>Build</Fieldset.Legend>
                {this.props.device.hardwareInfo.build}
              </Fieldset>
            )}
            {this.state.showMore && this.props.device.hardwareInfo && this.props.device.hardwareInfo.templateId && (
              <Fieldset>
                <Fieldset.Legend>Template Id</Fieldset.Legend>
                {this.props.device.hardwareInfo.templateId}
              </Fieldset>
            )}
            {this.state.showMore && this.props.device.hardwareInfo && this.props.device.hardwareInfo.heartbeatInterval && (
              <Fieldset>
                <Fieldset.Legend>Heartbeat Interval</Fieldset.Legend>
                {this.props.device.hardwareInfo.heartbeatInterval}
              </Fieldset>
            )}
            {this.state.showMore && this.props.device.hardwareInfo && this.props.device.hardwareInfo.buffIn && (
              <Fieldset>
                <Fieldset.Legend>Buff In</Fieldset.Legend>
                {this.props.device.hardwareInfo.buffIn}
              </Fieldset>
            )}
            {this.state.showMore && this.props.device.hardwareInfo && this.props.device.hardwareInfo.lastLoggedIP && (
              <Fieldset>
                <Fieldset.Legend>IP</Fieldset.Legend>
                {this.props.device.hardwareInfo.lastLoggedIP}
              </Fieldset>
            )}
            {this.state.showMore && this.props.device.hardwareInfo && this.props.device.hardwareInfo.connectTime && (
              <Fieldset>
                <Fieldset.Legend>Connect time</Fieldset.Legend>
                {this.props.device.hardwareInfo.connectTime}
              </Fieldset>
            )}
          </Col>
          <Col span={12}>

            <DeviceDelete deviceId={this.props.device.id}
                          onDeviceDelete={this.props.onDeviceDelete}/>

            <div className="device--device-info-logo">
              {this.props.device.productLogoUrl && (
                <img src={this.props.device.productLogoUrl}/>
              )}
            </div>
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            {metadataList && filterDisabledLocations(metadataList).length !== 0 &&
            (<Section title="Metadata">
              <div className="device--device-info-metadata-list">
                {filterDisabledLocations(metadataList).map((field) => {

                  const form = `device${this.props.device.id}metadataedit${field.name}`;

                  const fieldProps = {
                    key: form,
                    form: form,
                    initialValues: field,
                    modalWrapClassName: 'device-metadata-modal--location',
                  };

                  const props = {
                    form: form,
                    data: field,
                    onChange: this.onChange.bind(this),
                    account: this.props.account,
                    modalWrapClassName: 'device-metadata-modal--location',
                  };

                  if (field.type === Metadata.Fields.LOCATION)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Location {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.type === Metadata.Fields.EMAIL)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Text {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.type === Metadata.Fields.TEXT)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Text {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.type === Metadata.Fields.NUMBER)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Number {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.type === Metadata.Fields.UNIT)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Unit {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.type === Metadata.Fields.RANGE)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Range {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.type === Metadata.Fields.CONTACT)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Contact {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.type === Metadata.Fields.TIME)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Time {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.type === Metadata.Fields.COST)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Cost {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.type === Metadata.Fields.COORDINATES)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Coordinates {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.type === Metadata.Fields.SWITCH)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.Switch {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.type === Metadata.Fields.DEVICE_REFERENCE)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.DeviceReference {...props}/>
                      </DeviceMetadata.Field>
                    );

                  if (field.type === Metadata.Fields.LIST)
                    return (
                      <DeviceMetadata.Field {...fieldProps}>
                        <DeviceMetadata.List {...props}/>
                      </DeviceMetadata.Field>
                    );

                })
                }
                {/*<BackTop/>*/}
              </div>
            </Section>)}
          </Col>
        </Row>
      </div>
    );
  }

}

export default DeviceInfo;
