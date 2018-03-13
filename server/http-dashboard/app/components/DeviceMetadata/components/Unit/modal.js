import React from 'react';
import {ItemsGroup, Item, Input} from 'components/UI';
import Validation from 'services/Validation';

class UnitModal extends React.Component {

  render() {
    return (
      <div>
        <ItemsGroup>
          <Item label="Value">
            <Input name="value" type="text" placeholder="Value" validate={[
              Validation.Rules.number,
              Validation.Rules.required
            ]} style={{width: '100%'}}/>
          </Item>
        </ItemsGroup>
      </div>
    );
  }

}

export default UnitModal;
