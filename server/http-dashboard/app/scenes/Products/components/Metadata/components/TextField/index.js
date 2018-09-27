import React from 'react';
import {Form, Row, Col} from 'antd';
import {Field} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField/index';
import Static from './static';

class TextField extends BaseField {

  constructor(props) {
    super(props);

    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);

  }

  getPreviewValues() {

    const name = this.props.field.get('name');
    const value = this.props.field.get('value');

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && typeof value === 'string' ? value.trim() : null
    };
  }

  component() {
    return (
      <Row>
        <Col span={24}>
          <Form>
            <Field title="String"
                   className={`metadata-name-field-${this.props.field.get('id')} normal-offset`}
                   onFocus={this.onFocus}
                   onBlur={this.onBlur}
                   validateOnBlur={true}
                   name={`metaFields.${this.props.metaFieldKey}.name`}
                   placeholder="Field Name"
                   validate={[Validation.Rules.required]}
            />

            <Field title="Default Value"
                   onFocus={this.onFocus}
                   onBlur={this.onBlur}
                   validateOnBlur={true}
                   name={`metaFields.${this.props.metaFieldKey}.value`}
                   placeholder="Optional"
            />
          </Form>
        </Col>
      </Row>
    );
  }

}

TextField.Static = Static;
export default TextField;
