import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import BaseField from '../BaseField';
import classnames from 'classnames';

export default class CoordinatesField extends BaseField.Static {

  getPreviewValues() {
    const name = this.props.name;
    const lat = this.props.lat;
    const long = this.props.lon;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}:` : null,
      value: long && lat ? `${lat}, ${long}` : null
    };
  }

  component() {

    const longClassNames = classnames({
      'product-metadata-static-field': true,
      'no-value': !this.props.value
    });

    const latClassNames = classnames({
      'product-metadata-static-field': true,
      'no-value': !this.props.value
    });

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Coordinates</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Lat (optional)</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Long (optional)</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <Input.Group compact>
              <div className="product-metadata-static-field">
                {this.props.name}
              </div>
              <div className={latClassNames}>
                {this.props.lat || this.DEFAULT_VALUE}
              </div>
              <div className={longClassNames}>
                {this.props.lon || this.DEFAULT_VALUE}
              </div>
            </Input.Group>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}
