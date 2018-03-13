import React from 'react';
import {MetadataField} from 'components/Form';
import Validation from 'services/Validation';

class NumberModal extends React.Component {

  render() {
    return (
      <div>
        <MetadataField maxLength={15} placeholder="Value" name="value"
                       validate={[Validation.Rules.number, Validation.Rules.required]}/>
      </div>
    );
  }

}

export default NumberModal;
