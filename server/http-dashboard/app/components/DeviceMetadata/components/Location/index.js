import React from 'react';
import Base from '../Base';
import {Fieldset, LinearIcon} from 'components';
import {Col, Row, Button, Select} from 'antd';
import ListModal from './modal';
import './styles.less';

class List extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    if(this.props.availableLocationsList && this.props.availableLocationsList.length === 0 && !field.isFilled)
      // Case 2 Location Enabled but not filled, availableLocationsLength = 0
      return (
        <Fieldset>
          <Row>
            <Col span={12}>
              <Fieldset.Legend type="dark">
                <span className="device-metadata--location-field--title">
                  <LinearIcon type="map"/>
                  {field.name}
                </span>
              </Fieldset.Legend>
              <div className="device-metadata--location-field--description">
                No information about location
              </div>
            </Col>
            <Col span={12}>
              <Button onClick={this.handleEdit} style={{float: 'right'}}
                      className="device-metadata--location-field--add-location-btn">
                Add new location
              </Button>
            </Col>
          </Row>

        </Fieldset>
      );

    if(this.props.availableLocationsList && this.props.availableLocationsList.length > 0 && !field.isFilled)
    // Case 2 Location Enabled but not filled, availableLocationsLength > 0
      return (
        <Fieldset>
          <Row>
            <Col span={12}>
              <Fieldset.Legend type="dark">
                <span className="device-metadata--location-field--title">
                  <LinearIcon type="map"/>
                  {field.name}
                </span>
              </Fieldset.Legend>
              <div className="device-metadata--location-field--description">
                <Select placeholder="Choose Location" style={{width: '300px'}}>
                  <Select.Option key={`Warehouse 01`}>Warehouse 01</Select.Option>
                  <Select.Option key={`Warehouse 02`}>Warehouse 02</Select.Option>
                  <Select.Option key={`Warehouse 03`}>Warehouse 03</Select.Option>
                  <Select.Option key={`Add new Location`} className="device-metadata--location-field--add-new">
                    <div className="device-metadata--location-field--add-news">Add new Location</div>
                  </Select.Option>
                </Select>
              </div>
            </Col>
          </Row>

        </Fieldset>
      );

    if(field.isFilled) {

      const address = field.streetAddress;
      const addressInfo = [];
      const cityInfo = [];

      if(field.buildingName)
        addressInfo.push(field.buildingName);

      if(field.floor)
        addressInfo.push(field.floor);

      if(field.unit)
        addressInfo.push(field.unit);

      if(field.room)
        addressInfo.push(field.room);

      if(field.zone)
        addressInfo.push(field.zone);

      if(field.city)
        cityInfo.push(field.city);

      if(field.state)
        cityInfo.push(field.state);

      if(field.zip)
        cityInfo.push(field.zip);




      // Case 3 Location Enabled and filled
      return (
        <Fieldset>
          <Row>
            <Col span={12}>
              <Fieldset.Legend type="dark">
                <span className="device-metadata--location-field--title">
                  <LinearIcon type="map"/>
                  {field.name}
                </span>
              </Fieldset.Legend>
              <div className="device-metadata--location-field--description">
                <Select placeholder="Choose Location" style={{width: '300px'}}>
                  <Select.Option key={`Warehouse 01`}>Warehouse 01</Select.Option>
                  <Select.Option key={`Warehouse 02`}>Warehouse 02</Select.Option>
                  <Select.Option key={`Warehouse 03`}>Warehouse 03</Select.Option>
                  <Select.Option key={`Add new Location`} className="device-metadata--location-field--add-new">
                    <div className="device-metadata--location-field--add-news">Add new Location</div>
                  </Select.Option>
                </Select>
                <div className="device-metadata--location-field--info-list">

                  {address && (
                    <div className="device-metadata--location-field--info-list-item">
                      {address}
                    </div>
                  )}

                  {addressInfo && addressInfo.length > 0 && (
                    <div className="device-metadata--location-field--info-list-item">
                      {addressInfo.join(', ')}
                    </div>
                  )}

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
              </div>
            </Col>
          </Row>

        </Fieldset>
      );
    }


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

export default List;
