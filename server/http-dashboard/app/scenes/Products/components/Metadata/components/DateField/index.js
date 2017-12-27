import React from 'react';
import Metadata from '../../index';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField, MetadataDate as MetadataDateField} from 'components/Form';
import {reduxForm, formValueSelector} from 'redux-form';
import {connect} from 'react-redux';
import Validation from 'services/Validation';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      value: selector(state, 'value')
    }
  };
})
@reduxForm({
  touchOnChange: true
})
export default class DateField extends React.Component {

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

  constructor(props) {
    super(props);

    this.handleDelete = this.handleDelete.bind(this);
    this.handleClone = this.handleClone.bind(this);

  }

  getPreviewValues() {
    const name = this.props.fields.name;
    const value = this.props.fields.value;

    return {
      values: {
        name: name && typeof name === 'string' ? `${name.trim()}` : null,
        value: value && typeof value === 'string' ? value.trim() : null
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
                     onDelete={this.handleDelete}
                     onClone={this.handleClone}>
        <FormItem offset={false}>
          <FormItem.TitleGroup>
            <FormItem.Title style={{width: '50%'}}>Date</FormItem.Title>
            <FormItem.Title style={{width: '50%'}}>Value (optional)</FormItem.Title>
          </FormItem.TitleGroup>
          <FormItem.Content>
            <Input.Group compact>
              <MetadataFormField name="name" type="text" placeholder="Field Name" validate={[
                Validation.Rules.required, Validation.Rules.metafieldName,
              ]}/>
              <MetadataDateField name="value" type="text" timeFormat="MM-DD-YYYY" placeholder="Default value(optional)"/>
            </Input.Group>
          </FormItem.Content>
        </FormItem>
      </Metadata.Item>
    );
  }
}
