import React from 'react';
import {reduxForm} from 'redux-form';
import {MetadataField} from 'components/Form';
import Validation from 'services/Validation';
import {Input} from 'antd';

@reduxForm({
  form: 'deviceMetadataEdit'
})
class CoordinatesModal extends React.Component {

  render() {
    return (
      <div>
        <Input.Group compact>
          <MetadataField placeholder="Latitude" name="lat" validate={[Validation.Rules.latitude]}/>
          <MetadataField placeholder="Longitude" name="lon" validate={[Validation.Rules.longitude]}/>
        </Input.Group>
      </div>
    );
  }

}

export default CoordinatesModal;
