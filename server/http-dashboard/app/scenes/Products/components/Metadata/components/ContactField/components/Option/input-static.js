import React from 'react';
import {Form} from 'components/UI';
import Default from './default';

export default class Input extends Default.Static {

  static propTypes = {
    placeholder: React.PropTypes.string,
    isChecked: React.PropTypes.bool
  };

  DEFAULT_VALUE = 'No Value';

  render() {
    return (
      <Form.Items layout="inline">
        <Form.Item className="contact-field-static">
          {this.checkbox(Object.assign({}, this.props, {placeholder: ''}))}
        </Form.Item>
        <Form.Item className="contact-field-static">
          <div className={`product-metadata-static-field ${!this.props.value && 'no-value'}`}>
            { this.props.value || this.DEFAULT_VALUE }
          </div>
        </Form.Item>
      </Form.Items>
    );
  }

}
