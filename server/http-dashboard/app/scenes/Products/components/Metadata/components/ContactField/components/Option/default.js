import React from 'react';
import {Form} from 'components/UI';
import {Field} from 'redux-form';
import Checkbox from './checkbox';
import Static from './default-static';

class Default extends React.Component {

  static propTypes = {
    placeholder: React.PropTypes.string,
    prefix: React.PropTypes.string
  };

  capitalizePrefix(name) {
    return name[0].toUpperCase() + name.substr(1);
  }

  shouldComponentUpdate(nextProps) {
    return this.props.isChecked !== nextProps.isChecked ||
      this.props.value !== nextProps.value ||
      this.props.placeholder !== nextProps.placeholder ||
      this.props.prefix !== nextProps.prefix
  }

  render() {
    return (
      <Form.Items offset="small">
        <Form.Item>
          <Field name={`is${this.capitalizePrefix(this.props.prefix)}Enabled`}
                 placeholder={this.props.placeholder}
                 component={Checkbox}/>
        </Form.Item>
      </Form.Items>
    );
  }

}

Default.Static = Static;
export default Default;
