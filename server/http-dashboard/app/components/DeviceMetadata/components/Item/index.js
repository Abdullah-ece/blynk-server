import React from 'react';
import ReactDOM from "react-dom";
import {Metadata} from "services/Products";
import {
  isUserAbleToEdit, SUPER_ADMIN_ROLE_ID
} from "services/Roles";
import google from 'google';
import {Row, Col, Button, Icon} from 'antd';
import './styles.less';
import {connect} from 'react-redux';

@connect((state) => ({
  roles: state.Organization.roles
}))
class Item extends React.Component {

  static propTypes = {
    field: React.PropTypes.object,
    children: React.PropTypes.any,
    fieldName: React.PropTypes.string,
    onEditClick: React.PropTypes.func,
    fieldRole: React.PropTypes.array,
    userRole: React.PropTypes.number,
    isEditDisabled: React.PropTypes.bool,
    isManufacturer: React.PropTypes.bool,
  };

  constructor(props){
    super(props);
    this.onEditClick = this.onEditClick.bind(this);
  }

  componentDidMount() {
    if(this.props.field && this.props.field.type === Metadata.Fields.LOCATION) {
      this.initMap(this.props.field.lat, this.props.field.lon);
    }
  }

  componentDidUpdate(prevProps) {
    if(this.props.field && this.props.field.type === Metadata.Fields.LOCATION && this.props.field.lat && this.props.field.lon && (this.props.field.lat !== prevProps.field.lat || this.props.field.lon !== prevProps.field.lon)) {
      this.initMap(this.props.field.lat, this.props.field.lon);
    }
  }

  onEditClick() {
    if (typeof this.props.onEditClick === 'function')
      this.props.onEditClick();
  }

  initMap(lat, lon) {

    /* init map in 100 ms to prevent grey rectangles (google map issue) */

    setTimeout(() => {

      let myLatLng = {lat: 39.5490902, lng: -103.7329746};

      this.map = new google.maps.Map(ReactDOM.findDOMNode(this.googleMapRef), {
        zoom            : 3,
        center          : myLatLng,
        disableDefaultUI: true
      });

      if (lat && lon) {
        new google.maps.Marker({
          map     : this.map,
          position: {
            lat: lat,
            lng: lon,
          },
        });

        this.map.setCenter({
          lat: lat,
          lng: lon
        });

        this.map.setZoom(15);
      }

    }, 100);
  }

  render() {
    return (
      <div className="device-metadata--item">
        {this.props.isEditDisabled ? (
          <Row type="flex">
            <Col span={24}>
              {this.props.children}
            </Col>
          </Row>
        ) : (
          <Row type="flex">
            <Col span={14}>
              {this.props.children}
            </Col>
            { (this.props.field && this.props.field.type === Metadata.Fields.LOCATION && this.props.field.lat && this.props.field.lon) && (
              <Col span={10}>
                {this.props.userRole === SUPER_ADMIN_ROLE_ID || (this.props.isManufacturer === false && isUserAbleToEdit(this.props.userRole, this.props.fieldRole)) ? (
                  <div className="device-metadata--location-field--map-overlay">
                    <Button type="primary" onClick={this.onEditClick}>
                      <Icon type="edit"/>Edit
                    </Button>
                  </div>
                ) : (null)}
                <div ref={(ref) => this.googleMapRef = ref} style={{height: 'calc(100%)'}}/>
              </Col>
            ) || (
              <Col span={10} className="device-metadata--item-edit">
                {this.props.userRole === SUPER_ADMIN_ROLE_ID ||  (this.props.isManufacturer === false && isUserAbleToEdit(this.props.userRole, this.props.fieldRole)) ? (
                  <Button type="primary" onClick={this.onEditClick}>
                    <Icon type="edit"/>Edit
                  </Button>
                ): (null)}
              </Col>
            ) }
          </Row>
        )}
      </div>
    );
  }

}

export default Item;
