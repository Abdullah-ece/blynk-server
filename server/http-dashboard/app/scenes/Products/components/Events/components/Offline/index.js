import React from 'react';
import moment from 'moment';
import {Base} from '../../index';
import {TimePicker} from 'antd';
import {ItemsGroup, Item, Input} from 'components/UI';
import {EVENT_TYPES} from 'services/Products';
import {Field} from 'redux-form';

class Offline extends React.Component {

  static propTypes = {
    form: React.PropTypes.object,
    initialValues: React.PropTypes.object
  };

  ignorePeriod(props) {
    const format = "HH [hrs] mm [min]";

    return (
      <TimePicker
        format={format}
        style={{width: '100%'}}
        onChange={props.input.onChange}
        defaultValue={moment('00:00', 'HH:mm')}
        value={moment(props.input.value, 'HH:mm')}
      />
    );
  }

  render() {

    return (
      <Base type={EVENT_TYPES.OFFLINE} form={this.props.form} initialValues={this.props.initialValues}>
        <Base.Content>
          <ItemsGroup>
            <Item label="Offline Event" offset="small">
              <Input name="name" placeholder="Event Name" style={{width: '55%'}}/>
            </Item>
            <Item label="Ignore Period" offset="small" style={{width: '45%'}}>
              <Field name="ignorePeriod" component={this.ignorePeriod}/>
            </Item>
          </ItemsGroup>
        </Base.Content>
      </Base>
    );
  }

}

export default Offline;
