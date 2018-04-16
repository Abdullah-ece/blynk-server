import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField';
import Static from './static';

class CoordinatesField extends BaseField {

  constructor(props) {
    super(props);

    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);
  }


  getPreviewValues() {
    const name = this.props.field.get('name');
    const lat = this.props.field.get('lat');
    const long = this.props.field.get('lon');

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
          <FormItem.Title style={{width: '25%'}}>Lat</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Lon</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <MetadataFormField className={`metadata-name-field-${this.props.field.get('id')}`}
                               validateOnBlur={true} name={`metaFields.${this.props.metaFieldKey}.name`} type="text" placeholder="Field Name"
                               onFocus={this.onFocus} onBlur={this.onBlur}
                               style={{width: '200%'}} validate={[
              Validation.Rules.required, Validation.Rules.metafieldName,
            ]}/>
            <MetadataFormField onFocus={this.onFocus} onBlur={this.onBlur}
                               name={`metaFields.${this.props.metaFieldKey}.lat`} type="text" placeholder="Latitude" validate={[
              Validation.Rules.latitude
            ]}/>
            <MetadataFormField onFocus={this.onFocus} onBlur={this.onBlur}
                               name={`metaFields.${this.props.metaFieldKey}.lon`} type="text" placeholder="Longitude" validate={[
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
