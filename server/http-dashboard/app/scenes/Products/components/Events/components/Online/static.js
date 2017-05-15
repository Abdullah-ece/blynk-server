import React from 'react';
import {EVENT_TYPES} from 'services/Products';
import Base from '../Base';
import {Item} from 'components/UI';
import FieldStub from 'scenes/Products/components/FieldStub';

class Static extends React.Component {

  static propTypes = {
    fields: React.PropTypes.object
  };

  render() {

    return (
      <Base.Static type={EVENT_TYPES.ONLINE} fields={this.props.fields}>
        <Base.Content>
          <Item label="Online Event" offset="small">
            <FieldStub>{ this.props.fields.name }</FieldStub>
          </Item>
        </Base.Content>
      </Base.Static>
    );
  }

}

export default Static;
