import React from 'react';
import {reduxForm} from 'redux-form';
import {ItemsGroup, Item} from 'components/UI';
import {MetadataTime} from 'components/Form';

@reduxForm({
  form: 'deviceMetadataEdit'
})
class RangeModal extends React.Component {

  render() {
    return (
      <div>
        <ItemsGroup>
          <Item label="From" offset="normal">
            <MetadataTime name="from" type="text" timeFormat="HH:mm" placeholder="00:00" style={{width: '100%'}}/>
          </Item>
          <Item label="To" offset="normal">
            <MetadataTime name="to" type="text" timeFormat="HH:mm" placeholder="00:00" style={{width: '100%'}}/>
          </Item>
        </ItemsGroup>
      </div>
    );
  }

}

export default RangeModal;
