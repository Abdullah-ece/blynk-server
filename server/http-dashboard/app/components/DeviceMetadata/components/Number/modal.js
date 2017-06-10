import React from 'react';
import {reduxForm} from 'redux-form';
import {MetadataField} from 'components/Form';
import Validation from 'services/Validation';

@reduxForm({
  form: 'deviceMetadataEdit'
})
class NumberModal extends React.Component {

  render() {
    return (
      <div>
        <MetadataField placeholder="Value" name="value"
                       validate={[Validation.Rules.number, Validation.Rules.required]}/>
      </div>
    );
  }

}

export default NumberModal;
