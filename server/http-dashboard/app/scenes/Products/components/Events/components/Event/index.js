import React from 'react';
import {Base} from '../../index';
import {ItemsGroup, Item, Input} from 'components/UI';
import {EVENT_TYPES} from 'services/Products';
import _ from 'lodash';
import Validation from 'services/Validation';

class Event extends React.Component {

  static propTypes = {
    type: React.PropTypes.string,
    form: React.PropTypes.string,
    initialValues: React.PropTypes.object,
    onChange: React.PropTypes.func,
    onDelete: React.PropTypes.func,
    onClone: React.PropTypes.func,
    validate: React.PropTypes.func,
    anyTouched: React.PropTypes.bool
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
      <Base type={this.props.type} form={this.props.form} initialValues={this.props.initialValues}
            tools={true}
            onChange={this.props.onChange}
            onClone={this.props.onClone}
            validate={this.props.validate}
            onDelete={this.props.onDelete}>
        <Base.Content>
          <ItemsGroup>
            <Item label={this.getLabelForType(this.props.type)} offset="normal">
              <Input name="name" placeholder="Event Name" style={{width: '55%'}}
                     validate={[Validation.Rules.required]}/>
            </Item>
            <Item label="Event Code" offset="normal">
              <Input name="eventCode" placeholder="Event code" style={{width: '45%'}}
                     validate={[Validation.Rules.required, Validation.Rules.eventsEventCode]}/>
            </Item>
          </ItemsGroup>
          <Item label="Description" offset="small">
            <Input name="description" type="textarea" placeholder="Event Description (optional)" rows="3"/>
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
