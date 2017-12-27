import React from 'react';
import Metadata from '../../index';
import Static from './static';
import _ from 'lodash';

class BaseField extends React.PureComponent {

  static propTypes = {
    id: React.PropTypes.number,
    fields: React.PropTypes.object,
    field: React.PropTypes.object,
    form: React.PropTypes.string,
    initialValues: React.PropTypes.object,
    tools: React.PropTypes.bool,
    pristine: React.PropTypes.bool,
    invalid: React.PropTypes.bool,
    anyTouched: React.PropTypes.bool,
    onDelete: React.PropTypes.func,
    onChange: React.PropTypes.func,
    onClone: React.PropTypes.func,
    validate: React.PropTypes.func,
    isUnique: React.PropTypes.func,
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

  shouldComponentUpdate(nextProps, nextState) {
    return this.state.isFocused !== nextState.isFocused || !(_.isEqual(this.props.field, nextProps.field)) || !(_.isEqual(this.props.fields, nextProps.fields)) || !(_.isEqual(this.state, nextState));
  }

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
                     onChange={this.props.onChange}
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
