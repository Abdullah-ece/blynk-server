import React from 'react';
import {EVENT_TYPES} from 'services/Products';
import Base from '../Base';
import {Item, ItemsGroup} from 'components/UI';
import moment from 'moment';
import FieldStub from 'scenes/Products/components/FieldStub';

class Static extends React.Component {

  static propTypes = {
    fields: React.PropTypes.object
  };

  ignorePeriod() {
    let hours, minutes;
    if (!isNaN(Number(this.props.fields.ignorePeriod))) {
      const time = moment.duration(this.props.fields.ignorePeriod, 'seconds');
      hours = time.hours();
      minutes = time.minutes();
    }

    return moment().hours(hours || 0).minutes(minutes || 0).format('HH [hrs] mm [min]');
  }

  render() {

    return (
      <Base.Static type={EVENT_TYPES.OFFLINE} fields={this.props.fields}>
        <Base.Content>
          <ItemsGroup>
            <Item label="Offline Event" offset="small" style={{width: '65%'}}>
              <FieldStub>{this.props.fields.name}</FieldStub>
            </Item>
            <Item label="Ignore Period" offset="small" style={{width: '45%'}}>
              <FieldStub>{ this.ignorePeriod() || '0 hrs 0 min' }</FieldStub>
            </Item>
          </ItemsGroup>
        </Base.Content>
      </Base.Static>
    );
  }

}

export default Static;
