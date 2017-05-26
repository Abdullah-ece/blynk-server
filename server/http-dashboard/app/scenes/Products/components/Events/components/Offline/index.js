import React from 'react';
import moment from 'moment';
import {Base} from '../../index';
import {TimePicker} from 'antd';
import {ItemsGroup, Item, Input} from 'components/UI';
import {EVENT_TYPES} from 'services/Products';
import {Field} from 'redux-form';
import Static from './static';

class Offline extends React.Component {

  static propTypes = {
    form: React.PropTypes.string,
    initialValues: React.PropTypes.object,
    onChange: React.PropTypes.func,
    onDelete: React.PropTypes.func,
    validate: React.PropTypes.func
  };

  ignorePeriod(props) {
    const format = "HH [hrs] mm [min]";

    const onChange = (value) => {
      props.input.onChange(value);
    };

    return (
      <TimePicker
        format={format}
        style={{width: '100%'}}
        onChange={onChange}
        defaultValue={moment('00:00', 'HH:mm')}
        value={moment(props.input.value)}
      />
    );
  }

  render() {

    return (
      <Base type={EVENT_TYPES.OFFLINE} form={this.props.form} initialValues={this.props.initialValues}
            onChange={this.props.onChange}
            validate={this.props.validate}
            onDelete={this.props.onDelete}>
        <Base.Content>
          <ItemsGroup>
            <Item label="Offline Event" offset="small">
              <Input validateOnBlur={true} name="name" placeholder="Event Name"
                     style={{width: '55%'}}/>
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


Offline.Static = Static;

export default Offline;
