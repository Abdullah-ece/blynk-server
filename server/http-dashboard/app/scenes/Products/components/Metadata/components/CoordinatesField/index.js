import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField} from 'components/Form';
import {formValueSelector} from 'redux-form';
import {connect} from 'react-redux';
import Validation from 'services/Validation';
import BaseField from '../BaseField';
import Static from './static';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      lat: selector(state, 'lat'),
      lon: selector(state, 'lon'),
    }
  };
})
class CoordinatesField extends BaseField {

  getPreviewValues() {
    const name = this.props.fields.name;
    const lat = this.props.fields.lat;
    const long = this.props.fields.lon;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: long && lat ? `${lat}, ${long}` : null
    };
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Coordinates</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Lat (optional)</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Long (optional)</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <MetadataFormField name="name" type="text" placeholder="Field Name" style={{width: '200%'}} validate={[
              Validation.Rules.required, Validation.Rules.metafieldName,
            ]}/>
            <MetadataFormField name="lat" type="text" placeholder="Latitude" validate={[
              Validation.Rules.latitude
            ]}/>
            <MetadataFormField name="lon" type="text" placeholder="Longitude" validate={[
              Validation.Rules.longitude
            ]}/>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

CoordinatesField.Static = Static;
export default CoordinatesField;
