import React from 'react';
import {reduxForm} from 'redux-form';
import {ItemsGroup, Item, Input} from 'components/UI';
import Validation from 'services/Validation';

@reduxForm({
  form: 'deviceMetadataEdit'
})
class CoordinatesModal extends React.Component {

  render() {
    return (
      <div>
        <ItemsGroup>
          <Item label="Latitude">
            <Input placeholder="Latitude" name="lat" validate={[Validation.Rules.latitude, Validation.Rules.required]}
                   style={{width: '100%'}}/>
          </Item>
          <Item label="Longitude">
            <Input placeholder="Longitude" name="lon" validate={[Validation.Rules.longitude, Validation.Rules.required]}
                   style={{width: '100%'}}/>
          </Item>
        </ItemsGroup>
      </div>
    );
  }

}

export default CoordinatesModal;
