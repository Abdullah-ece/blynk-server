import React from 'react';
import {reduxForm} from 'redux-form';
import {MetadataField} from 'components/Form';

@reduxForm({
  form: 'deviceMetadataEdit'
})
class TextModal extends React.Component {

  render() {
    return (
      <div>
        <MetadataField placeholder="Value" name="value"/>
      </div>
    );
  }

}

export default TextModal;
