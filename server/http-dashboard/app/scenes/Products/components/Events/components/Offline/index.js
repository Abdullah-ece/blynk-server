import React from 'react';
import moment from 'moment';
import {Base} from '../../index';
import {TimePicker} from 'antd';
import {ItemsGroup, Item, Input} from 'components/UI';
import {EVENT_TYPES} from 'services/Products';

class Offline extends React.Component {

  render() {

    const format = "HH [hrs] mm [min]";
    return (
      <Base type={EVENT_TYPES.OFFLINE}>
        <Base.Content>
          <ItemsGroup>
            <Item label="Offline Event" offset="small" style={{width: '70%'}}>
              <Input placeholder="Event Name"/>
            </Item>
            <Item label="Ignore Period" offset="small" style={{width: '30%'}}>
              <TimePicker defaultValue={moment('00:00', 'HH:mm')} format={format} style={{width: '100%'}}/>
            </Item>
          </ItemsGroup>
        </Base.Content>
      </Base>
    );
  }

}

export default Offline;
