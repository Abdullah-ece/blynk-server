import React from 'react';
import moment from 'moment';
import {Base} from '../../index';
import {TimePicker} from 'antd';
import {ItemsGroup, Item, Input} from 'components/UI';
import {EVENT_TYPES} from 'services/Products';
import {Field} from 'redux-form';
import Static from './static';
import PropTypes from 'prop-types';
import {Map} from 'immutable';

class Offline extends React.Component {

  static propTypes = {
    onDelete: PropTypes.func,
    field: PropTypes.instanceOf(Map),
  };

  constructor(props) {
    super(props);

    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);
  }

  state = {
    isFocused: false
  };

  onFocus() {
    this.setState({
      isFocused: true
    });
  }

  onBlur() {
    this.setState({
      isFocused: false
    });
  }

  ignorePeriod(props) {

    const getTime = (milliseconds) => (
      moment().set({hours: 0, minutes: 0, seconds: 0, milliseconds: milliseconds})
    );

    const format = "HH [hrs] mm [min]";

    const onChange = (value) => {
      props.input.onChange(value.diff(getTime(0)));
    };

    return (
      <TimePicker
        format={format}
        style={{width: '100%'}}
        onChange={onChange}
        value={getTime(props.input.value)}
      />
    );
  }

  render() {

    return (
      <Base type={EVENT_TYPES.OFFLINE}
            field={this.props.field}
            onDelete={this.props.onDelete}
            isActive={this.state.isFocused}>
        <Base.Content>
          <ItemsGroup>
            <Item label="Offline Event" offset="small">
              <Input onFocus={this.onFocus} onBlur={this.onBlur}
                     validateOnBlur={true} name={`${this.props.field.get('fieldPrefix')}.name`} placeholder="Event Name"
                     style={{width: '55%'}}/>
            </Item>
            <Item label="Ignore Period" offset="small" style={{width: '45%'}}>
              <Field name={`${this.props.field.get('fieldPrefix')}.ignorePeriod`} component={this.ignorePeriod}/>
            </Item>
          </ItemsGroup>
        </Base.Content>
      </Base>
    );
  }

}


Offline.Static = Static;

export default Offline;
