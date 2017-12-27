import React from "react";
import {Base} from "../../index";
import {Input, Item, ItemsGroup} from "components/UI";
import {convertUserFriendlyEventCode, EVENT_TYPES} from "services/Products";
import _ from "lodash";
import Validation from "services/Validation";
import {change, formValueSelector, getFormMeta, getFormSyncErrors, getFormValues} from "redux-form";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import Static from "./static";

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      eventCode: selector(state, 'eventCode')
    },
    fieldsErrors: getFormSyncErrors(ownProps.form)(state),
    formValues: getFormValues(ownProps.form)(state),
    values: getFormMeta(ownProps.form)(state)
  };
}, (dispatch) => ({
  changeField: bindActionCreators(change, dispatch)
}))
class Event extends React.Component {

  static propTypes = {
    type: React.PropTypes.string,
    form: React.PropTypes.string,
    initialValues: React.PropTypes.object,
    onChange: React.PropTypes.func,
    onDelete: React.PropTypes.func,
    onClone: React.PropTypes.func,
    validate: React.PropTypes.func,
    changeField: React.PropTypes.func,
    anyTouched: React.PropTypes.bool,
    formValues: React.PropTypes.any,
    fields: React.PropTypes.any,
    fieldsErrors: React.PropTypes.any,
    values: React.PropTypes.any,
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

  shouldComponentUpdate(nextProps, nextState) {
    return this.state.isFocused !== nextState.isFocused || !(_.isEqual(this.props.formValues, nextProps.formValues)) || !(_.isEqual(this.props.fieldsErrors, nextProps.fieldsErrors));
  }

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
      <Base type={this.props.type} form={this.props.form} initialValues={this.props.initialValues}
            tools={true}
            onChange={this.props.onChange}
            onClone={this.props.onClone}
            validate={this.props.validate}
            onDelete={this.props.onDelete}
            isActive={this.state.isFocused}>
        <Base.Content>
          <ItemsGroup>
            <Item label={this.getLabelForType(this.props.type)} offset="normal">
              <Input onFocus={this.onFocus} onBlur={this.onBlur}
                     validateOnBlur={true} onChange={this.onNameChange} name="name" placeholder="Event Name"
                     style={{width: '55%'}}
                     validate={[Validation.Rules.required]}
                     className={`event-name-field-${this.props.initialValues.id}`}/>
            </Item>
            <Item label="Event Code" offset="normal">
              <Input onFocus={this.onFocus} onBlur={this.onBlur}
                     validateOnBlur={true} name="eventCode" placeholder="Event code" style={{width: '45%'}}
                     validate={[Validation.Rules.required, Validation.Rules.eventsEventCode]}/>
            </Item>
          </ItemsGroup>
          <Item label="Description" offset="small">
            <Input onFocus={this.onFocus} onBlur={this.onBlur}
                   name="description" type="textarea" placeholder="Event Description (optional)" rows="3"/>
          </Item>
        </Base.Content>
        <Base.Preview {...this.getPreviewProps()} valid={
          <Item label="Code Preview" offset="small">
            Blynk.logEvent("{ this.props.fields.eventCode }");
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
