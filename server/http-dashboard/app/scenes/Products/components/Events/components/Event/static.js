import React from 'react';
import {Base} from '../../index';
import {ItemsGroup, Item} from 'components/UI';
import {EVENT_TYPES} from 'services/Products';
import _ from 'lodash';

class Event extends React.Component {

  static propTypes = {
    type: React.PropTypes.string,
    fields: React.PropTypes.any,
  };

  constructor(props) {
    super(props);

    if (_.values(EVENT_TYPES).indexOf(props.type) === -1) {
      throw Error('Wrong props.type for Event');
    }
  }

  getLabelForType(type) {
    if (EVENT_TYPES.INFO === type) {
      return "Information event";
    }

    if (EVENT_TYPES.WARNING === type) {
      return "Warning event";
    }

    if (EVENT_TYPES.CRITICAL === type) {
      return "Critical event";
    }
  }

  getPreviewProps() {
    return {
      isValid: true
    };
  }

  render() {

    return (
      <Base.Static type={this.props.type} fields={this.props.fields}>
        <Base.Content>
          <ItemsGroup>
            <Item label={this.getLabelForType(this.props.type)} offset="normal" style={{width: '55%'}}>
              <div className={`product-metadata-static-field ${!this.props.fields.name && 'no-value'}`}>
                { this.props.fields.name || 'No Value' }
              </div>
            </Item>
            <Item label="Event Code" offset="normal" style={{width: '45%'}}>
              <div className={`product-metadata-static-field ${!this.props.fields.eventCode && 'no-value'}`}>
                { this.props.fields.eventCode || 'No Value' }
              </div>
            </Item>
          </ItemsGroup>
          <Item label="Description" offset="small">
            <div className={`product-metadata-static-field ${!this.props.fields.description && 'no-value'}`}>
              { this.props.fields.description || 'No Value' }
            </div>
          </Item>
        </Base.Content>
        <Base.Preview {...this.getPreviewProps()} valid={
          <Item label="Code Preview" offset="small">
            Blynk.logEvent({ this.props.fields.eventCode })
          </Item>
        } invalid={
          <Item label="Code Preview" offset="small">
            <div className="product-metadata-item--preview--unavailable">No Preview available</div>
          </Item>
        }/>
      </Base.Static>
    );
  }

}

export default Event;
