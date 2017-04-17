import React from 'react';
import Metadata from '../../index';
import FormItem from 'components/FormItem';
import {Input, Form} from 'antd';
import {formHasError, getFormFirstError} from 'services/Form';

class TextField extends React.Component {

  static propTypes = {
    id: React.PropTypes.number,
    form: React.PropTypes.object
  };

  getPreviewValues() {
    const name = this.props.form.getFieldValue('name');
    const value = this.props.form.getFieldValue('value');
    return {
      values: {
        name: name ? `${name}:` : null,
        value: value
      },
      isTouched: this.props.form.isFieldsTouched(),
      errors: this.props.form.getFieldsError()
    };
  }

  render() {

    const {getFieldDecorator} = this.props.form;
    const fieldsErrors = this.props.form.getFieldsError();

    return (
      <Metadata.Item preview={this.getPreviewValues()}>
        <Form>
          <FormItem offset={false}>
            <FormItem.TitleGroup>
              <FormItem.Title style={{width: '50%'}}>String</FormItem.Title>
              <FormItem.Title style={{width: '50%'}}>Value (optional)</FormItem.Title>
            </FormItem.TitleGroup>
            <FormItem.Content>
              <Form.Item validateStatus={formHasError(fieldsErrors) ? 'error' : 'success'}
                         help={getFormFirstError(fieldsErrors)} required={true}>
                <Input.Group compact>
                  {getFieldDecorator('name', {rules: [{required: true, message: 'Name is required'}]})(
                    <Input placeholder="Field name" style={{width: '50%'}}/>
                  )}
                  {getFieldDecorator('value')(
                    <Input placeholder="Default value" style={{width: '50%'}}/>
                  )}
                </Input.Group>
              </Form.Item>
            </FormItem.Content>
          </FormItem>
        </Form>
      </Metadata.Item>
    );
  }
}

export default Form.create({
  onValuesChange: (props, values) => {
    if (props.onChange && props.id) {
      props.onChange(props.id, values);
    }
  }
})(TextField);
