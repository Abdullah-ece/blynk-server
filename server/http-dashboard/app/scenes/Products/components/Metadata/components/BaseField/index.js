import React from 'react';
import Metadata from '../../index';
import Static from './static';

class BaseField extends React.Component {

  static propTypes = {
    id: React.PropTypes.number,
    fields: React.PropTypes.object,
    form: React.PropTypes.string,
    initialValues: React.PropTypes.object,
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
                     onChange={this.props.onChange.bind(this)}
                     onDelete={this.handleDelete.bind(this)}
                     onClone={this.handleClone.bind(this)}
                     validate={this.props.validate.bind(this)}
                     initialValues={this.props.initialValues}
                     fields={this.props.fields}
                     id={this.props.id}
                     form={this.props.form}>
        { this.component() }
      </Metadata.Item>
    );
  }
}

BaseField.Static = Static;

export default BaseField;
