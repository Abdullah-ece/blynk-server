import React from 'react';
import Metadata from '../../index';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField, MetadataTime as MetadataFormTime} from 'components/Form';
import {reduxForm, formValueSelector} from 'redux-form';
import {connect} from 'react-redux';
import Validation from 'services/Validation';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      from: selector(state, 'from'),
      to: selector(state, 'to'),
    }
  };
})
@reduxForm({
  touchOnChange: true
})
export default class ShiftField extends React.Component {

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
    const from = this.props.fields.from;
    const to = this.props.fields.to;

    return {
      values: {
        name: name && typeof name === 'string' ? `${name.trim()}:` : null,
        value: from && typeof from === 'string' && to && typeof to === 'string' ? `From ${from} to ${to}` : null
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
            <FormItem.Title style={{width: '50%'}}>Time Range</FormItem.Title>
            <FormItem.Title style={{width: '25%'}}>From</FormItem.Title>
            <FormItem.Title style={{width: '25%'}}>To</FormItem.Title>
          </FormItem.TitleGroup>
          <FormItem.Content>
            <Input.Group compact>
              <MetadataFormField name="name" type="text" placeholder="Field Name" style={{width: '200%'}} validate={[
                Validation.Rules.required
              ]}/>
              <MetadataFormTime name="from" type="text" timeFormat="HH:mm" placeholder="06:00"/>
              <MetadataFormTime name="to" type="text" timeFormat="HH:mm" placeholder="07:00" />
            </Input.Group>
          </FormItem.Content>
        </FormItem>
      </Metadata.Item>
    );
  }
}
