import React from 'react';
import InfoForm from './components/InfoForm';

class Info extends React.Component {

  static propTypes = {
    onChange: React.PropTypes.func,
    values: React.PropTypes.object
  };

  constructor(props) {
    super(props);

    this.invalid = false;

    this.onChange = this.onChange.bind(this);

  }

  onChange(values) {
    this.props.onChange(values);
  }

  getInitialValues() {
    const values = {
      boardType: this.props.values.boardType,
      connectionType: this.props.values.connectionType,
      name: this.props.values.name,
      description: this.props.values.description,
      logoUrl: this.props.values.logoUrl
    };
    return values;
  }

  render() {

    return (
      <InfoForm onChange={this.onChange} initialValues={this.getInitialValues()}/>
    );
  }
}

export default Info;
