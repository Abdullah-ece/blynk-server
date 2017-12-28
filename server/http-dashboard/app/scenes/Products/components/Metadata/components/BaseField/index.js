import React from 'react';
import Metadata from '../../index';
import Static from './static';
import PropTypes from 'prop-types';
// import _ from 'lodash';

class BaseField extends React.PureComponent {

  static propTypes = {
    id: PropTypes.number,
    fields: PropTypes.object,
    field: PropTypes.object,
    form: PropTypes.string,
    initialValues: PropTypes.object,
    tools: PropTypes.bool,
    pristine: PropTypes.bool,
    invalid: PropTypes.bool,
    anyTouched: PropTypes.bool,
    onDelete: PropTypes.func,
    onChange: PropTypes.func,
    onClone: PropTypes.func,
    validate: PropTypes.func,
    isUnique: PropTypes.func,

    metaFieldKey: PropTypes.oneOfType([
      React.PropTypes.string,
      React.PropTypes.number
    ])
  };

  constructor(props) {
    super(props);

    if (typeof this.component !== 'function') {
      throw new Error('Object nested from BaseField should have component method');
    }

    this.handleDelete = this.handleDelete.bind(this);
    this.handleClone = this.handleClone.bind(this);

  }

  state = {
    isFocused: false
  };

  onFocus() {
    this.setState({
      isFocused: true
    });
  }

  onBlur() {
    this.setState({
      isFocused: false
    });
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
      <Metadata.Item preview={this.getPreviewValues()}
                     metaFieldKey={this.props.metaFieldKey}
                     // onChange={this.props.onChange}
                     onDelete={this.handleDelete}
                     onClone={this.handleClone}
                     validate={this.props.validate}
                     initialValues={this.props.initialValues}
                     tools={this.props.tools !== false}
                     fields={this.props.fields}
                     field={this.props.field}
                     id={this.props.id}
                     form={this.props.form}
                     isActive={this.state.isFocused}>
        { this.component() }
      </Metadata.Item>
    );
  }
}

BaseField.Static = Static;

export default BaseField;
