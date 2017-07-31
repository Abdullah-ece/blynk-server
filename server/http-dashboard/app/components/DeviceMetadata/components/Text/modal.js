import React from 'react';
import {Item, Input} from 'components/UI';
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
        { this.props.isDeviceOwner && (
          <Item label="E-mail">
            <Input validateOnBlur={validateOnBlur}
                   placeholder='E-mail' name="value"
                   validate={validationRules}/>
          </Item>
        )}

        { !this.props.isDeviceOwner && (
          <Input validateOnBlur={validateOnBlur}
                 placeholder='Value' name="value"
                 validate={validationRules}/>
        )}

      </div>
    );
  }

}

export default TextModal;
