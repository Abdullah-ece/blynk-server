import React from 'react';
import {Base} from '../../index';
import {Item, Input} from 'components/UI';
import {EVENT_TYPES} from 'services/Products';
import Static from './static';
import {Map} from 'immutable';
import PropTypes from 'prop-types';

class Online extends React.Component {

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

  render() {
    return (
      <Base type={EVENT_TYPES.ONLINE}
            field={this.props.field}
            onDelete={this.props.onDelete}
            isActive={this.state.isFocused}>
        <Base.Content>
          <Item label="Online Event" offset="small">
            <Input onFocus={this.onFocus} onBlur={this.onBlur}
                   validateOnBlur={true} name={`${this.props.field.get('fieldPrefix')}.name`} placeholder="Event Name"/>
          </Item>
        </Base.Content>
      </Base>
    );
  }

}

Online.Static = Static;
export default Online;
