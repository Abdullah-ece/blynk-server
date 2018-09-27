import React from 'react';
import FormItem from 'components/FormItem';
import {Row, Col, Input} from 'antd';
import {MetadataField as MetadataFormField, Field} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField/index';
import Static from './static';

class NumberField extends BaseField {

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
      <div>

        <Field title="Number"
               className={`metadata-name-field-${this.props.field.get('id')} normal-offset`}
               onFocus={this.onFocus}
               onBlur={this.onBlur}
               validateOnBlur={true}
               name={`metaFields.${this.props.metaFieldKey}.name`}
               placeholder="Field Name"
               validate={[Validation.Rules.required]}
        />

        <Row>
          <Col span={10}>
            <Field title="Default Value"
                   className={`metadata-name-field-${this.props.field.get('id')} normal-offset`}
                   onFocus={this.onFocus}
                   onBlur={this.onBlur}
                   validateOnBlur={true}
                   name={`metaFields.${this.props.metaFieldKey}.value`}
                   placeholder="Optional"
            />
          </Col>
          <Col span={6} offset={1}>
            <FormItem offset={false}>
              <FormItem.TitleGroup>
                <FormItem.Title style={{width: '100%'}}>Min/Max values (optional)</FormItem.Title>
              </FormItem.TitleGroup>
              <FormItem.Content>
                <Input.Group compact>

                  <MetadataFormField style={{width: '50%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                                     name={`metaFields.${this.props.metaFieldKey}.min`} type="text" placeholder="Min" validate={[
                    Validation.Rules.number
                  ]}/>
                  <MetadataFormField style={{width: '50%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                                     name={`metaFields.${this.props.metaFieldKey}.max`} type="text" placeholder="Max" validate={[
                    Validation.Rules.number
                  ]}/>
                </Input.Group>
              </FormItem.Content>
            </FormItem>

          </Col>
          <Col span={6} offset={1}>
            <Field title="Step"
                   className={`metadata-name-field-${this.props.field.get('id')} normal-offset`}
                   onFocus={this.onFocus}
                   onBlur={this.onBlur}
                   validateOnBlur={true}
                   name={`metaFields.${this.props.metaFieldKey}.step`}
                   placeholder="Optional"
            />
          </Col>
        </Row>

      </div>
    );
  }
}

NumberField.Static = Static;

export default NumberField;
