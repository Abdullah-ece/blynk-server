import React from "react";
import {Base} from "../../index";
import {Item, ItemsGroup} from "components/UI";
import {EVENT_TYPES} from "services/Products";
import _ from "lodash";
import FieldStub from "scenes/Products/components/FieldStub";

class Event extends React.Component {

  static propTypes = {
    type: React.PropTypes.string,
    fields: React.PropTypes.any,
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

  getPreviewProps() {
    return {
      isValid: true
    };
  }

  render() {

    return (
      <Base.Static type={this.props.type} fields={this.props.fields}>
        <Base.Content>
          <ItemsGroup>
            <Item label={this.getLabelForType(this.props.type)} offset="normal" style={{width: '55%'}}>
              <FieldStub>
                { this.props.fields.name}
              </FieldStub>
            </Item>
            <Item label="Event Code" offset="normal" style={{width: '45%'}}>
              <FieldStub>
                { this.props.fields.eventCode }
              </FieldStub>
            </Item>
          </ItemsGroup>
          <Item label="Description" offset="normal">
            <FieldStub multipleLines={true} noValueMessage="Empty">
              { this.props.fields.description && this.props.fields.description.split('\n').map((item, key) => {
                return (<span key={key}>{item}<br/></span>);
              })}
            </FieldStub>
          </Item>
        </Base.Content>
        <Base.Preview {...this.getPreviewProps()} valid={
          <Item label="Code Preview" offset="small">
            Blynk.logEvent({`"${this.props.fields.eventCode }"`});
          </Item>
        } invalid={
          <Item label="Code Preview" offset="small">
            <div className="product-metadata-item--preview--unavailable">No Preview available</div>
          </Item>
        }/>
      </Base.Static>
    );
  }

}

export default Event;
