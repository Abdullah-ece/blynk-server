import React from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';
import google from 'google';
import {Select} from 'antd';
import _ from "lodash";
import "./styles.less";

class Index extends React.Component {

  static propTypes = {
    onSelect: PropTypes.func,
    placeholder: PropTypes.string,
    onChange: PropTypes.func,
    onBlur: PropTypes.func,
    onFocus: PropTypes.func,
    value: PropTypes.string,
    style: PropTypes.object,
  };

  constructor(props) {
    super(props);

    this.state = {
      options: null
    };

    this.isSelectFired = false;

    this.handleChange = this.handleChange.bind(this);
    this.handleSelectChange = this.handleSelectChange.bind(this);
    this.handleSearchChange = this.handleSearchChange.bind(this);
    this.handleBlur = this.handleBlur.bind(this);
    this.handleFocus = this.handleFocus.bind(this);
  }

  componentDidMount() {
    this.autocomplete = new google.maps.places.AutocompleteService();

    let map = ReactDOM.findDOMNode(this.mapRef);

    this.places = new google.maps.places.PlacesService(map);
  }

  handleBlur(value) {
    if(this.props && this.props.onBlur)
      this.props.onBlur(value);
  }

  handleFocus(value) {
    if(this.props && this.props.onFocus)
      this.props.onFocus(value);
  }

  handleChange(value) {
    if (this.props.onChange)
      this.props.onChange(value);
  }

  handleSelectChange(value, option) {

    const parseResponse = (data) => {

      let streetNumber, address, city, state, country, postalCode;

      let parser = (addressComponents, searchType) => {
        const result = _.find(addressComponents, (component) => {
          return component.types.some((type) => {
            return type === searchType;
          });
        });

        return result && result.long_name || null;
      };

      if(!data || !data.address_components) {
        return {};
      }

      streetNumber = parser(data.address_components, 'street_number');
      address = parser(data.address_components, 'route');
      city = parser(data.address_components, 'locality');
      state = parser(data.address_components, 'administrative_area_level_1');
      country = parser(data.address_components, 'country');
      postalCode = parser(data.address_components, 'postal_code');

      return {
        number: streetNumber, // street_number
        street: address, // route,
        city: city, // locality
        state: state, // administrative_area_level_1
        country: country, // country,
        postal: postalCode, // postal_code,
        geometry: data.geometry, // geometry,
        placeId: data.place_id, // placeId
      };
    };

    this.places.getDetails({
      placeId: option.props.placeId
    }, (response) => {

      const data = parseResponse(response);

      let resultString = '';

      if(data.street)
        resultString += data.street;

      if(data.number)
        resultString += ` ${data.number}`;

      this.handleChange(resultString);

      this.props.onSelect(data);
    });
  }

  handleSearchChange(value) {
    this.handleChange(value);

    this.fetch(value);
  }

  fetch(value) {
    if(!value)
      return false;

    this.autocomplete.getPlacePredictions({ input: value, language: "en" }, (fields) => {

      if(!fields) {
        this.setState({
          options: null,
        });
        return false;
      }

      const searchResults = fields.map((field) => {
        return (
          <Select.Option key={field.description} value={field.description} placeId={field.place_id}>{field.description}</Select.Option>
        );
      });

      this.setState({
        options: searchResults
      });

    });
  }

  render() {
    return (
      <div className="location-autocomplete" style={this.props.style}>
        <div ref={(ref) => this.mapRef = ref}/>
        <Select
          style={this.props.style}
          mode="combobox"
          defaultActiveFirstOption={false}
          showArrow={false}
          filterOption={false}
          placeholder={this.props.placeholder}
          optionFilterProp="children"
          value={this.props.value}
          onChange={this.handleChange}
          onSelect={this.handleSelectChange}
          onSearch={this.handleSearchChange}
          onBlur={this.handleBlur}
          onFocus={this.handleFocus}
        >
          {this.state.options}
        </Select>
      </div>
    );
  }

}

export default Index;
