import React from 'react';
import {reduxForm} from 'redux-form';
import {Input} from 'antd';
import {MetadataField} from 'components/Form';
import Validation from 'services/Validation';

@reduxForm({
  form: 'deviceMetadataEdit'
})
class RangeModal extends React.Component {

  render() {
    return (
      <div>
        <Input.Group compact>
          <MetadataField name="value" type="text" placeholder="Value" validate={[
            Validation.Rules.number
          ]}/>
        </Input.Group>
      </div>
    );
  }

}

export default RangeModal;
