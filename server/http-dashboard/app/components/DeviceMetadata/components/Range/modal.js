import React from 'react';
import {ItemsGroup, Item} from 'components/UI';
import {MetadataTime} from 'components/Form';

class RangeModal extends React.Component {

  render() {
    return (
      <div>
        <ItemsGroup>
          <Item label="From">
            <MetadataTime name="from" type="text" timeFormat="HH:mm" placeholder="00:00" style={{width: '100%'}}/>
          </Item>
          <Item label="To">
            <MetadataTime name="to" type="text" timeFormat="HH:mm" placeholder="00:00" style={{width: '100%'}}/>
          </Item>
        </ItemsGroup>
      </div>
    );
  }

}

export default RangeModal;
