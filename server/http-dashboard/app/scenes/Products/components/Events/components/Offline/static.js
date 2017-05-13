import React from 'react';
import {EVENT_TYPES} from 'services/Products';
import Base from '../Base';
import {Item, ItemsGroup} from 'components/UI';

class Static extends React.Component {

  static propTypes = {
    fields: React.PropTypes.object
  };

  render() {

    return (
      <Base.Static type={EVENT_TYPES.OFFLINE}>
        <Base.Content>
          <ItemsGroup>
            <Item label="Offline Event" offset="small" style={{width: '65%'}}>
              <div className={`product-metadata-static-field ${!this.props.fields.name && 'no-value'}`}>
                { this.props.fields.name || 'No Value' }
              </div>
            </Item>
            <Item label="Ignore Period" offset="small" style={{width: '45%'}}>
              <div className="product-metadata-static-field">
                { this.props.fields.ignorePeriod || '0 hrs 0 min' }
              </div>
            </Item>
          </ItemsGroup>
        </Base.Content>
      </Base.Static>
    );
  }

}

export default Static;
