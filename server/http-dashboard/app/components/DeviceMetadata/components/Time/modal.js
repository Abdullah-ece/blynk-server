import React from 'react';
import {reduxForm} from 'redux-form';
import {Item} from 'components/UI';
import {MetadataTime} from 'components/Form';

@reduxForm({
  form: 'deviceMetadataEdit'
})
class TimeModal extends React.Component {

  render() {
    return (
      <div>
        <Item label="Time">
          <MetadataTime name="time" type="text" timeFormat="HH:mm" placeholder="00:00" timestampPicker={true}
                        style={{width: '100%'}}/>
        </Item>

      </div>
    );
  }

}

export default TimeModal;
