import React from 'react';
import Metadata from '../../index';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField} from 'components/Form';
import {reduxForm, formValueSelector} from 'redux-form';
import {connect} from 'react-redux';
import Validation from 'services/Validation';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      on: selector(state, 'on'),
      off: selector(state, 'off'),
    }
  };
})
@reduxForm({
  touchOnChange: true
})
export default class SwitchField extends React.Component {

  static propTypes = {
    id: React.PropTypes.number,
    fields: React.PropTypes.object,
    pristine: React.PropTypes.bool,
    invalid: React.PropTypes.bool,
    anyTouched: React.PropTypes.bool,
    onDelete: React.PropTypes.func,
    onClone: React.PropTypes.func,
    isUnique: React.PropTypes.func
  };

  getPreviewValues() {
    const name = this.props.fields.name;
    const on = this.props.fields.on;
    const off = this.props.fields.off;

    return {
      values: {
        name: name && typeof name === 'string' ? `${name.trim()}` : null,
        value: on && typeof on === 'string' && off && typeof off === 'string' ? `From ${on} to ${off}` : null
      },
      isTouched: this.props.anyTouched,
      invalid: this.props.invalid
    };
  }

  handleDelete() {
    if (this.props.onDelete)
      this.props.onDelete(this.props.id);
  }

  handleClone() {
    if (this.props.onClone)
      this.props.onClone(this.props.id);
  }

  render() {

    return (
      <Metadata.Item touched={this.props.anyTouched} preview={this.getPreviewValues()}
                     onDelete={this.handleDelete.bind(this)}
                     onClone={this.handleClone.bind(this)}>
        <FormItem offset={false}>
          <FormItem.TitleGroup>
            <FormItem.Title style={{width: '50%'}}>Switch</FormItem.Title>
            <FormItem.Title style={{width: '25%'}}>Option A</FormItem.Title>
            <FormItem.Title style={{width: '25%'}}>Option B</FormItem.Title>
          </FormItem.TitleGroup>
          <FormItem.Content>
            <Input.Group compact>
              <MetadataFormField name="name" type="text" placeholder="Switch Name" style={{width: '200%'}} validate={[
                Validation.Rules.required, Validation.Rules.metafieldName,
              ]}/>
              <MetadataFormField name="on" type="text" placeholder="ON" validate={[
                Validation.Rules.required
              ]}/>
              <MetadataFormField name="off" type="text" placeholder="OFF" validate={[
                Validation.Rules.required
              ]}/>
            </Input.Group>
          </FormItem.Content>
        </FormItem>
      </Metadata.Item>
    );
  }
}
