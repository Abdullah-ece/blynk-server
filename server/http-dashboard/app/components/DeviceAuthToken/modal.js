import React        from 'react';
import {
  Item,
  Input
}                   from 'components/UI';
import Validation   from 'services/Validation';

class DeviceAuthTokenModal extends React.Component {
  render() {
    const validationRules = [Validation.Rules.mustEqual(32)];
    let validateOnBlur = false;

    return (
      <div>
        <Item>
          <Input validateOnBlur={validateOnBlur}
                 placeholder="Value" name="value"
                 validate={validationRules}/>
        </Item>

      </div>
    );
  }

}

export default DeviceAuthTokenModal;
