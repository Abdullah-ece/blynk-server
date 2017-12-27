import React from 'react';
import {Base} from '../../index';
import {Item, Input} from 'components/UI';
import {EVENT_TYPES} from 'services/Products';
import Static from './static';

class Online extends React.Component {

  static propTypes = {
    form: React.PropTypes.string,
    initialValues: React.PropTypes.object,
    onChange: React.PropTypes.func,
    onDelete: React.PropTypes.func,
    validate: React.PropTypes.func
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
      <Base type={EVENT_TYPES.ONLINE} form={this.props.form} initialValues={this.props.initialValues}
            onChange={this.props.onChange}
            validate={this.props.validate}
            onDelete={this.props.onDelete}
            isActive={this.state.isFocused}>
        <Base.Content>
          <Item label="Online Event" offset="small">
            <Input onFocus={this.onFocus} onBlur={this.onBlur}
                   validateOnBlur={true} name="name" placeholder="Event Name"/>
          </Item>
        </Base.Content>
      </Base>
    );
  }

}

Online.Static = Static;
export default Online;
