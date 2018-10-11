import React from 'react';
import ReactDOM from 'react-dom';
import {Input, Form, Col, Row} from 'antd';
import {LocationAutocomplete} from 'components';
import {Field, getFormValues, change} from 'redux-form';
import PropTypes from 'prop-types';
import google from 'google';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

@connect((state, ownProps) => ({
  formValues: getFormValues(ownProps.form)(state)
}), (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch)
}))
class LocationModal extends React.Component {

  static propTypes = {
    form: PropTypes.string,
    changeForm: PropTypes.func,
    options: PropTypes.oneOfType([
      PropTypes.object,
      PropTypes.array,
    ]),
    formValues: PropTypes.object,
  };

  constructor(props) {
    super(props);

    this.state = {
      isAdditionalFieldsVisible: false
    };

    this.addressAutocomplete = this.addressAutocomplete.bind(this);
  }

  componentDidMount() {

    this.checkStreet();

    this.initMap(
        this.props.formValues && this.props.formValues.lat,
        this.props.formValues && this.props.formValues.lon
    );
  }

  checkStreet() {
    if(this.props.formValues.streetAddress) {
      this.setState({
        isAdditionalFieldsVisible: true
      });
    }
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

  placeMarker(placeId, geometry) {
    let myLatLng = {lat: -25.363, lng: 131.044};

    this.map = new google.maps.Map(ReactDOM.findDOMNode(this.googleMapRef), {
      zoom: 4,
      center: myLatLng,
      disableDefaultUI: true
    });

    new google.maps.Marker({
      map: this.map,
      place: {
        placeId: placeId,
        location: geometry.location,
      }
    });

    this.map.setCenter(geometry.location);

    this.map.setZoom(15);

  }

  input(props) {
    return (
      <Input placeholder={props.placeholder} onChange={props.input.onChange} value={props.input.value}/>
    );
  }

  addressAutocomplete(props) {
    const onSelect = (values) => {

      this.placeMarker(
        values.placeId,
        values.geometry,
      );

      const form = this.props.form;
      const field = (fieldName) => `${fieldName}`;

      let streetString = '';

      if(values.street)
        streetString += values.street;

      if(values.number)
        streetString += ` ${values.number}`;

      props.input.onChange(streetString.trim());

      if(values.geometry && values.geometry.location && values.geometry.location.lat && values.geometry.location.lng) {
        this.props.changeForm(form, field(`lat`), values.geometry.location.lat());
        this.props.changeForm(form, field(`lon`), values.geometry.location.lng());
      }

      if(streetString) {
        this.props.changeForm(form, field(`streetAddress`), streetString);
      }

      if(values.city) {
        this.props.changeForm(form, field(`city`), values.city);
      }

      if(values.state) {
        this.props.changeForm(form, field(`state`), values.state);
      }

      if(values.country) {
        this.props.changeForm(form, field(`country`), values.country);
      }

      if(values.postal) {
        this.props.changeForm(form, field(`zip`), values.postal);
      }

      this.setState({
        isAdditionalFieldsVisible: true
      });

    };

    const onChange = (value) => {
      props.input.onChange(value);
    };

    const onBlur = (value) => {
      if(value && value.trim()) {
        props.input.onChange(value.trim());
        this.setState({
          isAdditionalFieldsVisible: true
        });
      }
    };

    const onFocus = (/*value*/) => {
      // do something
    };

    return (
      <LocationAutocomplete value={props.input.value} onBlur={onBlur} onFocus={onFocus} style={{width: '100%'}} onChange={onChange} onSelect={onSelect} placeholder={props.placeholder}/>
    );
  }

  render() {

    return (
      <div>

        <Row type="flex">
          <Col span={10}>
            <Form.Item className="medium-offset" label={this.props.formValues.name}>
              <Field component={this.input} name={'siteName'} placeholder={this.props.formValues.name}/>
            </Form.Item>

            { (this.state.isAdditionalFieldsVisible || (this.props.formValues.siteName && this.props.formValues.siteName.trim())) && (
              <Form.Item className="normal-offset" label={"Address"}>
                <Field component={this.addressAutocomplete} name={'streetAddress'} placeholder={'Address (start typing)'}/>
              </Form.Item>
            )}

            { (this.state.isAdditionalFieldsVisible) && (
              <div>

                {
                  this.props.formValues.isCityEnabled && (
                    <Form.Item className="normal-offset" label={"City"}>
                      <Field component={this.input} name={'city'} placeholder={'City'}/>
                    </Form.Item>
                  )
                }

                <Row>

                  { this.props.formValues.isCountryEnabled && (
                    <Col span={this.props.formValues.isZipEnabled ? 11 : 24}>

                      <Form.Item className="normal-offset" label={"Country"}>
                        <Field component={this.input} name={'country'} placeholder={'Country'}/>
                      </Form.Item>

                    </Col>
                  )}

                  { this.props.formValues.isZipEnabled && (
                    <Col span={this.props.formValues.isCountryEnabled ? 11 : 24} offset={this.props.formValues.isCountryEnabled ? 2 : 0}>

                      <Form.Item className="normal-offset" label={"ZIP"}>
                        <Field component={this.input} name={'zip'} placeholder={'ZIP'}/>
                      </Form.Item>

                    </Col>
                  )}

                </Row>


                { this.props.formValues.isBuildingNameEnabled && (
                  <Form.Item className="normal-offset" label={"Building Name"}>
                    <Field component={this.input} name={'buildingName'} placeholder={'Building Name'}/>
                  </Form.Item>
                )}
                { this.props.formValues.isFloorEnabled && (
                  <Form.Item className="normal-offset" label={"Floor"}>
                    <Field component={this.input} name={'floor'} placeholder={'Floor'}/>
                  </Form.Item>
                )}
                { this.props.formValues.isUnitEnabled && (
                  <Form.Item className="normal-offset" label={"Unit"}>
                    <Field component={this.input} name={'unit'} placeholder={'Unit'}/>
                  </Form.Item>
                )}
                { this.props.formValues.isRoomEnabled && (
                  <Form.Item className="normal-offset" label={"Room"}>
                    <Field component={this.input} name={'room'} placeholder={'Room'}/>
                  </Form.Item>
                )}
                { this.props.formValues.isZoneEnabled && (
                  <Form.Item className="normal-offset" label={"Zone"}>
                    <Field component={this.input} name={'zone'} placeholder={'Zone'}/>
                  </Form.Item>
                )}

              </div>
            )}
          </Col>
          <Col span={12} offset={1}>
            <div ref={(ref) => this.googleMapRef = ref} style={{height: 'calc(100% - 32px)', margin: '16px 0'}}/>
          </Col>
        </Row>

      </div>
    );
  }

}

export default LocationModal;
