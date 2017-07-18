import React from 'react';
import {Item} from 'components/UI';
import {MetadataTime} from 'components/Form';

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
