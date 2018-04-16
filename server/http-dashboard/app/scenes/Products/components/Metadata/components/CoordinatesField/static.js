import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import BaseField from '../BaseField';
import FieldStub from 'scenes/Products/components/FieldStub';

export default class CoordinatesField extends BaseField.Static {

  getPreviewValues() {
    const name = this.props.name;
    const lat = this.props.lat;
    const long = this.props.lon;

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
            <Input.Group compact>
              <FieldStub style={{width: '50%'}}>
                {this.props.name}
              </FieldStub>
              <FieldStub style={{width: '25%'}}>
                {this.props.lat}
              </FieldStub>
              <FieldStub style={{width: '25%'}}>
                {this.props.lon}
              </FieldStub>
            </Input.Group>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}
