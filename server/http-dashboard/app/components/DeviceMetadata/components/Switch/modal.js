import React from 'react';
import {Field} from 'redux-form';
import {Radio} from 'antd';

class SwitchModal extends React.Component {

  static propTypes = {
    initialValues: React.PropTypes.object
  };

  radio({input, from, to}) {
    return (
      <Radio.Group value={input.value} onChange={input.onChange}>
        <Radio value="0">{from}</Radio>
        <Radio value="1">{to}</Radio>
      </Radio.Group>
    );
  }

  render() {
    return (
      <div>
        <Field name="value"
               from={this.props.initialValues.from}
               to={this.props.initialValues.to}
               component={this.radio}/>
      </div>
    );
  }

}

export default SwitchModal;
