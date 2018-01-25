import React from "react";
import {Base} from "../../index";
import {Input, Item, ItemsGroup} from "components/UI";
import {convertUserFriendlyEventCode, EVENT_TYPES} from "services/Products";
import _ from "lodash";
import Validation from "services/Validation";
import Static from "./static";
import PropTypes from 'prop-types';
import {Map} from 'immutable';
class Event extends React.Component {

  static propTypes = {
    field: PropTypes.instanceOf(Map),
    type: PropTypes.string,
    form: PropTypes.string,
    initialValues: PropTypes.object,
    onChange: PropTypes.func,
    onDelete: PropTypes.func,
    onClone: PropTypes.func,
    validate: PropTypes.func,
    changeField: PropTypes.func,
    anyTouched: PropTypes.bool,
    formValues: PropTypes.any,
    fields: PropTypes.any,
    fieldsErrors: PropTypes.any,
    values: PropTypes.any,
  };

  constructor(props) {
    super(props);

    this.onBlur = this.onBlur.bind(this);
    this.onFocus = this.onFocus.bind(this);
    this.onNameChange = this.onNameChange.bind(this);

    if (_.values(EVENT_TYPES).indexOf(props.type) === -1) {
      throw Error('Wrong props.type for Event');
    }
  }

  state = {
    isFocused: false
  };

  // shouldComponentUpdate(nextProps, nextState) {
  //   return this.state.isFocused !== nextState.isFocused || !(_.isEqual(this.props.formValues, nextProps.formValues)) || !(_.isEqual(this.props.fieldsErrors, nextProps.fieldsErrors));
  // }

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

  getPreviewProps() {
    const values = this.props.values;
    return {
      isValid: !(this.props.fieldsErrors && this.props.fieldsErrors.eventCode),
      isEmpty: !(this.props.fields.eventCode),
      isTouched: ( values && values.eventCode && values.eventCode.touched)
    };
  }

  onNameChange(event, newValue, oldValue) {
    const eventCode = this.props.fields.eventCode;
    if (!eventCode || convertUserFriendlyEventCode(oldValue) === eventCode) {
      this.props.changeField(this.props.form, 'eventCode', convertUserFriendlyEventCode(newValue));
    }
  }

  render() {

    return (
      <Base type={this.props.type}
            tools={true}
            onClone={this.props.onClone}
            onDelete={this.props.onDelete}>
        <Base.Content>
          <ItemsGroup>
            <Item label={this.getLabelForType(this.props.type)} offset="normal">
              <Input onFocus={this.onFocus} onBlur={this.onBlur}
                     validateOnBlur={true} onChange={this.onNameChange} name={`${this.props.field.get('fieldPrefix')}.name`} placeholder="Event Name"
                     style={{width: '55%'}}
                     validate={[Validation.Rules.required]}
                     className={`event-name-field-${this.props.field.get('id')}`}/>
            </Item>
            <Item label="Event Code" offset="normal">
              <Input onFocus={this.onFocus} onBlur={this.onBlur}
                     validateOnBlur={true} name={`${this.props.field.get('fieldPrefix')}.eventCode`} placeholder="Event code" style={{width: '45%'}}
                     validate={[Validation.Rules.required, Validation.Rules.eventsEventCode]}/>
            </Item>
          </ItemsGroup>
          <Item label="Description" offset="small">
            <Input onFocus={this.onFocus} onBlur={this.onBlur}
                   name={`${this.props.field.get('fieldPrefix')}.description`} type="textarea" placeholder="Event Description (optional)" rows="3"/>
          </Item>
        </Base.Content>
        <Base.Preview /*{...this.getPreviewProps()}*/ valid={
          <Item label="Code Preview" offset="small">
            Blynk.logEvent("{ this.props.field.get('eventCode') }");
          </Item>
        } invalid={
          <Item label="Code Preview" offset="small">
            <div className="product-metadata-item--preview--unavailable">No Preview available</div>
          </Item>
        }/>
      </Base>
    );
  }

}

Event.Static = Static;

export default Event;
