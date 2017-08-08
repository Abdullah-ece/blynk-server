import React        from 'react';
import _            from 'lodash';
import {
  MetadataSelect
}                   from 'components/Form';
import {
  Item,
  Input
}                   from 'components/UI';
import Validation   from 'services/Validation';
import Timezones    from 'services/timeszones';

class TextModal extends React.Component {

  static propTypes = {
    isDeviceOwner: React.PropTypes.bool,
    isTimezoneOfDevice: React.PropTypes.bool,
  };

  render() {
    const validationRules = [Validation.Rules.required];
    let validateOnBlur = false;

    if (this.props.isDeviceOwner) {
      validationRules.push(Validation.Rules.email);
      validateOnBlur = true;
    }

    const timezones = _.map(Timezones, (value, key) => {
      return {
        key: key,
        value: value
      };
    });

    return (
      <div>
        { this.props.isDeviceOwner && (
          <Item label="E-mail">
            <Input validateOnBlur={validateOnBlur}
                   placeholder="E-mail" name="value"
                   validate={validationRules}/>
          </Item>
        )}

        { this.props.isTimezoneOfDevice && (
          <Item>
            <MetadataSelect displayError={false} name="value" values={timezones}
                            placeholder="Choose timezone"/>
          </Item>
        )}

        { !this.props.isDeviceOwner && !this.props.isTimezoneOfDevice && (
          <Item>
            <Input validateOnBlur={validateOnBlur}
                   placeholder="Value" name="value"
                   validate={validationRules}/>
          </Item>
        )}

      </div>
    );
  }

}

export default TextModal;
