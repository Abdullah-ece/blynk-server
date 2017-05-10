import React from 'react';
import {Base} from '../../index';
import {ItemsGroup, Item, Input} from 'components/UI';
import {EVENT_TYPES} from 'services/Products';
import _ from 'lodash';

class Event extends React.Component {

  static propTypes = {
    type: React.PropTypes.string
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

  render() {

    return (
      <Base type={this.props.type}>
        <Base.Content>
          <ItemsGroup>
            <Item label={this.getLabelForType(this.props.type)} offset="normal" style={{width: '70%'}}>
              <Input placeholder="Event Name"/>
            </Item>
            <Item label="Event Code" offset="normal" style={{width: '30%'}}>
              <Input placeholder="Event code"/>
            </Item>
          </ItemsGroup>
          <Item label="Description" offset="small">
            <Input type="textarea" placeholder="Event Description (optional)" rows="3"/>
          </Item>
        </Base.Content>
        <Base.Preview>
          <Item label="Code Preview" offset="small">
            Blynk.logEvent(flush_error);
          </Item>
        </Base.Preview>
      </Base>
    );
  }

}

export default Event;
