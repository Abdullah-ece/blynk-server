import React from 'react';
import {reduxForm} from 'redux-form';
import {Input} from 'antd';
import {MetadataTime} from 'components/Form';

@reduxForm({
  form: 'deviceMetadataEdit'
})
class TimeModal extends React.Component {

  render() {
    return (
      <div>
        <Input.Group compact>
          <MetadataTime name="from" type="text" timeFormat="HH:mm" placeholder="00:00"/>
          <MetadataTime name="to" type="text" timeFormat="HH:mm" placeholder="00:00"/>
        </Input.Group>
      </div>
    );
  }

}

export default TimeModal;
