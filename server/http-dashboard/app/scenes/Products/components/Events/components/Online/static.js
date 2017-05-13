import React from 'react';
import {EVENT_TYPES} from 'services/Products';
import Base from '../Base';
import {Item} from 'components/UI';

class Static extends React.Component {

  static propTypes = {
    fields: React.PropTypes.object
  };

  render() {

    return (
      <Base.Static type={EVENT_TYPES.ONLINE} fields={this.props.fields}>
        <Base.Content>
          <Item label="Online Event" offset="small">
            <div className={`product-metadata-static-field ${!this.props.fields.name && 'no-value'}`}>
              { this.props.fields.name || 'No Value' }
            </div>
          </Item>
        </Base.Content>
      </Base.Static>
    );
  }

}

export default Static;
