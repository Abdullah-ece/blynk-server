import React from 'react';
import Base from '../Base';
import {Fieldset, LinearIcon} from 'components';
import {Col, Row} from 'antd';
import ListModal from './modal';
import './styles.less';

class Location extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    if (field.isLocationEnabled && !field.siteName)
    // Case 2 Location Enabled but not filled, availableLocationsLength = 0
      return (
        <Fieldset>
          <Fieldset.Legend type="dark">
                <span className="device-metadata--location-field--title">
                  <LinearIcon type={field.icon || 'map'}/>
                  {field.name}
                </span>
          </Fieldset.Legend>
          <div className="device-metadata--location-field--description">
            No information about location
          </div>
        </Fieldset>
      );


    const address = field.streetAddress;
    const addressInfo = [];
    const cityInfo = [];

    if (field.buildingName && field.isBuildingNameEnabled)
      addressInfo.push(<div className="device-metadata--location-field--info-list-item--option">Building: {field.buildingName}</div>);

    if (field.floor && field.isFloorEnabled)
      addressInfo.push(<div className="device-metadata--location-field--info-list-item--option">Floor: {field.floor}</div>);

    if (field.unit && field.isUnitEnabled)
      addressInfo.push(<div className="device-metadata--location-field--info-list-item--option">Unit: {field.unit}</div>);

    if (field.room && field.isRoomEnabled)
      addressInfo.push(<div className="device-metadata--location-field--info-list-item--option">Room: {field.room}</div>);

    if (field.zone && field.isZoneEnabled)
      addressInfo.push(<div className="device-metadata--location-field--info-list-item--option">Zone: {field.zone}</div>);

    if (field.city && field.isCityEnabled)
      cityInfo.push(field.city);

    if (field.state && field.isCountryEnabled)
      cityInfo.push(field.state);

    if (field.zip && field.isZipEnabled)
      cityInfo.push(field.zip);


    // Case 3 Location Enabled and filled
    return (
      <Fieldset>
        <Row type="flex">
          <Col>
            <Fieldset.Legend type="dark">
                <span className="device-metadata--location-field--title">
                  <LinearIcon type="map"/>
                  {field.name}
                </span>
            </Fieldset.Legend>
            <div className="device-metadata--location-field--description">
              <div className="device-metadata--location-field--site-name">
                {field.siteName}
              </div>
              <div className="device-metadata--location-field--info-list">

                {address && (
                  <div className="device-metadata--location-field--info-list-item">
                    {address}
                  </div>
                )}

                { ((cityInfo && cityInfo.length) || field.country ) && (
                  <div className="device-metadata--location-field--info-list--primary">
                    {cityInfo && cityInfo.length > 0 && (
                      <div className="device-metadata--location-field--info-list-item">
                        {cityInfo.join(', ')}
                      </div>
                    )}

                    {field.country && (
                      <div className="device-metadata--location-field--info-list-item">
                        {field.country}
                      </div>
                    )}
                  </div>
                )}

                {addressInfo && addressInfo.length > 0 && (
                  <div className="device-metadata--location-field--info-list--primary">
                    {addressInfo}
                  </div>
                )}

              </div>
            </div>
          </Col>
        </Row>

      </Fieldset>
    );

  }

  getEditableComponent() {
    let field = this.props.data;

    let options = (Array.isArray(field.options) ? field.options : []).map((option) => ({
      key: option,
      value: option
    }));

    return (
      <div>
        <ListModal form={this.props.form} options={options}/>
      </div>
    );
  }

}

export default Location;
