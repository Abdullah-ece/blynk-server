import React from 'react';
import {Base} from '../../index';
import {ItemsGroup, Item, Input} from 'components/UI';
//import {EVENT_TYPES} from 'services/Products';

class Event extends React.Component {

  static propTypes = {
    type: React.PropTypes.string
  };

  render() {

    return (
      <Base type="alert">
        <Base.Content>
          <ItemsGroup>
            <Item label="Offline Event" offset="normal" style={{width: '70%'}}>
              <Input placeholder="Name"/>
            </Item>
            <Item label="Event Code" offset="normal" style={{width: '30%'}}>
              <Input placeholder="Event code"/>
            </Item>
          </ItemsGroup>
          <Item label="Description">
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
