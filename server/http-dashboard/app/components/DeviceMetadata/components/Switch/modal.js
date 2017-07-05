import React from 'react';
import {reduxForm, Field} from 'redux-form';
import {Radio} from 'antd';

@reduxForm({
  form: 'deviceMetadataEdit'
})
class SwitchModal extends React.Component {

  static propTypes = {
    initialValues: React.PropTypes.object
  };

  radio({input, from, to}) {
    return (
      <Radio.Group value={input.value} onChange={input.onChange}>
        <Radio value={from}>{from}</Radio>
        <Radio value={to}>{to}</Radio>
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
