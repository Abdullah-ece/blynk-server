import React from 'react';
import {Button} from 'antd';
import {EVENT_TYPES} from 'services/Products';
import './styles.less';

class Add extends React.Component {

  static propTypes = {
    handleSubmit: React.PropTypes.func
  };

  render() {
    return (
      <div className="products-add-new-field">
        <div className="products-add-new-field-title">+ Add new Event:</div>
        <div className="products-add-new-field-fields">
          <Button type="dashed"
                  className="add-info-event-button"
                  onClick={this.props.handleSubmit.bind({}, EVENT_TYPES.INFO)}>
            Info Event
          </Button>
          <Button type="dashed"
                  className="add-warning-event-button"
                  onClick={this.props.handleSubmit.bind({}, EVENT_TYPES.WARNING)}>
            Warning Event
          </Button>
          <Button type="dashed"
                  className="add-critical-event-button"
                  onClick={this.props.handleSubmit.bind({}, EVENT_TYPES.CRITICAL)}>
            Critical Event
          </Button>
        </div>
      </div>
    );
  }

}

export default Add;
