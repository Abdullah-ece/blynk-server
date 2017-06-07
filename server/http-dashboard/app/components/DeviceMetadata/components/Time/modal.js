import React from 'react';
import {reduxForm} from 'redux-form';
import {MetadataTime} from 'components/Form';

@reduxForm({
  form: 'deviceMetadataEdit'
})
class TimeModal extends React.Component {

  render() {
    return (
      <div>
        <MetadataTime name="time" type="text" timeFormat="HH:mm" placeholder="00:00" timestampPicker={true}/>
      </div>
    );
  }

}

export default TimeModal;
