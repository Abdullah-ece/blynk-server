import React from 'react';
import {MetadataField} from 'components/Form';
import Validation from 'services/Validation';

class TextModal extends React.Component {

  static propTypes = {
    isDeviceOwner: React.PropTypes.bool
  };

  render() {
    const validationRules = [Validation.Rules.required];
    let validateOnBlur = false;

    if (this.props.isDeviceOwner) {
      validationRules.push(Validation.Rules.email);
      validateOnBlur = true;
    }

    return (
      <div>
        <MetadataField validateOnBlur={validateOnBlur}
                       placeholder="Value" name="value"
                       validate={validationRules}/>
      </div>
    );
  }

}

export default TextModal;
